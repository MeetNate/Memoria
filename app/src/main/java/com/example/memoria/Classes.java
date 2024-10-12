package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Map;

public class Classes extends Fragment {

    UserSession userSession = UserSession.getInstance();
    private TextView academic_class, academic_year;
    private ImageButton profileBtn;
    private LinearLayout classButtonContainer; // Container for class buttons

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);

        profileBtn = view.findViewById(R.id.profilebtn);
        academic_class = view.findViewById(R.id.academic_class);
        academic_year = view.findViewById(R.id.academic_year);
        classButtonContainer = view.findViewById(R.id.classButtonContainer); // Initialize the button container

        // Display data using updated methods
        academic_class.setText(userSession.getClassVal());
        academic_year.setText(userSession.getAcademicYear());

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

        // Create a GridLayout for the class buttons
        GridLayout classGrid = new GridLayout(getActivity());
        classGrid.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Width
                LinearLayout.LayoutParams.WRAP_CONTENT // Height
        ));
        classGrid.setColumnCount(2); // Set number of columns

        // Create a button for each class
        if (classList != null && !classList.isEmpty()) {
            for (Map<String, String> classDetails : classList) {
                String classVal = classDetails.get("classVal");

                // Debug: Log the class value
                Log.d("ChatList", "Class Value: " + classVal);

                // Create a new instance for classBtn for each iteration
                Button classBtn = new Button(getActivity());

                // Set text for classBtn
                classBtn.setText(classVal);

                // Create layout params for the button with fixed size
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.setMargins(20, 16, 20, 16); // Set margin values (left, top, right, bottom)
                layoutParams.width = 300; // Set fixed width
                layoutParams.height = 200; // Set fixed height
                layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Set to fill the column equally

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

                // Add the button to the classGrid
                classGrid.addView(classBtn);
            }
        } else {
            Log.d("ChatList", "No classes available");
        }

        // Clear any previous GridLayout before adding
        classButtonContainer.removeAllViews();
        // Add the GridLayout to the classButtonContainer
        classButtonContainer.addView(classGrid);
    }
}
