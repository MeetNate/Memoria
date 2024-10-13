package com.example.memoria;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import Helper.FirestoreHelper;
import Helper.ValidationHelper;

import java.util.UUID; // Import UUID for unique ID generation

public class Details extends AppCompatActivity {

    private EditText classVal, academicYear;
    private Button submit;
    private FirestoreHelper firestoreHelper = new FirestoreHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        classVal = findViewById(R.id.classVal);
        academicYear = findViewById(R.id.academicYear);
        submit = findViewById(R.id.submit);

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        submit.setOnClickListener(v -> {
            String classVal_txt = classVal.getText().toString().trim();
            String academicYear_txt = academicYear.getText().toString().trim();

            if (ValidationHelper.validateInputs(Details.this, name, email, password)) {
                String uniqueClassroomId = generateUniqueClassroomId(classVal_txt, academicYear_txt); // Generate the ID
                firestoreHelper.checkUserExists(Details.this, email, name, password, classVal_txt, academicYear_txt, uniqueClassroomId);
            }
        });

    }

    // Method to generate a unique classroom ID using classVal and academicYear
    private String generateUniqueClassroomId(String classVal, String academicYear) {
        String uuidPart = UUID.randomUUID().toString().substring(0, 8); // Generate a short UUID
        return classVal + "_" + academicYear + "_" + uuidPart; // Combine classVal, academicYear, and UUID part
    }

}
