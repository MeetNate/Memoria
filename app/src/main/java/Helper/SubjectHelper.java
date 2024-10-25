package Helper;

import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectHelper {
    private static final String USERS_COLLECTION = "users";
    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSession userSession = UserSession.getInstance();


    public void storeSubject(Context context, String email, String subjectName, String facultyName) {
        // Reference to the user's document in Firestore
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(email);

        // Add the sport to the local user session (if applicable)
        userSession.addSport(subjectName, facultyName); // Ensure you have this method in your user session

        // Create a sport map
        Map<String, Object> subject = new HashMap<>();
        subject.put("subjectName", subjectName);
        subject.put("facultyName", facultyName);

        // Add the sport to the Firestore document as an array
        userRef.update("subjects", FieldValue.arrayUnion(subject))
                .addOnSuccessListener(aVoid -> {
                    // Sport successfully added to Firestore
                    showToast(context,"Data successfully added!");
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    showToast(context, e.getMessage());
                });
    }


    // New function to fetch subjects
    public ListenerRegistration getSubjects(String email, InterfaceHelper.OnSubjectsFetchedListener listener) {
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(email);
        return userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                listener.onError(e.getMessage());
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                List<Map<String, String>> subjects = (List<Map<String, String>>) documentSnapshot.get("subjects");
                if (subjects == null) {
                    subjects = new ArrayList<>();
                }
                listener.onSubjectsFetched(subjects);
            }
        });
    }


    public void deleteSubject(String userEmail, String subjectName, String teacherName, InterfaceHelper.OnSubjectDeletedListener listener) {
        DocumentReference userDoc = db.collection(USERS_COLLECTION).document(userEmail);

        // Retrieve and update the subject list
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, String>> subjects = (List<Map<String, String>>) document.get("subjects");
                    if (subjects != null) {
                        boolean deleted = false;
                        for (Map<String, String> subject : subjects) {
                            if (subject.get("subjectName").equals(subjectName) && subject.get("facultyName").equals(teacherName)) {
                                subjects.remove(subject);
                                deleted = true;
                                break;
                            }
                        }
                        if (deleted) {
                            userDoc.update("subjects", subjects)
                                    .addOnSuccessListener(aVoid -> listener.onSubjectDeleted())
                                    .addOnFailureListener(e -> listener.onError("Error deleting subject"));
                        } else {
                            listener.onError("Subject not found");
                        }
                    } else {
                        listener.onError("No subjects found");
                    }
                } else {
                    listener.onError("User document does not exist");
                }
            } else {
                listener.onError("Error retrieving user document");
            }
        });
    }
    // Show a toast message
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
