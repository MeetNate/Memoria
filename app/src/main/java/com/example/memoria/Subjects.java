package com.example.memoria;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Adapters.SubjectsAdapter;
import Helper.InterfaceHelper;
import Helper.SubjectHelper;
import Helper.UserSession;

public class Subjects extends AppCompatActivity {
    private ImageButton backButton, updateDetailsButton;
    private RecyclerView recyclerViewSubjects;
    private SubjectHelper subjectHelper = new SubjectHelper();
    private final UserSession userSession = UserSession.getInstance();
    private SubjectsAdapter subjectsAdapter;
    private List<Map<String, String>> subjectList = new ArrayList<>();
    private TextView noSubjectsMessage; // Reference to the TextView

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subjects);

        // Handle Edge to Edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        backButton = findViewById(R.id.backbtn);
        updateDetailsButton = findViewById(R.id.update_details);
        recyclerViewSubjects = findViewById(R.id.subject_recycler);
        noSubjectsMessage = findViewById(R.id.no_subjects_message); // Initialize TextView

        // Set up RecyclerView
        recyclerViewSubjects.setLayoutManager(new LinearLayoutManager(this));
        subjectsAdapter = new SubjectsAdapter(subjectList, this::deleteSubject); // Pass the delete listener
        recyclerViewSubjects.setAdapter(subjectsAdapter);

        // Fetch subjects from Firestore
        fetchSubjects();

        // Set back button functionality
        backButton.setOnClickListener(v -> finish());

        // Set click listener for update details button
        updateDetailsButton.setOnClickListener(v -> openPopup());
    }

    private void fetchSubjects() {
        subjectHelper.getSubjects(userSession.getUserEmail(), new InterfaceHelper.OnSubjectsFetchedListener() {
            @Override
            public void onSubjectsFetched(List<Map<String, String>> subjects) {
                subjectList.clear();
                subjectList.addAll(subjects);
                subjectsAdapter.notifyDataSetChanged(); // Notify adapter of data change

                // Show or hide the no subjects message
                if (subjectList.isEmpty()) {
                    noSubjectsMessage.setVisibility(View.VISIBLE);
                    recyclerViewSubjects.setVisibility(View.GONE);
                } else {
                    noSubjectsMessage.setVisibility(View.GONE);
                    recyclerViewSubjects.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                showToast(Subjects.this, error);
            }
        });
    }

    private void openPopup() {
        // Inflate the popup layout
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_subject_details, null);

        // Create an AlertDialog to show the popup
        AlertDialog.Builder builder = new AlertDialog.Builder(Subjects.this);
        builder.setView(popupView);

        // Initialize views in the popup
        EditText editSubjectName = popupView.findViewById(R.id.edit_subject);
        EditText editTeacherName = popupView.findViewById(R.id.edit_faculty);
        Button addButton = popupView.findViewById(R.id.done);

        // Create the dialog object
        AlertDialog dialog = builder.create();

        // Handle Add button click
        addButton.setOnClickListener(v -> {
            String subjectName = editSubjectName.getText().toString().trim();
            String teacherName = editTeacherName.getText().toString().trim();

            if (!subjectName.isEmpty() && !teacherName.isEmpty()) {
                subjectHelper.storeSubject(Subjects.this, userSession.getUserEmail(), subjectName, teacherName);
                dialog.dismiss();
            } else {
                if (subjectName.isEmpty()) {
                    editSubjectName.setError("Subject Name cannot be empty");
                }
                if (teacherName.isEmpty()) {
                    editTeacherName.setError("Teacher Name cannot be empty");
                }
            }
        });

        // Show the popup dialog
        dialog.show();
    }

    private void deleteSubject(String subjectName, String teacherName) {
        // Create a confirmation dialog
        new AlertDialog.Builder(Subjects.this)
                .setTitle("Delete Subject")
                .setMessage("Are you sure you want to delete this subject?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    subjectHelper.deleteSubject(userSession.getUserEmail(), subjectName, teacherName, new InterfaceHelper.OnSubjectDeletedListener() {
                        @Override
                        public void onSubjectDeleted() {
                            fetchSubjects(); // Refresh the list
                            showToast(Subjects.this, "Subject deleted successfully");
                        }

                        @Override
                        public void onError(String error) {
                            showToast(Subjects.this, error);
                        }
                    });
                })
                .setNegativeButton("No", null) // Dismiss the dialog on "No"
                .show();
    }

    // Method to show Toast messages
    private void showToast(Subjects context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
