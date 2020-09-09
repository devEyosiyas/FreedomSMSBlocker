package dev.eyosiyas.smsblocker.adapter;

import android.content.Context;
import android.content.Intent;
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
import dev.eyosiyas.smsblocker.view.DetailSmsActivity;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Message message = messages.get(position);
        if (!message.getSender().equals(message.getDisplayName()))
            holder.sender.setText(message.getDisplayName());
        else
            holder.sender.setText(message.getSender());
        holder.body.setText(message.getBody());
        holder.time.setText(Core.getReadableTime(message.getTimestamp()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DetailSmsActivity.class).putExtra("KEY", message.getSender()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView sender, body, time;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.UserProfile);
            sender = itemView.findViewById(R.id.txtSenderName);
            body = itemView.findViewById(R.id.txtMessageBody);
            time = itemView.findViewById(R.id.txtTime);
        }
    }
}