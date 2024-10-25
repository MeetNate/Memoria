package com.example.memoria;

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

import Adapters.SportAdapter;
import Helper.InterfaceHelper;
import Helper.SportHelper;
import Helper.UserSession;

public class Sports extends AppCompatActivity {
    private ImageButton backButton, updateDetailsButton;
    private SportHelper sportHelper = new SportHelper();
    private final UserSession userSession = UserSession.getInstance();
    private ListenerRegistration registration;
    private List<Map<String, String>> sports = new ArrayList<>();
    private RecyclerView sportsRecyclerView;
    private SportAdapter sportAdapter;
    private TextView noSportsMessage; // Reference to the TextView for no sports message

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sports);

        // Handle Edge to Edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        backButton = findViewById(R.id.backbtn);
        updateDetailsButton = findViewById(R.id.update_details);
        sportsRecyclerView = findViewById(R.id.spt_ach);
        noSportsMessage = findViewById(R.id.no_sports_message); // Initialize TextView

        // Initialize RecyclerView and SportsAdapter
        sportAdapter = new SportAdapter(sports, this::deleteSport); // Pass the delete listener
        sportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sportsRecyclerView.setAdapter(sportAdapter); // Set the adapter to the RecyclerView

        // Set back button functionality
        backButton.setOnClickListener(v -> finish());

        // Set update_details click listener to open popup
        updateDetailsButton.setOnClickListener(v -> openPopup());

        // Fetch sports achievements initially
        fetchSports();
    }

    private void openPopup() {
        // Inflate the popup layout
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Create an AlertDialog to show the popup
        AlertDialog.Builder builder = new AlertDialog.Builder(Sports.this);
        builder.setView(popupView);

        // Initialize the views in the popup
        EditText editTitleAch = popupView.findViewById(R.id.edit_title_ach);
        EditText editDescriptionAch = popupView.findViewById(R.id.edit_description_ach);
        Button doneButton = popupView.findViewById(R.id.done);

        // Create the dialog object
        AlertDialog dialog = builder.create();

        // Handle Done button click
        doneButton.setOnClickListener(v -> {
            String title = editTitleAch.getText().toString().trim();
            String description = editDescriptionAch.getText().toString().trim();

            boolean isValid = true; // Flag to check validation

            // Check if the title field is empty
            if (title.isEmpty()) {
                editTitleAch.setError("Title cannot be empty");
                isValid = false; // Mark as invalid
            } else {
                editTitleAch.setError(null); // Clear the error if it's valid
            }

            // Check if the description field is empty
            if (description.isEmpty()) {
                editDescriptionAch.setError("Description cannot be empty");
                isValid = false; // Mark as invalid
            } else {
                editDescriptionAch.setError(null); // Clear the error if it's valid
            }

            // If both fields are valid, proceed
            if (isValid) {
                // Store sports achievement in Firestore
                sportHelper.storeSports(Sports.this, userSession.getUserEmail(), title, description);

                // Refresh the sports list after adding new achievement
                fetchSports(); // Call fetchSports again to refresh the list
                dialog.dismiss();
            } else {
                Toast.makeText(Sports.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the popup dialog
        dialog.show();
    }

    private void fetchSports() {
        String email = userSession.getUserEmail();

        // Set up Firestore listener to fetch achievements
        registration = sportHelper.getSports(email, new InterfaceHelper.OnSportsFetchedListener() {
            @Override
            public void onSportsFetched(List<Map<String, String>> fetchedSports) {
                sports.clear();
                sports.addAll(fetchedSports);
                sportAdapter.notifyDataSetChanged(); // Notify adapter about data change

                // Show or hide the no sports message
                if (sports.isEmpty()) {
                    noSportsMessage.setVisibility(View.VISIBLE);
                    sportsRecyclerView.setVisibility(View.GONE);
                } else {
                    noSportsMessage.setVisibility(View.GONE);
                    sportsRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Sports.this, "Error fetching Sports: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSport(String title, String description) {
        // Create a confirmation dialog
        new AlertDialog.Builder(Sports.this)
                .setTitle("Delete Sport")
                .setMessage("Are you sure you want to delete this sport?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sportHelper.deleteSports(userSession.getUserEmail(), title, description, new InterfaceHelper.OnSportDeletedListener() {
                        @Override
                        public void onSportDeleted() {
                            fetchSports(); // Refresh the list after deletion
                            Toast.makeText(Sports.this, "Sport deleted successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(Sports.this, "Error deleting Sport: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null) // Dismiss the dialog on "No"
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();
        }
    }
}
