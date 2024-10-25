package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import Adapters.ClassButtonAdapter;
import Helper.UserSession;

public class Classes extends Fragment {

    private UserSession userSession = UserSession.getInstance();
    private TextView academic_class, academic_year, noClassesTextView;
    private ImageButton profileBtn;
    private RecyclerView recyclerView;
    private ClassButtonAdapter adapter;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);

        // Initialize views
        profileBtn = view.findViewById(R.id.profilebtn);
        academic_class = view.findViewById(R.id.academic_class);
        academic_year = view.findViewById(R.id.academic_year);
        recyclerView = view.findViewById(R.id.recyclerView);
        noClassesTextView = view.findViewById(R.id.noClassesTextView);

        // Set GridLayoutManager with 3 columns for RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Fetch class list from UserSession
        List<Map<String, String>> classList = userSession.getClassList();

        // Check if classList is not empty and update UI accordingly
        if (classList != null && !classList.isEmpty()) {
            // Display data from the first class
            Map<String, String> firstClass = classList.get(0);
            String classVal = firstClass.get("classVal");
            String academicYear = firstClass.get("academicYear");

            academic_class.setText(classVal);
            academic_year.setText(academicYear);

            // Set up RecyclerView
            recyclerView.setVisibility(View.VISIBLE);
            noClassesTextView.setVisibility(View.GONE);

            // Create and set the adapter
            adapter = new ClassButtonAdapter(getActivity(), classList);
            recyclerView.setAdapter(adapter);
        } else {
            // Hide the RecyclerView and show the TextView
            recyclerView.setVisibility(View.GONE);
            noClassesTextView.setVisibility(View.VISIBLE);
            noClassesTextView.setText("No classes found");

            // Debug: No classes available
            Log.d("Classes", "No classes available");
        }

        // Navigation to Profile
        profileBtn.setOnClickListener(v -> {
            Intent profileIntent = new Intent(getActivity(), Profile.class);
            startActivity(profileIntent);
        });

        return view; // Return the inflated view
    }
}
