package com.example.memoria;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Adapters.ChatAdapter;
import Helper.ClassroomHelper;
import Helper.InterfaceHelper;
import Helper.MessageHelper;
import Helper.UserSession;
import Model.Message;

public class Chat extends AppCompatActivity {
    private ImageButton backButton;
    private TextView title;
    private EditText messageEditText;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private final UserSession userSession = UserSession.getInstance(); // Singleton instance of user session
    private ClassroomHelper classroomHelper = new ClassroomHelper(); // FirestoreHelper for database interactions
    private MessageHelper messageHelper = new MessageHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Retrieve the class values passed from the previous activity
        String classVal = getIntent().getStringExtra("classVal");
        String classroomId = getIntent().getStringExtra("classroomId");
        String senderName = userSession.getUserName(); // Get the user name from the session

        // Initialize UI components
        backButton = findViewById(R.id.backbtn);
        title = findViewById(R.id.title);
        messageEditText = findViewById(R.id.messageEditText);
        ImageButton sendButton = findViewById(R.id.sendButton);
        chatRecyclerView = findViewById(R.id.messagesRecyclerView);

        // Set the chat title based on the class name
        title.setText("Class " + classVal);

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, senderName);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Back button to finish the activity
        backButton.setOnClickListener(v -> finish());

        // Fetch and display previous messages from Firestore for the given classroom
        messageHelper.fetchMessagesFromClassroom(classroomId, new InterfaceHelper.OnMessagesFetchedListener() {
            @Override
            public void onMessagesFetched(List<Message> messages) {
                // Add fetched messages to the message list
                messageList.addAll(messages);
                chatAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
                chatRecyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the latest message
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error (e.g., log the error)
                Log.e("Chat", "Error fetching messages: " + errorMessage);
            }
        });

        // Handle send button click
        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString().trim(); // Get the entered message
            if (!messageText.isEmpty()) {
                // Create a new message object with the sender's name
                Message message = new Message(messageText, true, senderName);
                messageList.add(message); // Add the message to the list

                chatAdapter.notifyItemInserted(messageList.size() - 1); // Notify the adapter about the new message
                chatRecyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the new message
                messageEditText.setText(""); // Clear the message input field

                // Store the message in Firestore and update the classroom data
                classroomHelper.storeClassroomInClassrooms(classroomId, classVal, userSession.getAcademicYear(), messageList);
            }
        });
    }
}
