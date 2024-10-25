package Helper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Model.Memory;

public class MemoryHelper {
    private static final String USERS_COLLECTION = "users";
    private static final String CLASSROOMS_COLLECTION = "classrooms"; // New collection for classrooms
    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public void saveMemory(Context context, String email, String memoryName, String memoryCaption, Uri imageUri) {
        // Reference to the user's document in Firestore
        DocumentReference userRef = db.collection(USERS_COLLECTION).document(email);

        // Create a Memory object to hold the data
        Memory memory = new Memory(memoryName, memoryCaption, imageUri.toString()); // Assuming Memory constructor takes these parameters


        // Add the memory to Firestore
        userRef.update("memories", FieldValue.arrayUnion(memory))
                .addOnSuccessListener(aVoid -> {
                    // Memory successfully added to Firestore
                    showToast(context, "Memory successfully added!");
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    showToast(context, "Error adding memory: " + e.getMessage());
                });
    }

    public void getMemories(String email, InterfaceHelper.OnMemoriesFetchListener listener) {
        db.collection(USERS_COLLECTION).document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> memoriesData = (List<Map<String, Object>>) documentSnapshot.get("memories");
                        List<Memory> memories = new ArrayList<>();

                        // Convert each map into a Memory object
                        if (memoriesData != null) {
                            for (Map<String, Object> memoryData : memoriesData) {
                                String memoryName = (String) memoryData.get("title"); // Assuming "name" is the key in your Memory object
                                String caption = (String) memoryData.get("caption"); // Assuming "caption" is the key
                                String imageUri = (String) memoryData.get("imageUri"); // Assuming "imageUri" is the key

                                // Create a Memory object and add it to the list
                                memories.add(new Memory(memoryName, caption, imageUri));
                            }
                        }
                        listener.onMemoriesFetched(memories);
                    } else {
                        listener.onMemoriesFetched(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching memories", e);
                    listener.onMemoriesFetched(null);
                });
    }
    // Show a toast message
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
