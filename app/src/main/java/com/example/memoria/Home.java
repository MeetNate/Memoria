package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;
import java.util.Map;

import Helper.ResultHelper;
import Helper.UserSession;
import ReusableClass.navigationMenuBar;

public class Home extends AppCompatActivity {

    private ImageButton profileBtn;
    private NavigationBarView navigationMenu;
    private TextView academic_class, academic_year;
    private navigationMenuBar navigation;
    private UserSession userSession = UserSession.getInstance();
    ResultHelper resultHelper = new ResultHelper(); // Create an instance of ResultHelper

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        profileBtn = findViewById(R.id.profilebtn);
        navigationMenu = findViewById(R.id.bottomNavigationView);
        academic_class = findViewById(R.id.academic_class);
        academic_year = findViewById(R.id.academic_year);
        navigation = new navigationMenuBar();

        // Fetch class list from UserSession
        List<Map<String, String>> classList = userSession.getClassList();

        // Check if classList is not empty and fetch the first class details
        if (classList != null && !classList.isEmpty()) {
            Map<String, String> firstClass = classList.get(0); // Fetch 0th index data
            String classVal = firstClass.get("classVal");
            String academicYear = firstClass.get("academicYear");

            // Display data in TextViews
            academic_class.setText(classVal);
            academic_year.setText(academicYear);
        } else {
            // Handle case when classList is empty
            academic_class.setText("Create a new class");
            academic_year.setText("Not available");
        }

        // Profile button navigation
        profileBtn.setOnClickListener(v -> {
            Intent profileIntent = new Intent(Home.this, Profile.class);
            startActivity(profileIntent);
        });

        // Initialize navigation menu
        navigation.menuBar(navigationMenu, Home.this);

    }
}

