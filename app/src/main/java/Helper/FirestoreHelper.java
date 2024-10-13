package Helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.memoria.Home;
import com.example.memoria.Signin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class FirestoreHelper {

    private static final String USERS_COLLECTION = "users";
    private static final String CLASSROOMS_COLLECTION = "classrooms"; // New collection for classrooms
    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSession userSession = UserSession.getInstance();

    // Check if the user exists
    public void checkUserExists(final Context context, final String email, final String name, final String password, final String classVal, final String academicYear, String classroomId) {
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
                    registerUser(context, email, name, password, classVal, academicYear, classroomId);
                }
            } else {
                handleError(context, task.getException(), "Failed to check if user exists");
            }
        });
    }

    // Register a new user with class list details
    private void registerUser(final Context context, final String email, final String name, final String password, final String classVal, final String academicYear, String classroomId) {

        Map<String, Object> user = createUserDataMap(name, email, password, classVal, academicYear, classroomId);

        userSession.clearClassList();
        db.collection(USERS_COLLECTION).document(email)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    showToast(context, "User registered successfully");
                    setUserData(email, name, classVal, academicYear, classroomId);
                    // Store classroomId in classrooms collection
                    storeClassroomInClassrooms(email, classroomId, classVal, academicYear);
                    Intent intent = new Intent(context, Signin.class);
                    context.startActivity(intent);
                })
                .addOnFailureListener(e -> handleError(context, e, "Registration failed"));
    }

    // Store classroom details in classrooms collection
    private void storeClassroomInClassrooms(String email, String classroomId, String classVal, String academicYear) {
        Map<String, Object> classroomData = new HashMap<>();
        classroomData.put("classroomId", classroomId);
        classroomData.put("email", email);
        classroomData.put("classVal", classVal);
        classroomData.put("academicYear", academicYear);

        db.collection(CLASSROOMS_COLLECTION).document(classroomId)
                .set(classroomData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Classroom added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add classroom", e));
    }

    // Create a map of user data, with class list as an array of objects
    private Map<String, Object> createUserDataMap(String name, String email, String password, String classVal, String academicYear, String classroomId) {
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
        classDetails.put("classroomId", classroomId);
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
                                String classroomId = (String) classDetails.get("classroomId");

                                // Add class details to the UserSession
                                userSession.addClassToList(userClass, userAcademicYear, classroomId);
                            }

                            // Optionally, set the first class details as current class
                            Map<String, Object> firstClassDetails = classList.get(0);
                            userSession.setClassDetails((String) firstClassDetails.get("classVal"),
                                    (String) firstClassDetails.get("academicYear"), (String) firstClassDetails.get("classroomId"));
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
                            updatedUser.put("classList", classList);

                            // Update user data in Firestore
                            db.collection(USERS_COLLECTION).document(userEmail).set(updatedUser)
                                    .addOnSuccessListener(aVoid -> {
                                        showToast(context, "User data updated successfully");
                                        userSession.setUserName(name); // Update the session
                                    })
                                    .addOnFailureListener(e -> handleError(context, e, "Failed to update user data"));
                        } else {
                            showToast(context, "User does not exist");
                        }
                    } else {
                        handleError(context, task.getException(), "Error fetching user data");
                    }
                });
    }


    public void addClassToUserDatabase(String email, String classVal, String academicYear, String classroomId, OnCompleteListener<Object> listener) {
        // Create a map with the class details
        Map<String, Object> classDetails = new HashMap<>();
        classDetails.put("classVal", classVal);
        classDetails.put("academicYear", academicYear);
        classDetails.put("classroomId", classroomId);

        // Reference to the user's document
        DocumentReference userDocRef = db.collection("users").document(email);

        // Use a Firestore transaction to ensure atomic updates
        db.runTransaction(transaction -> {
            // Get the user's document
            DocumentSnapshot userDoc = transaction.get(userDocRef);

            // Get the existing classList or create a new one
            List<Map<String, Object>> classList;
            if (userDoc.exists() && userDoc.contains("classList")) {
                classList = (List<Map<String, Object>>) userDoc.get("classList");
            } else {
                classList = new ArrayList<>();
            }

            // Check if the class already exists in the list
            boolean classExists = classList.stream().anyMatch(classItem ->
                    classItem.get("classroomId").equals(classroomId) ||
                            (classItem.get("classVal").equals(classVal) && classItem.get("academicYear").equals(academicYear))
            );

            // If the class does not exist, add it
            if (!classExists) {
                classList.add(classDetails);
                // Update the user's classList in Firestore
                transaction.update(userDocRef, "classList", classList);
            } else {
                // If the class exists, we can choose to do nothing or return a specific value
                // (no need to update the transaction)
                return null; // No change needed
            }

            return null; // Transaction completed successfully
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete(task); // Notify listener upon success
            } else {
                listener.onComplete(task); // Notify listener upon failure
            }
        });
    }



    //check class exists or not
    public void checkClassExistsAndFetchIds(String classVal, String academicYear, BiConsumer<Boolean, List<String>> callback) {
        // Query Firestore to check if the class exists
        db.collection("classrooms")
                .whereEqualTo("classVal", classVal)
                .whereEqualTo("academicYear", academicYear)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // If exists, fetch the classroom IDs
                        List<String> classroomIds = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Collect all matching classroom IDs
                            classroomIds.add(document.getId()); // Assuming document ID is the classroom ID
                        }

                        // If we found classroom IDs, return true with the IDs
                        if (!classroomIds.isEmpty()) {
                            callback.accept(true, classroomIds);
                        } else {
                            callback.accept(false, Collections.emptyList());
                        }
                    } else {
                        // Class does not exist
                        callback.accept(false, Collections.emptyList());
                    }
                });
    }
    // Method to get classroom details by classroomId
    public void getClassroomDetails(String classroomId, ClassroomDetailsCallback callback) {
        db.collection(CLASSROOMS_COLLECTION)
                .document(classroomId) // Search the document with the classroomId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Create a map of class details to return
                            Map<String, String> classroomDetails = new HashMap<>();
                            classroomDetails.put("classVal", document.getString("classVal"));
                            classroomDetails.put("academicYear", document.getString("academicYear"));

                            // Callback with classroom details
                            callback.onCallback(classroomDetails);
                        } else {
                            // If the document doesn't exist, return null
                            callback.onCallback(null);
                        }
                    } else {
                        Log.e(TAG, "Error fetching classroom details", task.getException());
                        callback.onCallback(null);
                    }
                });
    }

    // Interface for the callback to handle the response
    public interface ClassroomDetailsCallback {
        void onCallback(Map<String, String> classroomDetails);
    }


    //creating a new class
    public void createNewClass(final Context context, final String classVal, final String academicYear, final String classroomId) {
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
                            newClass.put("classroomId", classroomId);

                            // Add the new class to the list
                            classList.add(newClass);

                            // Update the user's document in Firestore with the new class list
                            db.collection(USERS_COLLECTION).document(userEmail)
                                    .update("classList", classList)
                                    .addOnSuccessListener(aVoid -> {
                                        showToast(context, "New class added successfully");
                                        userSession.addClassToList(classVal, academicYear, classroomId);
                                        // Store the classroom information in the classrooms collection
                                        storeClassroomInClassrooms(userEmail, classroomId, classVal, academicYear);
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

    // Interface for the callback to handle the response
    public interface ClassDetailsCallback {
        void onCallback(Map<String, String> classDetails);
    }

    // Method to get class details by classroomId
    public void getClassDetails(String classroomId, ClassDetailsCallback callback) {
        db.collection("classes")
                .document(classroomId) // Search the document with the classroomId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Create a map of class details to return
                            Map<String, String> classDetails = new HashMap<>();
                            classDetails.put("classVal", document.getString("classVal"));
                            classDetails.put("academicYear", document.getString("academicYear"));

                            // Callback with class details
                            callback.onCallback(classDetails);
                        } else {
                            // If the document doesn't exist, return null
                            callback.onCallback(null);
                        }
                    } else {
                        Log.e("FirestoreHelper", "Error fetching class details", task.getException());
                        callback.onCallback(null);
                    }
                });
    }
    //set userdata for display
    private void setUserData(final String email, final String name, final String classVal, final String academicYear, final String classroomId){
        UserSession userSession = UserSession.getInstance();
        userSession.setUserName(name);
        userSession.setUserEmail(email);
        userSession.setClassDetails(classVal, academicYear,classroomId);
    }

}
