package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoria.Chat;
import com.example.memoria.R;

import java.util.List;
import java.util.Map;

public class ClassButtonAdapter extends RecyclerView.Adapter<ClassButtonAdapter.ViewHolder> {
    private Context context;
    private List<Map<String, String>> classList;

    public ClassButtonAdapter(Context context, List<Map<String, String>> classList) {
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> classItem = classList.get(position);

        // Set the button text with class value
        String classVal = classItem.get("classVal");
        holder.classButton.setText("Class " + classVal);

        // Set click listener on each button to open the Chat activity
        holder.classButton.setOnClickListener(v -> {
            String classroomId = classItem.get("classroomId"); // Assuming classroomId exists in the map

            // Create intent to navigate to Chat activity
            Intent chatIntent = new Intent(context, Chat.class);
            chatIntent.putExtra("classVal", classVal);
            chatIntent.putExtra("classroomId", classroomId);

            // Start the Chat activity
            context.startActivity(chatIntent);
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button classButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            classButton = itemView.findViewById(R.id.classBtn); // Use the appropriate ID for the button
        }
    }
}
