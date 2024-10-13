package com.example.memoria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import Helper.FirestoreHelper;
import Helper.ValidationHelper;

public class register extends AppCompatActivity {

    private EditText name, password, email, phone;
    private TextView already_have_account;
    private Button submit, google_reg;

    private FirestoreHelper firestoreHelper = new FirestoreHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        submit = findViewById(R.id.submit);
        google_reg = findViewById(R.id.google_reg);
        already_have_account = findViewById(R.id.already_have_account);

        already_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register.this, Signin.class));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_txt = name.getText().toString().trim();
                String email_txt = email.getText().toString().trim();
                String password_txt = password.getText().toString().trim();

                // Validate input fields
                if (ValidationHelper.validateInputs(register.this, name_txt, email_txt, password_txt)) {
                    // Create an intent to start the Details activity
                    Intent intent = new Intent(register.this, Details.class);

                    // Put the data into the intent as extras
                    intent.putExtra("name", name_txt);
                    intent.putExtra("email", email_txt);
                    intent.putExtra("password", password_txt);

                    // Start the Details activity
                    startActivity(intent);
                }
            }
        });

    }
}
