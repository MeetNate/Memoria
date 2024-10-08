package com.example.memoria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        phone = findViewById(R.id.phone);
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
                String phone_txt = phone.getText().toString().trim();

                if (ValidationHelper.validateInputs(register.this, name_txt, email_txt, password_txt, phone_txt)) {
                    firestoreHelper.checkUserExists(register.this, email_txt, name_txt, password_txt, phone_txt);
                }
            }
        });
    }
}
