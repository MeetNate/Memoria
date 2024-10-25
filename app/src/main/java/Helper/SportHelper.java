package Helper;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SportHelper {

    private static final String USERS_COLLECTION = "users";
    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSession userSession = UserSession.getInstance();


    public void storeSports(Context context, String email, String sportName, String sportDescription) {
        // Reference to the user's document in Firestore
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(email);

        // Add the sport to the local user session (if applicable)
        userSession.addSport(sportName, sportDescription); // Ensure you have this method in your user session

        // Create a sport map
        Map<String, Object> sport = new HashMap<>();
        sport.put("name", sportName);
        sport.put("description", sportDescription);

        // Add the sport to the Firestore document as an array
        userRef.update("sports", FieldValue.arrayUnion(sport))
                .addOnSuccessListener(aVoid -> {
                    // Sport successfully added to Firestore
                    showToast(context,"Activity successfully added!");
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    showToast(context, e.getMessage());
                });
    }

    public ListenerRegistration getSports(String email, InterfaceHelper.OnSportsFetchedListener listener){
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(email);
        return userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                listener.onError(e.getMessage());
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                List<Map<String, String>> sports = (List<Map<String, String>>) documentSnapshot.get("sports");
                if (sports == null) {
                    sports = new ArrayList<>();
                }
                listener.onSportsFetched(sports);
            }
        });
    }

    public void deleteSports(String email, String title, String description, InterfaceHelper.OnSportDeletedListener listener) {
        DocumentReference userDoc = db.collection(USERS_COLLECTION).document(email);

        // Retrieve and update the sports list
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, String>> sportsList = (List<Map<String, String>>) document.get("sports");
                    if (sportsList != null) {
                        boolean deleted = false;
                        for (Map<String, String> sport : sportsList) {
                            if (sport.get("name").equals(title) && sport.get("description").equals(description)) {
                                sportsList.remove(sport);
                                deleted = true;
                                break;
                            }
                        }
                        if (deleted) {
                            userDoc.update("sports", sportsList)
                                    .addOnSuccessListener(aVoid -> listener.onSportDeleted())
                                    .addOnFailureListener(e -> listener.onError("Error deleting sport"));
                        } else {
                            listener.onError("Sport not found");
                        }
                    } else {
                        listener.onError("No sports found");
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
