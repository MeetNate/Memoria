package com.example.memoria;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class singleClass extends Fragment {

    // Get the instance of UserSession
    UserSession userSession = UserSession.getInstance();
    TextView displayYear, classVal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_class, container, false);

        // Initialize TextViews using the inflated view
        displayYear = view.findViewById(R.id.displayYear);
        classVal = view.findViewById(R.id.classVal);

        // Fetch and display the academic year and class from UserSession
        String academicYear = userSession.getAcademicYear();
        String classValue = userSession.getClassVal();

        // Set the text for displayYear and classVal
        displayYear.setText(academicYear != null ? academicYear : "No Academic Year");
        classVal.setText(classValue != null ? "Class " + classValue : "No Class");

        return view; // Return the inflated view
    }
}
