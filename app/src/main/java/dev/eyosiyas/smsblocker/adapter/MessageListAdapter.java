package dev.eyosiyas.smsblocker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.eyosiyas.smsblocker.R;
import dev.eyosiyas.smsblocker.model.Message;
import dev.eyosiyas.smsblocker.util.Core;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private List<Message> messages;

    public MessageListAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentVH(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedVH(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentVH) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedVH) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getType().equalsIgnoreCase("received"))
            return VIEW_TYPE_MESSAGE_RECEIVED;
        else
            return VIEW_TYPE_MESSAGE_SENT;
    }


    public class SentVH extends RecyclerView.ViewHolder {
        private TextView message, time;

        public SentVH(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.txtSentMessage);
            time = itemView.findViewById(R.id.txtMessageSentTime);
        }

        void bind(Message sentMessage) {
            message.setText(sentMessage.getBody());
            time.setText(Core.getReadableTime(sentMessage.getTimestamp()));
        }
    }

    public class ReceivedVH extends RecyclerView.ViewHolder {
        private ImageView profile; // TODO: 9/10/2020 Acquire profile picture
        private TextView message, time;

        public ReceivedVH(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.SenderProfile);
            message = itemView.findViewById(R.id.txtSenderMessage);
            time = itemView.findViewById(R.id.txtSenderTimestamp);
        }

        void bind(Message receivedMessage) {
            message.setText(receivedMessage.getBody());
            time.setText(Core.getReadableTime(receivedMessage.getTimestamp()));
        }
    }
}
