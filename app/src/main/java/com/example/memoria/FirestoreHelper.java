package com.example.memoria;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {

    private static final String USERS_COLLECTION = "users";
    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSession userSession = UserSession.getInstance();


    // Check if the user exists
    public void checkUserExists(final Context context, final String email, final String name, final String password, final String classVal, final String academicYear) {
        if (email == null || email.isEmpty()) {
            showToast(context, "Email cannot be null or empty");
            return; // Prevent proceeding with null or empty email
        }

        db.collection(USERS_COLLECTION).document(email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    showToast(context, "User already exists");
                } else {
                    registerUser(context, email, name, password, classVal, academicYear);
                }
            } else {
                handleError(context, task.getException(), "Failed to check if user exists");
            }
        });
    }

    // Register a new user with class list details
    private void registerUser(final Context context, final String email, final String name, final String password, final String classVal, final String academicYear) {
        Map<String, Object> user = createUserDataMap(name, email, password, classVal, academicYear);

        userSession.clearClassList();
        db.collection(USERS_COLLECTION).document(email)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    showToast(context, "User registered successfully");
                    setUserData(email, name,classVal, academicYear);
                    Intent intent = new Intent(context, Home.class);
                    context.startActivity(intent);
                })
                .addOnFailureListener(e -> handleError(context, e, "Registration failed"));
    }

    // Create a map of user data, with class list as an array of objects
    private Map<String, Object> createUserDataMap(String name, String email, String password, String classVal, String academicYear) {
        Map<String, Object> user = new HashMap<>();

        // Basic user details
        user.put("name", name);
        user.put("email", email);
        user.put("password", password);

        // Class list as an array of objects
        List<Map<String, Object>> classList = new ArrayList<>();
        Map<String, Object> classDetails = new HashMap<>();
        classDetails.put("classVal", classVal);
        classDetails.put("academicYear", academicYear);
        classList.add(classDetails);

        // Add classList to user data
        user.put("classList", classList);

        return user;
    }

    // Show a toast message
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Handle errors
    private void handleError(Context context, Exception e, String message) {
        showToast(context, message);
        Log.w(TAG, message, e);
    }

    // Sign in the user
    public void signInUser(final Context context, final String email, final String password) {
        if (email == null || email.isEmpty()) {
            showToast(context, "Email cannot be null or empty");
            return; // Prevent proceeding with null or empty email
        }

        userSession.clearClassList();
        db.collection(USERS_COLLECTION).document(email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String storedPassword = document.getString("password");
                    if (password.equals(storedPassword)) {
                        // Password matches, fetch the user's details
                        String userName = document.getString("name");
                        String userEmail = document.getString("email");
                        List<Map<String, Object>> classList = (List<Map<String, Object>>) document.get("classList");

                        // Store user data in UserSession
                        userSession.setUserName(userName);
                        userSession.setUserEmail(userEmail);

                        if (classList != null && !classList.isEmpty()) {
                            // Fetch and store all class details
                            for (Map<String, Object> classDetails : classList) {
                                String userClass = (String) classDetails.get("classVal");
                                String userAcademicYear = (String) classDetails.get("academicYear");

                                // Add class details to the UserSession
                                userSession.addClassToList(userClass, userAcademicYear);
                            }

                            // Optionally, set the first class details as current class
                            Map<String, Object> firstClassDetails = classList.get(0);
                            userSession.setClassDetails((String) firstClassDetails.get("classVal"),
                                    (String) firstClassDetails.get("academicYear"));
                        }

                        // Redirect to Home
                        Intent intent = new Intent(context, Home.class);
                        context.startActivity(intent);
                    } else {
                        showToast(context, "Invalid password");
                    }
                } else {
                    showToast(context, "User does not exist");
                }
            } else {
                showToast(context, "Error checking user");
            }
        });
    }

    // Update user data
    public void updateUserData(final Context context, final String email, final String name, final String classVal) {
        UserSession user = UserSession.getInstance();

        String userEmail = user.getUserEmail(); // Use the email stored in the singleton

        if (userEmail == null || userEmail.isEmpty()) {
            showToast(context, "User email cannot be null or empty");
            return; // Prevent proceeding with null or empty userEmail
        }

        // Check if the user exists
        db.collection(USERS_COLLECTION).document(userEmail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // User exists, retrieve the current classList
                            List<Map<String, Object>> classList = (List<Map<String, Object>>) document.get("classList");
                            if (classList == null) {
                                classList = new ArrayList<>(); // Initialize if null
                            }

                            boolean classExists = false;
                            String academicYear = user.getAcademicYear();

                            // Check if the class already exists and update it
                            for (Map<String, Object> classDetails : classList) {
                                if (academicYear.equals(classDetails.get("academicYear"))) {
                                    classDetails.put("classVal", classVal);
                                    classExists = true;
                                    break;
                                }
                            }

                            // If the class does not exist, add a new entry
                            if (!classExists) {
                                Map<String, Object> newClassDetails = new HashMap<>();
                                newClassDetails.put("classVal", classVal);
                                newClassDetails.put("academicYear", academicYear);
                                classList.add(newClassDetails);
                            }

                            // Prepare the updated user data
                            Map<String, Object> updatedUser = new HashMap<>();
                            updatedUser.put("name", name);
                            updatedUser.put("email", email);
                            updatedUser.put("classList", classList);

                            // Update the user's data in Firestore
                            db.collection(USERS_COLLECTION).document(userEmail)
                                    .update(updatedUser)
                                    .addOnSuccessListener(aVoid -> {
                                        showToast(context, "User data updated successfully");
                                        // Update the user data in the singleton
                                        user.setUserName(name);
                                        user.setUserEmail(email);
                                        user.setClassDetails(classVal, academicYear);

                                    })
                                    .addOnFailureListener(e -> handleError(context, e, "Failed to update user data"));
                        } else {
                            // User does not exist
                            showToast(context, "User does not exist");
                        }
                    } else {
                        // Error checking if the user exists
                        handleError(context, task.getException(), "Error checking user existence");
                    }
                });
    }

    public void createNewClass(final Context context, final String classVal, final String academicYear) {
        UserSession userSession = UserSession.getInstance();
        String userEmail = userSession.getUserEmail(); // Get the user's email from the session

        if (userEmail == null || userEmail.isEmpty()) {
            showToast(context, "User email cannot be null or empty");
            return; // Prevent proceeding if the email is null or empty
        }

        // Fetch the current user's data from Firestore
        db.collection(USERS_COLLECTION).document(userEmail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // User exists, get the current class list
                            List<Map<String, Object>> classList = (List<Map<String, Object>>) document.get("classList");
                            if (classList == null) {
                                classList = new ArrayList<>(); // Initialize if class list is null
                            }

                            // Create a new class object
                            Map<String, Object> newClass = new HashMap<>();
                            newClass.put("classVal", classVal);
                            newClass.put("academicYear", academicYear);

                            // Add the new class to the list
                            classList.add(newClass);

                            // Update the user's document in Firestore with the new class list
                            db.collection(USERS_COLLECTION).document(userEmail)
                                    .update("classList", classList)
                                    .addOnSuccessListener(aVoid -> {
                                        showToast(context, "New class added successfully");
                                        // Optionally update the user session
                                        userSession.addClassToList(classVal, academicYear);
                                    })
                                    .addOnFailureListener(e -> handleError(context, e, "Failed to update class list"));
                        } else {
                            showToast(context, "User does not exist");
                        }
                    } else {
                        handleError(context, task.getException(), "Error fetching user data");
                    }
                });
    }


    // Example method to get the current user's ID
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    //set userdata for display
    private void setUserData(final String email, final String name, final String classVal, final String academicYear){
        UserSession userSession = UserSession.getInstance();
        userSession.setUserName(name);
        userSession.setUserEmail(email);
        userSession.setClassDetails(classVal, academicYear);
    }
}
