package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.memoria.R;

import java.util.List;

import Model.Message;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<Message> messageList;
    private String currentUserName;  // The name of the current user

    public ChatAdapter(List<Message> messageList, String currentUserName) {
        this.messageList = messageList;
        this.currentUserName = currentUserName;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderName().equals(currentUserName)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sender_message, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receiver_message, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder for sent messages (current user)
    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessageTextView, senderNameTextView;

        public SentMessageViewHolder(View itemView) {
            super(itemView);
            senderMessageTextView = itemView.findViewById(R.id.senderMessageTextView);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
        }

        void bind(Message message) {
            senderMessageTextView.setText(message.getMessageText());
            senderNameTextView.setText(message.getSenderName());
        }
    }

    // ViewHolder for received messages (other users)
    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMessageTextView, receiverNameTextView;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            receiverMessageTextView = itemView.findViewById(R.id.receiverMessageTextView);
            receiverNameTextView = itemView.findViewById(R.id.receiverNameTextView);
        }

        void bind(Message message) {
            receiverMessageTextView.setText(message.getMessageText());
            receiverNameTextView.setText(message.getSenderName());
        }
    }
}
