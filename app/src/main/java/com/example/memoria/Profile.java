package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;
import java.util.Map;

import Helper.UserHelper;
import Helper.UserSession;
import Helper.ValidationHelper;

public class Profile extends AppCompatActivity {

    private TextView nameTextView, emailTextView, classTextView, classroomIdView, academicYearTextView, nameView;
    private EditText editEmail, editClass, editName, editAcademicYear;
    private Button updateButton;
    private ImageButton backButton, logoutButton, copyButton;
    private UserHelper userHelper = new UserHelper();
    private UserSession userSession = UserSession.getInstance();
    private BroadcastReceiver classCreationReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setInitialValues();

        logoutButton.setOnClickListener(v -> confirmLogout());
        copyButton.setOnClickListener(v -> copyClassroomId());
        updateButton.setOnClickListener(v -> handleUpdate());
        backButton.setOnClickListener(v -> navigateToHome());

        registerClassCreationReceiver();
    }

    private void initializeViews() {
        nameTextView = findViewById(R.id.name_profile);
        emailTextView = findViewById(R.id.email_profile);
        classTextView = findViewById(R.id.curr_class);
        classroomIdView = findViewById(R.id.classroomId);
        nameView = findViewById(R.id.nameView);
        logoutButton = findViewById(R.id.logout);
        updateButton = findViewById(R.id.updatebtn);
        editEmail = findViewById(R.id.editEmailView);
        editClass = findViewById(R.id.editClassView);
        editName = findViewById(R.id.editNameView);
        backButton = findViewById(R.id.backbtn);
        copyButton = findViewById(R.id.copyButton);
        academicYearTextView = findViewById(R.id.academic_year);
        editAcademicYear = findViewById(R.id.editAcademicYear); // Initialize Academic Year EditText

    }

    private void setInitialValues() {
        nameTextView.setText(userSession.getUserName());
        emailTextView.setText(userSession.getUserEmail());

        // Fetch classList and display the first class details
        List<Map<String, String>> classList = userSession.getClassList();
        if (classList != null && !classList.isEmpty()) {
            Map<String, String> firstClass = classList.get(0);
            classTextView.setText(firstClass.get("classVal"));
            classroomIdView.setText(firstClass.get("classroomId"));
            academicYearTextView.setText(firstClass.get("academicYear"));
        } else {
            classTextView.setText("");
            classroomIdView.setText("");
            academicYearTextView.setText("");
        }

        // Disable buttons if any of the TextViews are empty
        if (isEmpty(classTextView) || isEmpty(classroomIdView) || isEmpty(academicYearTextView)) {
            copyButton.setEnabled(false);
            updateButton.setEnabled(false);
        } else {
            copyButton.setEnabled(true);
            updateButton.setEnabled(true);
        }
    }

    // Helper method to check if a TextView is empty
    private boolean isEmpty(TextView textView) {
        return textView.getText().toString().trim().isEmpty();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(Profile.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    userSession.setUserName(null);
                    userSession.setUserEmail(null);
                    userSession.setClassDetails(null, null, null);
                    navigateToSignIn();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void navigateToSignIn() {
        Intent signInIntent = new Intent(Profile.this, Signin.class);
        signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signInIntent);
        finish();
    }

    private void copyClassroomId() {
        String classroomId = classroomIdView.getText().toString();

        if (!classroomId.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Classroom ID", classroomId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(Profile.this, "Classroom ID copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Profile.this, "No Classroom ID available to copy", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleUpdate() {
        if (editEmail.getVisibility() == View.GONE) {
            showEditFields();
        } else {
            // Show toast message before update
            Toast.makeText(Profile.this, "Updating data, please wait...", Toast.LENGTH_SHORT).show();
            updateUserData();
        }
    }


    private void showEditFields() {
        emailTextView.setVisibility(View.GONE);
        classTextView.setVisibility(View.GONE);
        nameTextView.setVisibility(View.GONE);
        academicYearTextView.setVisibility(View.GONE);

        editEmail.setVisibility(View.VISIBLE);
        editEmail.setText(emailTextView.getText().toString());

        editClass.setVisibility(View.VISIBLE);
        editClass.setText(classTextView.getText().toString());

        nameView.setVisibility(View.VISIBLE);
        editName.setVisibility(View.VISIBLE);
        editName.setText(nameTextView.getText().toString());

        editAcademicYear.setVisibility(View.VISIBLE);
        editAcademicYear.setText(academicYearTextView.getText().toString());
    }

    private void updateUserData() {
        String updatedEmail = editEmail.getText().toString();
        String updatedClass = editClass.getText().toString();
        String updatedName = editName.getText().toString();
        String updatedAcademicYear = editAcademicYear.getText().toString();

        if (isInputValid(updatedEmail, updatedClass, updatedName, updatedAcademicYear) &&
                ValidationHelper.validateClassAndYear(Profile.this, updatedClass, updatedAcademicYear) &&
                ValidationHelper.validateUserInputs(Profile.this, updatedName, updatedEmail)
        ) {

            userSession.setClassDetails(updatedClass, updatedAcademicYear, userSession.getClassroomId());
            userSession.setUserEmail(updatedEmail);
            userSession.setUserName(updatedName);

            userHelper.updateUserData(Profile.this, updatedEmail, updatedName, updatedClass, updatedAcademicYear);
            updateTextViews(updatedEmail, updatedClass, updatedName, updatedAcademicYear);

            // Refresh the activity to reflect updated data
            Toast.makeText(Profile.this, "Data updated successfully! Refreshing the page...", Toast.LENGTH_SHORT).show();
            recreate(); // This will refresh the current activity

        } else {
            Intent homeIntent = new Intent(Profile.this, Profile.class);
            startActivity(homeIntent);
        }
    }

    private boolean isInputValid(String email, String classVal, String name, String academicYear) {
        return !email.isEmpty() && !classVal.isEmpty() && !name.isEmpty() && !academicYear.isEmpty();
    }

    private void updateTextViews(String email, String classVal, String name, String academicYear) {
        emailTextView.setText(email);
        classTextView.setText(classVal);
        nameTextView.setText(name);
        academicYearTextView.setText(academicYear);
        classroomIdView.setText(userSession.getClassroomId());

        emailTextView.setVisibility(View.VISIBLE);
        classTextView.setVisibility(View.VISIBLE);
        nameTextView.setVisibility(View.VISIBLE);

        editEmail.setVisibility(View.GONE);
        editClass.setVisibility(View.GONE);
        editName.setVisibility(View.GONE);
        nameView.setVisibility(View.GONE);
        editAcademicYear.setVisibility(View.GONE);
    }

    private void navigateToHome() {
        Intent homeIntent = new Intent(Profile.this, Home.class);
        startActivity(homeIntent);
    }

    private void registerClassCreationReceiver() {
        classCreationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setInitialValues();  // Refresh the displayed values
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(classCreationReceiver, new IntentFilter("CLASS_CREATED"));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(classCreationReceiver);
        super.onDestroy();
    }
}
