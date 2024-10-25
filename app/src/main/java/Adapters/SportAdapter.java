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

public class SportAdapter extends RecyclerView.Adapter<SportAdapter.SportsViewHolder> {
    private List<Map<String, String>> sportsList;
    private OnSportClickListener listener;

    public SportAdapter(List<Map<String, String>> sportsList, OnSportClickListener listener) {
        this.sportsList = sportsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sport, parent, false);
        return new SportsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SportsViewHolder holder, int position) {
        Map<String, String> sport = sportsList.get(position);

        // Safely set the text views
        holder.titleTextView.setText(sport.getOrDefault("name", "Unknown Sport"));
        holder.descriptionTextView.setText(sport.getOrDefault("description", "No Description"));

        // Handle the delete button click
        holder.deleteButton.setOnClickListener(v -> {
            String title = sport.get("name");
            String description = sport.get("description");
            if (listener != null) {
                listener.onSportDelete(title, description); // Notify the listener for delete action
            }
        });
    }
    public interface OnSportClickListener {
        void onSportDelete(String title, String description);
    }
    @Override
    public int getItemCount() {
        return sportsList != null ? sportsList.size() : 0; // Ensure size is handled safely
    }

    static class SportsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageButton deleteButton; // Button to delete the sport

        public SportsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.sport_name);
            descriptionTextView = itemView.findViewById(R.id.sport_description);
            deleteButton = itemView.findViewById(R.id.delete_button); // Ensure the button is in your layout
        }
    }
}
