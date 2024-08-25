package com.example.memoria;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {

    private static final String USERS_COLLECTION = "users";
    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void checkUserExists(final Context context, final String email, final String name, final String password, final String phone) {
        db.collection(USERS_COLLECTION).document(email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    showToast(context, "User already exists");
                } else {
                    registerUser(context, email, name, password, phone);
                }
            } else {
                handleError(context, task.getException(), "Failed to check if user exists");
            }
        });
    }

    private void registerUser(final Context context, final String email, final String name, final String password, final String phone) {
        Map<String, Object> user = createUserDataMap(name, email, password, phone);

        db.collection(USERS_COLLECTION).document(email)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    showToast(context, "User registered successfully");
                    navigateToHome(context, name, email, phone);
                })
                .addOnFailureListener(e -> handleError(context, e, "Registration failed"));
    }

    private Map<String, Object> createUserDataMap(String name, String email, String password, String phone) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("password", password);
        user.put("phone", phone);
        return user;
    }

    private void navigateToHome(Context context, String name, String email, String phone) {
        Intent intent = new Intent(context, Home.class);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void handleError(Context context, Exception e, String message) {
        showToast(context, message);
        Log.w(TAG, message, e);
    }

    public void signInUser(final Context context, final String username, final String password) {
        db.collection(USERS_COLLECTION).document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String storedPassword = document.getString("password");
                        if (password.equals(storedPassword)) {
                            // Password matches, redirect to Home and pass user details
                            Intent intent = new Intent(context, Home.class);
                            intent.putExtra("name", document.getString("name"));
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Error checking user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
