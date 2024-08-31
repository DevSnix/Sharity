package com.test.sharity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private Context context;
    private List<Message> messagesList;

    public MessagesAdapter(Context context, List<Message> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messagesList.get(position);
        holder.textViewMessageTitle.setText(message.getTitle());
        holder.textViewMessageContent.setText(message.getContent());

        // Format timestamp to readable date and time
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(message.getTimestamp());
        holder.textViewMessageTimestamp.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessageTitle;
        TextView textViewMessageContent;
        TextView textViewMessageTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessageTitle = itemView.findViewById(R.id.textViewMessageTitle);
            textViewMessageContent = itemView.findViewById(R.id.textViewMessageContent);
            textViewMessageTimestamp = itemView.findViewById(R.id.textViewMessageTimestamp);
        }
    }
}
