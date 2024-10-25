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

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private List<Map<String, Object>> resultList;
    private OnDeleteClickListener deleteClickListener;

    public ResultAdapter(List<Map<String, Object>> resultList, OnDeleteClickListener deleteClickListener) {
        this.resultList = resultList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Map<String, Object> result = resultList.get(position);
        holder.gradeTextView.setText((String) result.get("grade"));
        holder.percentageTextView.setText(String.valueOf(result.get("percentage")));

        // Set up delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(result);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public void removeResult(Map<String, Object> result) {
        resultList.remove(result);
        notifyDataSetChanged();
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Map<String, Object> result);
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView gradeTextView;
        TextView percentageTextView;
        ImageButton deleteButton; // Add ImageButton for deletion

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            gradeTextView = itemView.findViewById(R.id.grade);
            percentageTextView = itemView.findViewById(R.id.percentage);
            deleteButton = itemView.findViewById(R.id.delete_button); // Initialize the delete button
        }
    }
}
