package com.example.memoria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Signin extends AppCompatActivity {
    private EditText email, edtpass;
    private Button google_sigin, submit;
    private TextView create_new_account;
    private FirestoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        email = findViewById(R.id.email);
        edtpass = findViewById(R.id.edtpass);
        submit = findViewById(R.id.submit);
        google_sigin = findViewById(R.id.google_reg);
        create_new_account = findViewById(R.id.create_new_account_);

        firestoreHelper = new FirestoreHelper(); // Initialize FirestoreHelper

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = email.getText().toString().trim();
                String password = edtpass.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Signin.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                firestoreHelper.signInUser(Signin.this, username, password);
                clearFields();
            }
        });

        create_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(Signin.this, register.class);
                startActivity(s);
            }
        });
    }

    private void clearFields() {
        email.setText("");
        edtpass.setText("");
    }
}
