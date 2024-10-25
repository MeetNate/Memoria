package Helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import Model.Message;

public class ClassroomHelper {
    private static final String USERS_COLLECTION = "users";
    private static final String CLASSROOMS_COLLECTION = "classrooms";
    private static final String TAG = "ClassroomHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MessageHelper messageHelper = new MessageHelper();


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

                            // Initialize an empty list for messages if needed
                            List<Message> messages = new ArrayList<>();

                            // Add the new class to the list
                            classList.add(newClass);

                            // Update the user's document in Firestore with the new class list
                            List<Map<String, Object>> finalClassList = classList;
                            db.collection(USERS_COLLECTION).document(userEmail)
                                    .update("classList", classList)
                                    .addOnSuccessListener(aVoid -> {
                                        showToast(context, "New class added successfully");
                                        userSession.addClassToList(classVal, academicYear, classroomId);

                                        // Check if the classList has any classes before setting session details
                                        if (finalClassList.isEmpty()) {
                                            userSession.setClassDetails(classVal, academicYear, classroomId);// Only set if classList is not empty
                                        }

                                        // Store the classroom information in the classrooms collection
                                        storeClassroomInClassrooms(classroomId, classVal, academicYear, messages);
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


    public void checkForExistingClassroom(final Context context, String classVal, String academicYear, InterfaceHelper.OnClassroomCheckListener listener) {
        db.collection(CLASSROOMS_COLLECTION)
                .whereEqualTo("classVal", classVal)
                .whereEqualTo("academicYear", academicYear)
                .limit(1) // Limit to one result for efficiency
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // If classroom exists, get its ID
                        DocumentSnapshot existingClassroom = task.getResult().getDocuments().get(0);
                        String existingClassroomId = existingClassroom.getId();
                        listener.onClassroomCheck(existingClassroomId);
                    } else {
                        listener.onClassroomCheck(null); // No existing classroom found
                    }
                });
    }



    // Method to get classroom details by classroomId
    public void getClassroomDetails(String classroomId, InterfaceHelper.ClassroomDetailsCallback callback) {
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


    // Update classroom data in collection
    public void updateClassroomDataInCollection(String classroomId, String classVal, String academicYear, List<Message> messages, Context context) {
        // Prepare the updated classroom data
        Map<String, Object> updatedClassroomData = new HashMap<>();
        updatedClassroomData.put("classVal", classVal);
        updatedClassroomData.put("academicYear", academicYear);
        updatedClassroomData.put("classroomId", classroomId);

        // Check if messages list is not null or empty, otherwise, initialize an empty list
        List<Message> messageList = (messages != null) ? messages : new ArrayList<>();
        updatedClassroomData.put("messages", messageList);

        // Update the classroom in Firestore
        db.collection(CLASSROOMS_COLLECTION)
                .document(classroomId)
                .set(updatedClassroomData, SetOptions.merge())  // Merge to update the existing document without overriding other fields
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Classroom data updated successfully"))
                .addOnFailureListener(e -> handleError(context, e, "Failed to update classroom data"));
    }

    // Store classroom details in classrooms collection
    public void storeClassroomInClassrooms(String classroomId, String classVal, String academicYear, List<Message> messages) {
        // Prepare classroom data
        Map<String, Object> classroomData = new HashMap<>();
        classroomData.put("classroomId", classroomId);
        classroomData.put("classVal", classVal);
        classroomData.put("academicYear", academicYear);

        // Initialize an empty message list
        classroomData.put("messages", new ArrayList<>()); // Create an empty list for messages

        // Store the classroom data in Firestore
        db.collection(CLASSROOMS_COLLECTION).document(classroomId)
                .set(classroomData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Classroom added successfully");
                    // After successfully adding classroom, store messages
                    messageHelper.storeMessages(classroomId, messages);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add classroom", e));
    }


    // Handle errors
    private void handleError(Context context, Exception e, String message) {
        showToast(context, message);
        Log.w(TAG, message, e);
    }

    // Show a toast message
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}