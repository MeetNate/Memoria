package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView; // Import TextView

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import Adapters.ClassButtonAdapter;
import Helper.UserSession;

public class ChatList extends Fragment {

    private RecyclerView recyclerView;
    private ClassButtonAdapter adapter;
    private UserSession userSession = UserSession.getInstance();
    private ImageButton profileBtn;
    private TextView noClassesTextView; // Declare TextView for "Classes not available Yet"

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView); // Initialize the RecyclerView
        profileBtn = view.findViewById(R.id.profilebtn);
        noClassesTextView = view.findViewById(R.id.noClassesTextView); // Initialize TextView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // Set LayoutManager

        // Navigation to Profile
        profileBtn.setOnClickListener(v -> {
            Intent profileIntent = new Intent(getActivity(), Profile.class);
            startActivity(profileIntent);
        });

        // Populate RecyclerView
        populateClassButtons();

        return view; // Return the inflated view
    }

    // Method to populate class buttons in RecyclerView
    private void populateClassButtons() {
        List<Map<String, String>> classList = userSession.getClassList();

        // Check if classList is empty and update UI accordingly
        if (classList != null && !classList.isEmpty()) {
            // Show the RecyclerView and hide the TextView
            recyclerView.setVisibility(View.VISIBLE);
            noClassesTextView.setVisibility(View.GONE);

            // Create and set the adapter
            adapter = new ClassButtonAdapter(getActivity(), classList);
            recyclerView.setAdapter(adapter);
        } else {
            // Hide the RecyclerView and show the TextView
            recyclerView.setVisibility(View.GONE);
            noClassesTextView.setVisibility(View.VISIBLE);

            // Debug: No classes available
            Log.d("ChatList", "No classes available in classList");
        }
    }
}
