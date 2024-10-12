package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.navigation.NavigationBarView;

public class Home extends AppCompatActivity {

    private ImageButton profileBtn;
    NavigationBarView navigationMenu;
    private TextView academic_class, academic_year;
    private navigationMenuBar navigation;
    UserSession userSession = UserSession.getInstance();

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

        // Display data from UserSession
        academic_class.setText(userSession.getClassVal());  // Update based on new class details method
        academic_year.setText(userSession.getAcademicYear());  // Update based on new class details method

        // Profile button navigation
        profileBtn.setOnClickListener(v -> {
            Intent profileIntent = new Intent(Home.this, Profile.class);
            startActivity(profileIntent);
        });

        // Initialize navigation menu
        navigation.menuBar(navigationMenu, Home.this);
    }
}
