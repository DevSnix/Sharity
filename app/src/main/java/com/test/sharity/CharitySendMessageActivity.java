package com.test.sharity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CharitySendMessageActivity extends AppCompatActivity {

    private int licenseNumber;
    private String stringLicenseNumber;
    private DatabaseReference charityRef;
    private EditText etMessageTitle;
    private EditText etMessageContent;
    private Button btnSendMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_send_message);

        etMessageTitle = findViewById(R.id.et_message_title);
        etMessageContent = findViewById(R.id.et_message_content);
        btnSendMessage = findViewById(R.id.btn_send_message);
        SharedPreferences sharedPreferences = getSharedPreferences("CharityDetails", MODE_PRIVATE);
        licenseNumber = sharedPreferences.getInt("charityLicenseNumber", -1);
        stringLicenseNumber = String.valueOf(licenseNumber);
        charityRef = FirebaseDatabase.getInstance().getReference("charities").child(stringLicenseNumber);

        // Set click listener on the send button
        btnSendMessage.setOnClickListener(v -> sendMessageToFollowers());
    }

    private void sendMessageToFollowers() {
        // Get the message title and content from the EditTexts
        String messageTitle = etMessageTitle.getText().toString().trim();
        String messageContent = etMessageContent.getText().toString().trim();

        // Check if the message title and content are not empty
        if (!TextUtils.isEmpty(messageTitle) && !TextUtils.isEmpty(messageContent)) {
            // Get the current timestamp
            long timestamp = System.currentTimeMillis();

            // Create a message object or use a Map
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("title", messageTitle);
            messageData.put("content", messageContent);
            messageData.put("timestamp", timestamp);

            // Save the message data with the title and timestamp
            DatabaseReference messagesRef = charityRef.child("messages").push();
            messagesRef.setValue(messageData);

            // Notify the charity of success
            Toast.makeText(CharitySendMessageActivity.this, "Message sent successfully!", Toast.LENGTH_SHORT).show();

            // Clear the EditTexts after sending the message
            etMessageTitle.setText("");
            etMessageContent.setText("");

            // Exit the activity
            finish();
        } else {
            // Notify the charity that the message title or content is empty
            Toast.makeText(CharitySendMessageActivity.this, "Please enter a title and message before sending.", Toast.LENGTH_SHORT).show();
        }
    }
}
