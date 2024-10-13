package com.example.memoria;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Model.Message;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    private List<Message> messages;

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSender() ? VIEW_TYPE_SENDER : VIEW_TYPE_RECEIVER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sender_message, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receiver_message, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).bind(message.getMessage());
        } else {
            ((ReceiverViewHolder) holder).bind(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessageTextView;

        SenderViewHolder(View itemView) {
            super(itemView);
            senderMessageTextView = itemView.findViewById(R.id.senderMessageTextView);
        }

        void bind(String message) {
            senderMessageTextView.setText(message);
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMessageTextView;

        ReceiverViewHolder(View itemView) {
            super(itemView);
            receiverMessageTextView = itemView.findViewById(R.id.receiverMessageTextView);
        }

        void bind(String message) {
            receiverMessageTextView.setText(message);
        }
    }
}
