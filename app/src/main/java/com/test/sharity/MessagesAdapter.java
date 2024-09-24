package com.test.sharity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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

        // Check if there is an image URL, load image using Picasso if available
        if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
            holder.imageViewMessageImage.setVisibility(View.VISIBLE);
            Picasso.get().load(message.getImageUrl()).into(holder.imageViewMessageImage);
        } else {
            holder.imageViewMessageImage.setVisibility(View.GONE);  // Hide ImageView if no image
        }
    }

    // Return the number of items in the list
    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessageTitle;
        TextView textViewMessageContent;
        TextView textViewMessageTimestamp;
        ImageView imageViewMessageImage;  // New ImageView for message image

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessageTitle = itemView.findViewById(R.id.textViewMessageTitle);
            textViewMessageContent = itemView.findViewById(R.id.textViewMessageContent);
            textViewMessageTimestamp = itemView.findViewById(R.id.textViewMessageTimestamp);
            imageViewMessageImage = itemView.findViewById(R.id.imageViewMessageImage);  // ImageView reference
        }
    }
}
