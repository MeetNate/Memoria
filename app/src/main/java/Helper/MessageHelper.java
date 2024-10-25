package Helper;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Message;

public class MessageHelper {

    private static final String CLASSROOMS_COLLECTION = "classrooms";
    private static final String TAG = "MessageHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserSession userSession = UserSession.getInstance();

    public void storeMessages(String classroomId, List<Message> messages) {
        // Create a reference to the classroom document
        DocumentReference classroomRef = db.collection(CLASSROOMS_COLLECTION).document(classroomId);

        for (Message message : messages) {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("messageText", message.getMessageText());
            messageData.put("isSender", message.isSender());
            messageData.put("senderName", message.getSenderName());
            messageData.put("receiverName", message.getReceiverName()); // Ensure Message has a getReceiverName() method
            messageData.put("timestamp", System.currentTimeMillis()); // Optional: Add a timestamp

            // Update the classroom document to include the new message
            classroomRef.update("messages", FieldValue.arrayUnion(messageData)) // Append messageData to the messages array
                    .addOnSuccessListener(aVoid ->
                            Log.d(TAG, "Message added successfully to classroom: " + classroomId))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Failed to add message to classroom", e));
        }
    }


    public void fetchMessagesFromClassroom(String classroomId, InterfaceHelper.OnMessagesFetchedListener listener) {
        // Reference the classroom document
        DocumentReference classroomRef = db.collection(CLASSROOMS_COLLECTION).document(classroomId);

        // Get the document and retrieve the messages array
        classroomRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract the list of messages
                        List<Map<String, Object>> messageDataList = (List<Map<String, Object>>) documentSnapshot.get("messages");

                        // Convert the raw message data to Message objects
                        List<Message> messages = new ArrayList<>();
                        if (messageDataList != null) {
                            for (Map<String, Object> messageData : messageDataList) {
                                Message message = new Message();
                                message.setMessageText((String) messageData.get("messageText"));
                                message.setSenderName((String) messageData.get("senderName"));
                                message.setReceiverName((String) messageData.get("receiverName"));
                                message.setSender((Boolean) messageData.get("isSender"));
                                // Optionally, handle the timestamp if needed

                                messages.add(message);
                            }
                        }

                        // Notify the listener with the retrieved messages
                        listener.onMessagesFetched(messages);
                    } else {
                        Log.d(TAG, "No classroom found with the provided ID");
                        listener.onError("No classroom found with the provided ID");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch messages", e);
                    listener.onError("Failed to fetch messages");
                });
    }
}