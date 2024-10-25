package Helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.memoria.Home;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserHelper {

    private static final String USERS_COLLECTION = "users";
    private static final String TAG = "UserHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserSession userSession = UserSession.getInstance();
    private ClassroomHelper classroomHelper = new ClassroomHelper();
    // Show a toast message
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Handle errors
    private void handleError(Context context, Exception e, String message) {
        Log.e(TAG, message, e);
        showToast(context, message);
    }

    // Create a map of user data
    private Map<String, Object> createUserDataMap(String name, String email, String password) {
        Map<String, Object> user = new HashMap<>();

        // Basic user details
        user.put("name", name);
        user.put("email", email);
        user.put("password", password);

        return user;
    }

    // Set user data in the session
    private void setUserData(final String email, final String name){
        UserSession userSession = UserSession.getInstance();
        userSession.setUserName(name);
        userSession.setUserEmail(email);
    }

    // Check if the user exists
    public void checkUserExists(final Context context, final String email, final String name, final String password) {
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
                    registerUser(context, email, name, password);
                }
            } else {
                handleError(context, task.getException(), "Failed to check if user exists");
            }
        });
    }

    // Register a new user
    private void registerUser(final Context context, final String email, final String name, final String password) {
        // Create user data map
        Map<String, Object> user = createUserDataMap(name, email, password);

        userSession.clearClassList();

        // Register the user in Firestore
        db.collection(USERS_COLLECTION).document(email)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    showToast(context, "User registered successfully");
                    setUserData(email, name);

                    // Start the Signin activity
                    Intent intent = new Intent(context, Home.class);
                    context.startActivity(intent);
                })
                .addOnFailureListener(e -> handleError(context, e, "Registration failed"));
    }

    // Sign in the user
    public void signInUser(final Context context, final String email, final String password) {
        if (email == null || email.isEmpty()) {
            showToast(context, "Email cannot be null or empty");
            return; // Prevent proceeding with null or empty email
        }

        userSession.clearClassList(); // Clear previous classes if necessary
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
    public void updateUserData(final Context context, final String email, final String name, final String classVal, final String academicYear) {
        String userEmail = userSession.getUserEmail();

        if (userEmail == null || userEmail.isEmpty()) {
            showToast(context, "User email cannot be null or empty");
            return;
        }

        // Check if the user exists
        db.collection(USERS_COLLECTION).document(userEmail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // User exists, retrieve the current classList
                            List<Map<String, Object>> classList = (List<Map<String, Object>>) document.get("classList");
                            classList = (classList != null) ? classList : new ArrayList<>(); // Initialize if null

                            // If classList is empty, add a new entry at index 0
                            if (classList.isEmpty()) {
                                Map<String, Object> newClassDetails = new HashMap<>();
                                newClassDetails.put("classVal", classVal);
                                newClassDetails.put("academicYear", academicYear);
                                classList.add(0, newClassDetails); // Insert new class at 0th index
                            } else {
                                // Always update the 0th index
                                Map<String, Object> classDetails = classList.get(0);
                                classDetails.put("classVal", classVal); // Update the class value
                                classDetails.put("academicYear", academicYear); // Update the academic year
                            }

                            // Check for existing classroom ID
                            List<Map<String, Object>> finalClassList = classList;
                            classroomHelper.checkForExistingClassroom(context, classVal, academicYear, (existingClassroomId) -> {
                                String classroomIdToStore;

                                if (existingClassroomId != null) {
                                    classroomIdToStore = existingClassroomId; // Use existing classroom ID
                                } else {
                                    classroomIdToStore = generateUniqueClassroomId(classVal, academicYear); // Generate new classroom ID
                                }

                                // Update the classroom ID at the 0th index
                                finalClassList.get(0).put("classroomId", classroomIdToStore); // Store the classroom ID with classVal and academicYear

                                // Prepare the fields to be updated
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("name", name);
                                updates.put("classList", finalClassList);

                                // Only update email if it's different
                                if (!userEmail.equals(email)) {
                                    updates.put("email", email); // Update email
                                }

                                // Update user data in Firestore
                                db.collection(USERS_COLLECTION).document(userEmail).update(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            showToast(context, "User data updated successfully");
                                            userSession.setUserName(name); // Update the session with the new name
                                            userSession.setUserEmail(email); // Update the session with the new email

                                            userSession.setClassDetails(classVal, academicYear, classroomIdToStore);
                                            userSession.updateFirstClassInList(classVal, academicYear,classroomIdToStore);

                                            // Update the classroom ID in classrooms collection
                                            classroomHelper.updateClassroomDataInCollection(classroomIdToStore, classVal, academicYear, new ArrayList<>(), context);
                                        })
                                        .addOnFailureListener(e -> handleError(context, e, "Failed to update user data"));
                            });
                        } else {
                            showToast(context, "User does not exist");
                        }
                    } else {
                        handleError(context, task.getException(), "Error fetching user data");
                    }
                });
    }


    // Method to generate a unique classroom ID using classVal and academicYear
    private String generateUniqueClassroomId(String classVal, String academicYear) {
        String uuidPart = UUID.randomUUID().toString().substring(0, 8); // Generate a short UUID
        return classVal + "_" + academicYear + "_" + uuidPart; // Combine classVal, academicYear, and UUID part
    }
}
