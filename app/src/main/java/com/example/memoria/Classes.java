package com.example.memoria;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class Classes extends Fragment {

    UserSession userSession = UserSession.getInstance();
    private TextView academic_class, academic_year, classTxt;
    private ImageButton profileBtn, menuBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);

        profileBtn = view.findViewById(R.id.profilebtn);
        menuBtn = view.findViewById(R.id.menu);
        academic_class = view.findViewById(R.id.academic_class);
        academic_year = view.findViewById(R.id.academic_year);
        classTxt = view.findViewById(R.id.classTxt);

        // Display data using updated methods
        academic_class.setText(userSession.getClassVal());
        academic_year.setText(userSession.getAcademicYear());
        classTxt.setText(userSession.getClassVal());


        // Navigation to Profile
        profileBtn.setOnClickListener(v -> {
            Intent profileIntent = new Intent(getActivity(), Profile.class);
            startActivity(profileIntent);
        });

        return view; // Return the inflated view
    }
}
