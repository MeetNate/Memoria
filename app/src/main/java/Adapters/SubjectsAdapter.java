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

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> {
    private List<Map<String, String>> subjectList;
    private OnSubjectDeleteListener deleteListener;

    public SubjectsAdapter(List<Map<String, String>> subjectList, OnSubjectDeleteListener deleteListener) {
        this.subjectList = subjectList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Map<String, String> subject = subjectList.get(position);
        holder.subjectNameTextView.setText(subject.get("subjectName"));
        holder.teacherNameTextView.setText(subject.get("facultyName"));

        // Set the delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onSubjectDelete(subject.get("subjectName"), subject.get("facultyName"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectNameTextView;
        TextView teacherNameTextView;
        ImageButton deleteButton;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameTextView = itemView.findViewById(R.id.subject_name);
            teacherNameTextView = itemView.findViewById(R.id.teacher_name);
            deleteButton = itemView.findViewById(R.id.delete_button); // Ensure this button exists in your layout
        }
    }

    // Interface for delete action
    public interface OnSubjectDeleteListener {
        void onSubjectDelete(String subjectName, String teacherName);
    }
}
