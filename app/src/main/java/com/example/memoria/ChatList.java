package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Map;

public class ChatList extends Fragment {

    private ImageButton profileBtn;
    private LinearLayout classButtonContainer; // Container for class buttons
    private Button classBtn; // Single Button instance
    private UserSession userSession = UserSession.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        profileBtn = view.findViewById(R.id.profilebtn);
        classButtonContainer = view.findViewById(R.id.classButtonContainer); // Initialize the button container

        // Create class buttons dynamically
        populateClassButtons();

        // Navigation to Profile
        profileBtn.setOnClickListener(v -> {
            Intent profileIntent = new Intent(getActivity(), Profile.class);
            startActivity(profileIntent);
        });

        return view; // Return the inflated view
    }

    private void populateClassButtons() {
        List<Map<String, String>> classList = userSession.getClassList();

        // Debug: Check the size of the class list
        Log.d("ChatList", "Number of classes: " + (classList != null ? classList.size() : 0));

        // Clear any existing buttons
        classButtonContainer.removeAllViews();

        // Create a button for each class
        if (classList != null && !classList.isEmpty()) {
            for (Map<String, String> classDetails : classList) {
                String classVal = classDetails.get("classVal");

                // Debug: Log the class value
                Log.d("ChatList", "Class Value: " + classVal);

                // Create a new instance for classBtn for each iteration
                classBtn = new Button(getActivity());

                // Set text for classBtn
                classBtn.setText("Class " + classVal);

                // Create layout params with margins
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(330, 170);
                layoutParams.setMargins(20, 16, 16, 16); // Set margin values (left, top, right, bottom)
                classBtn.setTextSize(15);

                // Set the layout parameters to the button
                classBtn.setLayoutParams(layoutParams);

                // Set the background to the rounded button drawable
                classBtn.setBackgroundResource(R.drawable.rounded_button);

                // Set the click listener for the button
                classBtn.setOnClickListener(v -> {
                    // Handle button click, e.g., navigate to class chat
                    Intent intent = new Intent(getActivity(), Home.class);
                    intent.putExtra("classVal", classVal);
                    startActivity(intent);
                });

                // Add the button to the container
                classButtonContainer.addView(classBtn);
            }
        } else {
            // Debug: No classes available
            Log.d("ChatList", "No classes available in classList");
        }
    }
}
