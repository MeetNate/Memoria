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

public class ResultHelper {
    private static final String USERS_COLLECTION = "users";
    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSession userSession = UserSession.getInstance();


    public void storeResult(Context context, String email, String Grade, Double Percentage){
        // Reference to the user's document in Firestore
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(email);

        // Add the sport to the local user session (if applicable)
        userSession.addResult(Grade, Percentage); // Ensure you have this method in your user session

        // Create a sport map
        Map<String, Object> result = new HashMap<>();
        result.put("grade", Grade);
        result.put("percentage", Percentage);

        // Add the sport to the Firestore document as an array
        userRef.update("results", FieldValue.arrayUnion(result))
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
    public ListenerRegistration getResults(String email, InterfaceHelper.OnResultsFetchedListener listener) {
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(email);
        return userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                listener.onError(e.getMessage());
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Safely cast the fetched results to a List of Maps with Object values
                List<Map<String, Object>> rawResults = (List<Map<String, Object>>) documentSnapshot.get("results");

                List<Map<String, Object>> results = new ArrayList<>();

                if (rawResults != null) {
                    for (Map<String, Object> rawResult : rawResults) {
                        Map<String, Object> result = new HashMap<>();

                        // Handle each entry in the rawResult map
                        for (Map.Entry<String, Object> entry : rawResult.entrySet()) {
                            if (entry.getKey().equals("grade")) {
                                // Grade is expected to be a String
                                result.put("grade", (String) entry.getValue());
                            } else if (entry.getKey().equals("percentage") && entry.getValue() instanceof Number) {
                                // Percentage should be converted to a Double
                                result.put("percentage", ((Number) entry.getValue()).doubleValue());
                            }
                        }
                        results.add(result);  // Add the processed result to the list
                    }
                }

                // Pass the correctly formatted results to the listener
                listener.onResultsFetched(results);
            } else {
                // If no results are found, return an empty list
                listener.onResultsFetched(new ArrayList<>());
            }
        });
    }

    public void deleteResult(String userEmail, String grade, double percentage, InterfaceHelper.OnResultDeletedListener listener) {
        // Get instance of Firestore
        DocumentReference userDoc = db.collection(USERS_COLLECTION).document(userEmail);

        // Read the user document to find the results array
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> results = (List<Map<String, Object>>) document.get("results");
                    if (results != null) {
                        // Find the result to delete
                        boolean deleted = false;
                        for (Map<String, Object> result : results) {
                            String resultGrade = (String) result.get("grade");
                            double resultPercentage = (double) result.get("percentage");

                            if (resultGrade.equals(grade) && resultPercentage == percentage) {
                                results.remove(result);
                                deleted = true;
                                break;
                            }
                        }

                        // Update the user document if a result was deleted
                        if (deleted) {
                            userDoc.update("results", results)
                                    .addOnSuccessListener(aVoid -> listener.onResultDeleted())
                                    .addOnFailureListener(e -> listener.onError("Error deleting result"));
                        } else {
                            listener.onError("Result not found");
                        }
                    } else {
                        listener.onError("No results found");
                    }
                } else {
                    listener.onError("User document does not exist");
                }
            } else {
                listener.onError("Error retrieving user document");
            }
        });
    }
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
