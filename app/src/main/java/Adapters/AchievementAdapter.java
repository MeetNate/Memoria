package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoria.R;

import java.util.List;
import java.util.Map;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {
    private List<Map<String, String>> achievements;
    private OnAchievementDeleteListener deleteListener;

    public AchievementAdapter(List<Map<String, String>> achievements, OnAchievementDeleteListener deleteListener) {
        this.achievements = achievements;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Map<String, String> achievement = achievements.get(position);
        holder.titleTextView.setText(achievement.get("title"));
        holder.descriptionTextView.setText(achievement.get("description"));

        // Set delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onAchievementDelete(achievement.get("title"), achievement.get("description"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public static class AchievementViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageButton deleteButton;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            deleteButton = itemView.findViewById(R.id.delete_button); // Ensure this button exists in your layout
        }
    }

    // Interface for delete action
    public interface OnAchievementDeleteListener {
        void onAchievementDelete(String title, String description);
    }
}
