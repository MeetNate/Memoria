package Adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memoria.R;

import java.util.List;

import Model.Memory;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder> {

    private List<Memory> memoryList;

    public MemoryAdapter(List<Memory> memoryList) {
        this.memoryList = memoryList;
    }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory, parent, false);
        return new MemoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position) {
        Memory memory = memoryList.get(position);
        holder.memoryNameTextView.setText(memory.getTitle());
        holder.memoryCaptionTextView.setText(memory.getCaption());

        // Load image using Glide or Picasso
        Glide.with(holder.itemView.getContext())
                .load(memory.getImageUri())
                .into(holder.memoryImageView);
    }


    @Override
    public int getItemCount() {
        return memoryList.size();
    }

    static class MemoryViewHolder extends RecyclerView.ViewHolder {
        TextView memoryNameTextView;
        TextView memoryCaptionTextView;
        ImageView memoryImageView;

        public MemoryViewHolder(@NonNull View itemView) {
            super(itemView);
            memoryNameTextView = itemView.findViewById(R.id.titleTextView);
            memoryCaptionTextView = itemView.findViewById(R.id.captionTextView);
            memoryImageView = itemView.findViewById(R.id.selectImageView);
        }
    }
}
