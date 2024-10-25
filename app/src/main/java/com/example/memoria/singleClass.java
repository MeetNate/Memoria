package com.example.memoria;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import Helper.UserSession;

public class singleClass extends Fragment {

    UserSession userSession = UserSession.getInstance();
    private TextView displayYear, classVal;
    private Button ach_cht, spt_act, sub_fac, rst_cht, yb_cht;
    private ImageButton profileBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_class, container, false);

        // Initialize TextViews and Buttons
        displayYear = view.findViewById(R.id.displayYear);
        classVal = view.findViewById(R.id.classVal);
        profileBtn = view.findViewById(R.id.profilebtn);
        ach_cht = view.findViewById(R.id.ach_cht);
        spt_act = view.findViewById(R.id.spt_act);
        sub_fac = view.findViewById(R.id.sub_fac);
        rst_cht = view.findViewById(R.id.rst_cht);
        yb_cht = view.findViewById(R.id.yb_cht);

        // Fetch the academic year and class from UserSession
        List<Map<String, String>> classList = userSession.getClassList();

        boolean isDataAvailable = false;

        if (classList != null && !classList.isEmpty()) {
            Map<String, String> firstClass = classList.get(0);
            String academicYear = firstClass.get("academicYear");
            String classValue = firstClass.get("classVal");

            // Set the text for displayYear and classVal
            displayYear.setText(academicYear != null && !academicYear.isEmpty() ? academicYear : "No Academic Year");
            classVal.setText(classValue != null && !classValue.isEmpty() ? "Class " + classValue : "No Class");

            // Check if both values are not empty
            isDataAvailable = academicYear != null && !academicYear.isEmpty() && classValue != null && !classValue.isEmpty();
        } else {
            // No data available
            displayYear.setText("No Academic Year");
            classVal.setText("No Class");
        }

        // Disable buttons if data is unavailable
        if (!isDataAvailable) {
            ach_cht.setEnabled(false);
            spt_act.setEnabled(false);
            sub_fac.setEnabled(false);
            rst_cht.setEnabled(false);
            yb_cht.setEnabled(false);

            // Show a popup alert dialog when classVal or displayYear is empty
            showDataMissingDialog();
        }

        // Set click listeners for buttons
        ach_cht.setOnClickListener(v -> {
            Intent achIntent = new Intent(getActivity(), Achievements.class);
            startActivity(achIntent);
        });

        spt_act.setOnClickListener(v -> {
            Intent sptIntent = new Intent(getActivity(), Sports.class);
            startActivity(sptIntent);
        });

        sub_fac.setOnClickListener(v -> {
            Intent subjectIntent = new Intent(getActivity(), Subjects.class);
            startActivity(subjectIntent);
        });

        rst_cht.setOnClickListener(v -> {
            Intent resultIntent = new Intent(getActivity(), Result.class);
            startActivity(resultIntent);
        });

        yb_cht.setOnClickListener(v -> {
            Intent yearbookIntent = new Intent(getActivity(), Yearbook.class);
            startActivity(yearbookIntent);
        });

        // Navigation to Profile
        profileBtn.setOnClickListener(v -> {
            Intent profileIntent = new Intent(getActivity(), Profile.class);
            startActivity(profileIntent);
        });

        return view;
    }

    // Method to show popup dialog when data is missing
    private void showDataMissingDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Alert!! \uD83D\uDEA8")
                .setMessage("Please create a class to access all the features of tha application..")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
