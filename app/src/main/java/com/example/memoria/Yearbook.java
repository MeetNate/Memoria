package com.example.memoria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import Helper.InterfaceHelper;
import Helper.MemoryHelper;
import Helper.UserSession;
import Model.Memory;
import Adapters.MemoryAdapter;

public class Yearbook extends AppCompatActivity {

    private ImageButton backButton;
    private Button memoryBtn;
    private MemoryHelper memoryHelper = new MemoryHelper();
    private RecyclerView memoriesRecyclerView;
    private MemoryAdapter memoryAdapter;
    private List<Memory> memoryList = new ArrayList<>();
    private final UserSession userSession = UserSession.getInstance();
    private TextView noMemoriesTextView;
    private Uri selectedImageUri; // To store the selected image URI
    private AlertDialog dialog; // Declare the dialog here

    // Firebase Storage
    private FirebaseStorage storage;
    private StorageReference storageReference;

    // Activity Result Launcher for picking images
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_yearbook);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        backButton = findViewById(R.id.backbtn);
        memoryBtn = findViewById(R.id.memoryBtn);
        memoriesRecyclerView = findViewById(R.id.recyclerView);
        noMemoriesTextView = findViewById(R.id.noMemoriesTextView);

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Initialize the ActivityResultLauncher for image selection
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageUri = data.getData(); // Store the selected image URI
                            // Update the ImageView in the dialog if it's open
                            if (dialog != null && dialog.isShowing()) {
                                ImageView selectedImageView = dialog.findViewById(R.id.selectedImageView);
                                updateImageViewInDialog(selectedImageUri, selectedImageView);
                            }
                        }
                    }
                }
        );

        // Set up button click listeners
        backButton.setOnClickListener(v -> finish());
        memoryBtn.setOnClickListener(v -> showDialog());

        // Fetch and display memories when the activity is created
        fetchAndDisplayMemories();
    }

    public void showDialog() {
        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_memory, null);
        builder.setView(dialogView);

        // Initialize UI elements in the dialog
        EditText memoryNameEditText = dialogView.findViewById(R.id.memoryNameEditText);
        EditText memoryCaptionEditText = dialogView.findViewById(R.id.captionEditText);
        ImageView selectedImageView = dialogView.findViewById(R.id.selectedImageView);
        Button selectImageButton = dialogView.findViewById(R.id.selectImageButton);

        // Set dialog title
        builder.setTitle("Add a New Memory");

        // Set up the Select Image button click listener
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        // Set dialog positive button
        builder.setPositiveButton("Add", (dialog, which) -> {
            String memoryName = memoryNameEditText.getText().toString().trim();
            String memoryCaption = memoryCaptionEditText.getText().toString().trim();

            if (memoryName.isEmpty() || memoryCaption.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show();
                return; // Do not proceed if any field is empty
            }

            // Handle saving the memory (e.g., upload image to Firebase and save memory data)
            uploadMemoryData(userSession.getUserEmail(), memoryName, memoryCaption, selectedImageUri);
            dialog.dismiss(); // Dismiss the dialog after adding
        });

        // Set dialog negative button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create the dialog
        dialog = builder.create();
        dialog.show();

        // Update the ImageView in the dialog if an image is selected
        if (selectedImageUri != null) {
            updateImageViewInDialog(selectedImageUri, selectedImageView);
        }
    }

    private void updateImageViewInDialog(Uri selectedImageUri, ImageView selectedImageView) {
        // Method to update the ImageView with the selected image URI
        if (selectedImageUri != null) {
            selectedImageView.setImageURI(selectedImageUri);
            selectedImageView.setVisibility(View.VISIBLE); // Make the ImageView visible
        }
    }

    private void uploadMemoryData(String email, String memoryName, String memoryCaption, Uri imageUri) {
        // Reference to Firebase Storage
        StorageReference memoryImageRef = storageReference.child("memories/" + email + "/" + System.currentTimeMillis() + ".jpg");

        // Upload the image to Firebase Storage
        memoryImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL for the uploaded image
                    memoryImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String imageUrl = downloadUri.toString(); // Get the URL as a string

                        // Use the existing saveMemory method to store memory data in Firestore
                        memoryHelper.saveMemory(this, email, memoryName, memoryCaption, Uri.parse(imageUrl));
                        Toast.makeText(this, "Memory Added: " + memoryName, Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        // Handle the error in getting the download URL
                        Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle the error in uploading the image
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchAndDisplayMemories() {
        String email = userSession.getUserEmail();
        memoryHelper.getMemories(email, new InterfaceHelper.OnMemoriesFetchListener() {
            @Override
            public void onMemoriesFetched(List<Memory> memories) {
                memoryList.clear(); // Clear any existing memories
                if (memories != null) {
                    memoryList.addAll(memories); // Add fetched memories
                }
                if (memoryAdapter == null) {
                    // Initialize the adapter only once
                    memoryAdapter = new MemoryAdapter(memoryList);
                    memoriesRecyclerView.setLayoutManager(new LinearLayoutManager(Yearbook.this));
                    memoriesRecyclerView.setAdapter(memoryAdapter);  // Set the adapter to RecyclerView
                } else {
                    // Notify the adapter of the change in data
                    memoryAdapter.notifyDataSetChanged();
                }
                noMemoriesTextView.setVisibility(memoryList.isEmpty() ? View.VISIBLE : View.GONE); // Show/hide the no memories text
            }

            public void onFailure(String errorMessage) {
                Toast.makeText(Yearbook.this, "Error fetching memories: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
