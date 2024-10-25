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

public class AchievementHelper {

    private static final String USERS_COLLECTION = "users";
    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSession userSession = UserSession.getInstance();


    public void storeAchievements(Context context, String email, String title, String description) {
        // Reference to the user's document in Firestore
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(email);

        // Add the achievement to the local user session
        userSession.addAchievement(title, description);

        // Create an achievement map
        Map<String, Object> achievement = new HashMap<>();
        achievement.put("title", title);
        achievement.put("description", description);

        // Option 1: Add a new achievement array field in Firestore
        userRef.update("achievements", FieldValue.arrayUnion(achievement))
                .addOnSuccessListener(aVoid -> {
                    // Achievement successfully added to Firestore
                    showToast(context,"Achievement successfully added!");
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    showToast(context, e.getMessage());
                });
    }

    // Fetch achievements from Firestore
    public ListenerRegistration getAchievements(String email, InterfaceHelper.OnAchievementsFetchedListener listener) {
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(email);
        return userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                listener.onError(e.getMessage());
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                List<Map<String, String>> achievements = (List<Map<String, String>>) documentSnapshot.get("achievements");
                if (achievements == null) {
                    achievements = new ArrayList<>();
                }
                listener.onAchievementsFetched(achievements);
            }
        });
    }

    public void deleteAchievement(String userEmail, String title, String description, InterfaceHelper.OnAchievementDeletedListener listener) {
        // Get instance of Firestore
        DocumentReference userDoc = db.collection(USERS_COLLECTION).document(userEmail);

        // Read the user document to find the achievements array
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, String>> achievements = (List<Map<String, String>>) document.get("achievements");
                    if (achievements != null) {
                        // Find the achievement to delete
                        boolean deleted = false;
                        for (Map<String, String> achievement : achievements) {
                            String achievementTitle = achievement.get("title");
                            String achievementDescription = achievement.get("description");

                            // Check if the current achievement matches the one to delete
                            if (achievementTitle.equals(title) && achievementDescription.equals(description)) {
                                achievements.remove(achievement);
                                deleted = true;
                                break;
                            }
                        }

                        // Update the user document if an achievement was deleted
                        if (deleted) {
                            userDoc.update("achievements", achievements)
                                    .addOnSuccessListener(aVoid -> listener.onAchievementDeleted())
                                    .addOnFailureListener(e -> listener.onError("Error deleting achievement"));
                        } else {
                            listener.onError("Achievement not found");
                        }
                    } else {
                        listener.onError("No achievements found");
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
