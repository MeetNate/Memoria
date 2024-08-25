package com.example.memoria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Signin extends AppCompatActivity {
    private EditText name, edtpass;
    private Button google_sigin, submit;
    private TextView create_new_account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        name = findViewById(R.id.name);
        edtpass = findViewById(R.id.edtpass);
        submit = findViewById(R.id.submit);
        google_sigin = findViewById(R.id.google_signin);
        create_new_account=findViewById(R.id.create_new_account_);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = name.getText().toString();
                String password = edtpass.getText().toString();

                if (username.equals("admin") && password.equals("admin")) {
                    Intent intent = new Intent(Signin.this, Home.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Signin.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }
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
}