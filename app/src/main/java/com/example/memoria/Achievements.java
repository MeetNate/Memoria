package com.example.memoria;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapters.AchievementAdapter;
import Helper.AchievementHelper;
import Helper.InterfaceHelper;
import Helper.UserSession;

public class Achievements extends AppCompatActivity {

    private ImageButton backButton, update_details;
    private AchievementHelper achievementHelper = new AchievementHelper();
    private final UserSession userSession = UserSession.getInstance();
    private RecyclerView recyclerViewAchievements;
    private AchievementAdapter achievementAdapter;
    private List<Map<String, String>> achievements = new ArrayList<>();
    private ListenerRegistration registration; // For Firestore listener
    private TextView noAchievementsMessage; // Reference to the TextView

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_achievements);

        // Handle Edge to Edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        backButton = findViewById(R.id.backbtn);
        update_details = findViewById(R.id.update_details);
        recyclerViewAchievements = findViewById(R.id.act_ach);
        noAchievementsMessage = findViewById(R.id.no_achievements_message); // Initialize TextView

        // Setup RecyclerView
        recyclerViewAchievements.setLayoutManager(new LinearLayoutManager(this));
        achievementAdapter = new AchievementAdapter(achievements, this::deleteAchievement); // Pass delete listener
        recyclerViewAchievements.setAdapter(achievementAdapter);

        // Set back button functionality
        backButton.setOnClickListener(v -> finish());

        // Set update_details click listener to open popup
        update_details.setOnClickListener(v -> openPopup());

        // Fetch achievements from Firestore
        fetchAchievements();
    }

    private void openPopup() {
        // Inflate the popup layout
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Create an AlertDialog to show the popup
        AlertDialog.Builder builder = new AlertDialog.Builder(Achievements.this);
        builder.setView(popupView);

        // Initialize the views in the popup
        EditText edit_title_ach = popupView.findViewById(R.id.edit_title_ach);
        EditText edit_description_ach = popupView.findViewById(R.id.edit_description_ach);
        Button doneButton = popupView.findViewById(R.id.done);

        // Create the dialog object
        AlertDialog dialog = builder.create();

        // Handle Done button click
        doneButton.setOnClickListener(v -> {
            // Get the input from the fields
            String title = edit_title_ach.getText().toString().trim();
            String description = edit_description_ach.getText().toString().trim();

            // Check if the fields are empty
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(Achievements.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Store achievements in Firestore
                achievementHelper.storeAchievements(Achievements.this, userSession.getUserEmail(), title, description);
                dialog.dismiss();
            }
        });

        // Show the popup dialog
        dialog.show();
    }

    private void fetchAchievements() {
        String email = userSession.getUserEmail();

        // Set up Firestore listener to fetch achievements
        registration = achievementHelper.getAchievements(email, new InterfaceHelper.OnAchievementsFetchedListener() {
            @Override
            public void onAchievementsFetched(List<Map<String, String>> fetchedAchievements) {
                achievements.clear();
                achievements.addAll(fetchedAchievements);
                achievementAdapter.notifyDataSetChanged();

                // Show or hide the no achievements message
                if (achievements.isEmpty()) {
                    noAchievementsMessage.setVisibility(View.VISIBLE);
                    recyclerViewAchievements.setVisibility(View.GONE);
                } else {
                    noAchievementsMessage.setVisibility(View.GONE);
                    recyclerViewAchievements.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Achievements.this, "Error fetching achievements: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Delete achievement with confirmation
    private void deleteAchievement(String title, String description) {
        // Create a confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Achievement")
                .setMessage("Are you sure you want to delete this achievement?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Proceed with deletion
                    achievementHelper.deleteAchievement(userSession.getUserEmail(), title, description, new InterfaceHelper.OnAchievementDeletedListener() {
                        @Override
                        public void onAchievementDeleted() {
                            fetchAchievements(); // Refresh the list
                            Toast.makeText(Achievements.this, "Achievement deleted successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(Achievements.this, "Error deleting achievement: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove Firestore listener to prevent memory leaks
        if (registration != null) {
            registration.remove();
        }
    }
}
