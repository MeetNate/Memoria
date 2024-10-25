package com.example.memoria;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView; // Import TextView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapters.ResultAdapter;
import Helper.InterfaceHelper;
import Helper.ResultHelper;
import Helper.UserSession;

public class Result extends AppCompatActivity implements ResultAdapter.OnDeleteClickListener {
    private ImageButton backButton, updateDetailsButton;
    private RecyclerView recyclerViewResults;
    private ResultHelper resultHelper = new ResultHelper();
    private final UserSession userSession = UserSession.getInstance();
    private ResultAdapter resultAdapter;
    private List<Map<String, Object>> result = new ArrayList<>();
    private TextView noResultsMessage; // Reference to the TextView

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.backbtn);
        updateDetailsButton = findViewById(R.id.update_details);
        recyclerViewResults = findViewById(R.id.result_recycler);
        noResultsMessage = findViewById(R.id.no_results_message); // Initialize TextView

        resultAdapter = new ResultAdapter(result, this);
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewResults.setAdapter(resultAdapter);

        fetchResult();

        backButton.setOnClickListener(v -> finish());
        updateDetailsButton.setOnClickListener(v -> openPopup());
    }

    private void fetchResult() {
        resultHelper.getResults(userSession.getUserEmail(), new InterfaceHelper.OnResultsFetchedListener() {
            @Override
            public void onResultsFetched(List<Map<String, Object>> results) {
                result.clear();
                result.addAll(results);
                resultAdapter.notifyDataSetChanged();

                // Show or hide the no results message
                if (result.isEmpty()) {
                    noResultsMessage.setVisibility(View.VISIBLE);
                    recyclerViewResults.setVisibility(View.GONE);
                } else {
                    noResultsMessage.setVisibility(View.GONE);
                    recyclerViewResults.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                showToast(Result.this, error);
            }
        });
    }

    private void openPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_result_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Result.this);
        builder.setView(popupView);

        EditText editGrade = popupView.findViewById(R.id.edit_grade);
        EditText editPercentage = popupView.findViewById(R.id.edit_percentage);
        Button addButton = popupView.findViewById(R.id.done);

        AlertDialog dialog = builder.create();

        addButton.setOnClickListener(v -> {
            String grade = editGrade.getText().toString().trim();
            String percentageStr = editPercentage.getText().toString().trim();

            if (!grade.isEmpty() && !percentageStr.isEmpty()) {
                try {
                    Double percentage = Double.parseDouble(percentageStr);
                    resultHelper.storeResult(Result.this, userSession.getUserEmail(), grade, percentage);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    editPercentage.setError("Please enter a valid percentage value");
                }
            } else {
                if (grade.isEmpty()) {
                    editGrade.setError("Grade cannot be empty");
                }
                if (percentageStr.isEmpty()) {
                    editPercentage.setError("Percentage cannot be empty");
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onDeleteClick(Map<String, Object> result) {
        // Show confirmation dialog before deleting
        new AlertDialog.Builder(this)
                .setTitle("Delete Result")
                .setMessage("Are you sure you want to delete this result?")
                .setPositiveButton("Delete", (dialog, which) -> deleteResult(result))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteResult(Map<String, Object> result) {
        String grade = (String) result.get("grade");
        Double percentage = (Double) result.get("percentage");

        resultHelper.deleteResult(userSession.getUserEmail(), grade, percentage, new InterfaceHelper.OnResultDeletedListener() {
            @Override
            public void onResultDeleted() {
                resultAdapter.removeResult(result);
                showToast(Result.this, "Result deleted successfully");
                // Refresh the results after deletion
                fetchResult();
            }

            @Override
            public void onError(String error) {
                showToast(Result.this, error);
            }
        });
    }

    private void showToast(Result context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
