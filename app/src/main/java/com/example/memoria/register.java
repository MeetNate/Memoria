package com.example.memoria;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {
    private EditText name,password,email,phone;
    private TextView already_have_account;
    private Button submit,google_reg;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=findViewById(R.id.name);
        password=findViewById(R.id.password);
        email=findViewById(R.id.email);
        phone=findViewById(R.id.phone);
        submit=findViewById(R.id.submit);
        google_reg=findViewById(R.id.google_reg);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_txt = name.getText().toString().trim();
                String email_txt = email.getText().toString().trim();
                String password_txt = password.getText().toString().trim();
                String phone_txt = phone.getText().toString().trim();

                Map<String, Object> user = new HashMap<>();
                user.put("name", name_txt);
                user.put("email", email_txt);
                user.put("password", password_txt);
                user.put("phone", phone_txt);

                db.collection("users")
                        .add(user)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(register.this, Home.class));
                                } else {
                                    Toast.makeText(register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });
    }
}