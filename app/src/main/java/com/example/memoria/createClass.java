package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import java.util.regex.Pattern;

public class createClass extends Fragment {

    private EditText classNameEditText, academicYearEditText, classDivisionEditText;
    private Button createClassButton;
    private FirestoreHelper firestoreHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_class, container, false);

        // Initialize views
        classNameEditText = view.findViewById(R.id.classNameEditText);
        academicYearEditText = view.findViewById(R.id.academicYearEditText);
        classDivisionEditText = view.findViewById(R.id.classDivision);
        createClassButton = view.findViewById(R.id.createClassButton);

        firestoreHelper = new FirestoreHelper();

        createClassButton.setOnClickListener(v -> {
            // Call the validateFields() function when the button is clicked
            if (validateFields()) {
                // Proceed only if validation is successful
                String className = classNameEditText.getText().toString().trim();
                String academicYear = academicYearEditText.getText().toString().trim();
                String classDivision = classDivisionEditText.getText().toString().trim();

                // Create new class and close fragment on success
                firestoreHelper.createNewClass(getContext(), className, academicYear, classDivision);

                // Clear input fields
                classNameEditText.setText("");
                academicYearEditText.setText("");
                classDivisionEditText.setText("");

                showSuccessDialog();  // Show success dialog after creating the class
            }
        });

        return view;
    }

    // Validation function for create new class
    private boolean validateFields() {
        String className = classNameEditText.getText().toString().trim();
        String academicYear = academicYearEditText.getText().toString().trim();
        String classDivision = classDivisionEditText.getText().toString().trim();

        // Check if fields are empty
        if (className.isEmpty() || academicYear.isEmpty() || classDivision.isEmpty()) {
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

        // Validate class division format (e.g., 11-C)
        if (!Pattern.matches("\\d{1,2}-[A-Z]", classDivision)) {
            showErrorDialog("Class division must be in the format 'X-Y' (e.g., 11-C).");
            return false;
        }

        return true;
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
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Class Created")
                .setMessage("Class created successfully")
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
