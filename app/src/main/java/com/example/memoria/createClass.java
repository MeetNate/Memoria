package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import Helper.FirestoreHelper;
import Helper.UserSession;

public class createClass extends Fragment {

    private TextView classroomIdView;
    private EditText classNameEditText, academicYearEditText;
    private Button createClassButton;
    private FirestoreHelper firestoreHelper;
    private UserSession userSession = UserSession.getInstance(); // Get the singleton instance

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_class, container, false);

        // Initialize views
        classNameEditText = view.findViewById(R.id.classNameEditText);
        academicYearEditText = view.findViewById(R.id.academicYearEditText);
        createClassButton = view.findViewById(R.id.createClassButton);
        classroomIdView = view.findViewById(R.id.classId);

        firestoreHelper = new FirestoreHelper();

        // Create new class logic
        createClassButton.setOnClickListener(v -> {
            if (validateFields()) {
                // Extracting className and academicYear
                String className = classNameEditText.getText().toString().trim();
                String academicYear = academicYearEditText.getText().toString().trim();

                classNameEditText.setText("");
                academicYearEditText.setText("");

                // Extract the classVal as an integer from className
                int classVal;
                try {
                    classVal = Integer.parseInt(className);
                } catch (NumberFormatException e) {
                    showErrorDialog("Class value must be a number.");
                    return; // Early return to prevent further processing
                }

                // Check if the class already exists in Firestore using classVal and academicYear
                firestoreHelper.checkClassExistsAndFetchIds(className, academicYear, (exists, existingClassroomIds) -> {
                    if (exists && !existingClassroomIds.isEmpty()) {
                        // Show a dialog for the user to select from existing classrooms
                        showClassroomSelectionDialog(existingClassroomIds);
                    } else {
                        // Generate unique classroom ID
                        String classroomId = generateUniqueClassroomId(className, academicYear);

                        // Create new class
                        firestoreHelper.createNewClass(getContext(), className, academicYear, classroomId);
                        showSuccessDialog("Class created successfully.");

                        // Optionally, you can also add the newly created class to the user's class list
                        userSession.addClassToList(className, academicYear, classroomId);

                        // Clear input fields
                        classNameEditText.setText("");
                        academicYearEditText.setText("");
                    }
                });
            }


        });

        // Set the listener for joining a class using classroom ID
        classroomIdView.setOnClickListener(v -> showJoinClassDialog()); // Show the join class dialog

        return view;
    }

    // Validation function for create new class
    private boolean validateFields() {
        String className = classNameEditText.getText().toString().trim();
        String academicYear = academicYearEditText.getText().toString().trim();

        if (className.isEmpty() || academicYear.isEmpty()) {
            showErrorDialog("All fields are required.");
            return false;
        }

        // Validate class value between 1 to 12
        try {
            int classVal = Integer.parseInt(className);
            if (classVal < 1 || classVal > 12) {
                showErrorDialog("Class value must be between 1 and 12.");
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Class value must be a number.");
            return false;
        }

        // Validate academic year format (e.g., 2020-21)
        if (!Pattern.matches("\\d{4}-\\d{2}", academicYear)) {
            showErrorDialog("Academic year must be in the format 'YYYY-YY' (e.g., 2020-21).");
            return false;
        }

        return true;
    }

    // Method to generate a unique classroom ID using classVal and academicYear
    private String generateUniqueClassroomId(String classVal, String academicYear) {
        String uuidPart = UUID.randomUUID().toString().substring(0, 8); // Generate a short UUID
        return classVal + "_" + academicYear + "_" + uuidPart; // Combine classVal, academicYear, and UUID part
    }

    // Error dialog
    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Validation Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Success dialog
    private void showSuccessDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to display the join class dialog
// Method to display the join class dialog
    private void showJoinClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Join Class");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setHint("Enter Classroom ID");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Join", (dialog, which) -> {
            String classroomId = input.getText().toString().trim();

            // Validate if classroom ID is provided and if it's valid
            if (!classroomId.isEmpty()) {
                if (isValidClassroomIdFormat(classroomId)) {
                    // Call FirestoreHelper to check if the classroomId exists
                    firestoreHelper.getClassroomDetails(classroomId, classroomDetails -> {
                        if (classroomDetails != null) {
                            String classVal = classroomDetails.get("classVal");
                            String academicYear = classroomDetails.get("academicYear");

                            // Add the class to the user's class list in UserSession
                            userSession.addClassToList(classVal, academicYear, classroomId);

                            // Add class details to the user's database
                            firestoreHelper.addClassToUserDatabase(userSession.getUserEmail(), classVal, academicYear, classroomId, success -> {
                                    showSuccessDialog("Class joined successfully and details saved.");
                            });
                        } else {
                            // Show an error message if class not found
                            showErrorDialog("Classroom ID not found.");
                        }
                    });
                } else {
                    showErrorDialog("Invalid Classroom ID format.");
                }
            } else {
                showErrorDialog("Classroom ID cannot be empty.");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void showClassroomSelectionDialog(List<String> existingClassroomIds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Classroom Exists");

        // Create a string array from existing classroom IDs
        String[] classroomIdsArray = existingClassroomIds.toArray(new String[0]);

        // Set up the list of classroom IDs
        builder.setItems(classroomIdsArray, (dialog, which) -> {
            // User selected a classroom ID to join
            String selectedClassroomId = classroomIdsArray[which];

            // Copy the selected classroom ID to clipboard
            copyToClipboard(selectedClassroomId);

            // Retrieve classroom details for the selected ID
            firestoreHelper.getClassroomDetails(selectedClassroomId, classDetails -> {
                if (classDetails != null) {
                    String classVal = classDetails.get("classVal");
                    String academicYear = classDetails.get("academicYear");

                    // Add class details to user's class list if not already present
                    userSession.addClassToList(classVal, academicYear, selectedClassroomId);

                    // Save this class in the user's database
                    firestoreHelper.addClassToUserDatabase(userSession.getUserEmail(), classVal, academicYear, selectedClassroomId, task -> {
                        if (task.isSuccessful()) {
                            showSuccessDialog("Classroom is already created! Join using the link provided below: " + selectedClassroomId);
                        } else {
                            showErrorDialog("Failed to save class details.");
                        }
                    });
                } else {
                    showErrorDialog("Classroom details not found.");
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Classroom ID", text);
        clipboard.setPrimaryClip(clip);
        showSuccessDialog("Classroom ID copied to clipboard: " + text); // Optional: Notify the user
    }


    // Method to validate if classroom ID format is valid
    private boolean isValidClassroomIdFormat(String classroomId) {
        // Validate the format "classVal_academicYear_uuidPart"
        return classroomId.matches("\\d+_\\d{4}-\\d{2}_[a-z0-9]{8}");
    }
}
