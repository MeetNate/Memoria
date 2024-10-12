package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;

public class Profile extends AppCompatActivity {

    private TextView nameTextView, emailTextView, classTextView;
    private TextView logoutTextView;
    private EditText editEmail, editClass, editName;
    private Button updateButton;
    private FirestoreHelper firestoreHelper;
    private ImageButton backButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Handle Edge to Edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        nameTextView = findViewById(R.id.name_profile);
        emailTextView = findViewById(R.id.email_profile);
        classTextView = findViewById(R.id.curr_class);
        logoutTextView = findViewById(R.id.logout);
        updateButton = findViewById(R.id.updatebtn);
        editEmail = findViewById(R.id.editEmailView);
        editClass = findViewById(R.id.editClassView);
        editName = findViewById(R.id.editNameView);
        backButton = findViewById(R.id.backbtn);

        firestoreHelper = new FirestoreHelper();
        UserSession userSession = UserSession.getInstance();

        // Set initial values in TextViews
        nameTextView.setText(userSession.getUserName());
        emailTextView.setText(userSession.getUserEmail());
        classTextView.setText(userSession.getClassVal()); // Using classVal method

        // Logout functionality
        logoutTextView.setOnClickListener(v -> {
            userSession.setUserName(null);
            userSession.setUserEmail(null);
            userSession.setClassDetails(null, null); // Resetting class details

            new AlertDialog.Builder(Profile.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        Intent signInIntent = new Intent(Profile.this, Signin.class);
                        signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(signInIntent);
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Update button functionality
        updateButton.setOnClickListener(v -> {
            if (editEmail.getVisibility() == View.GONE) {
                // Show EditTexts and hide TextViews
                emailTextView.setVisibility(View.GONE);
                classTextView.setVisibility(View.GONE);
                nameTextView.setVisibility(View.GONE);

                editEmail.setVisibility(View.VISIBLE);
                editEmail.setText(emailTextView.getText().toString());

                editClass.setVisibility(View.VISIBLE);
                editClass.setText(classTextView.getText().toString());

                editName.setVisibility(View.VISIBLE);
                editName.setText(nameTextView.getText().toString());
            } else {
                // Update user data in Firestore and session
                String updatedEmail = editEmail.getText().toString();
                String updatedClass = editClass.getText().toString();
                String updatedName = editName.getText().toString();

                // Set the updated class details in UserSession (only classVal is stored)
                userSession.setClassDetails(updatedClass, userSession.getAcademicYear()); // Only updating classVal
                userSession.setUserEmail(updatedEmail);
                userSession.setUserName(updatedName);

                // Update the data in Firestore
                firestoreHelper.updateUserData(Profile.this, updatedEmail, updatedName, updatedClass);

                // Update the TextViews and hide the EditTexts
                emailTextView.setText(updatedEmail);
                emailTextView.setVisibility(View.VISIBLE);
                editEmail.setVisibility(View.GONE);

                classTextView.setText(updatedClass);
                classTextView.setVisibility(View.VISIBLE);
                editClass.setVisibility(View.GONE);

                nameTextView.setText(updatedName);
                nameTextView.setVisibility(View.VISIBLE);
                editName.setVisibility(View.GONE);
            }
        });

        // Back button functionality
        backButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(Profile.this, Home.class);
            startActivity(homeIntent);
        });
    }
}
