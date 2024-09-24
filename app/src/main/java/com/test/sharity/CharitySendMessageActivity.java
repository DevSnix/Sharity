package com.test.sharity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class CharitySendMessageActivity extends AppCompatActivity {

    private int licenseNumber;
    private String stringLicenseNumber;
    private DatabaseReference charityRef;
    private EditText etMessageTitle;
    private EditText etMessageContent;
    private Button btnSendMessage;
    private ImageView backButton;
    private static final int GALLERY_REQUEST_CODE = 1;
    private ImageView imageViewSelectImage;
    private ImageView selectedImageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_send_message);

        etMessageTitle = findViewById(R.id.et_message_title);
        etMessageContent = findViewById(R.id.et_message_content);
        btnSendMessage = findViewById(R.id.btn_send_message);
        backButton = findViewById(R.id.backButton);
        SharedPreferences sharedPreferences = getSharedPreferences("CharityDetails", MODE_PRIVATE);
        licenseNumber = sharedPreferences.getInt("charityLicenseNumber", -1);
        stringLicenseNumber = String.valueOf(licenseNumber);
        charityRef = FirebaseDatabase.getInstance().getReference("charities").child(stringLicenseNumber);
        imageViewSelectImage = findViewById(R.id.imageViewSelectImage);
        selectedImageView = findViewById(R.id.selectedImageView);

        btnSendMessage.setOnClickListener(v -> sendMessageToFollowers());
        backButton.setOnClickListener(v -> onBackPressed());
        imageViewSelectImage.setOnClickListener(v -> openGallery());
    }

    private void sendMessageToFollowers() {
        String messageTitle = etMessageTitle.getText().toString().trim();
        String messageContent = etMessageContent.getText().toString().trim();

        if (!TextUtils.isEmpty(messageTitle) && !TextUtils.isEmpty(messageContent)) {
            long timestamp = System.currentTimeMillis();
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("title", messageTitle);
            messageData.put("content", messageContent);
            messageData.put("timestamp", timestamp);

            if (selectedImageUri != null) {
                // Upload the selected image to Firebase Storage, get the URL, and add it to the messageData
                uploadImageAndSendMessage(messageData);
            } else {
                // No image selected, just send the message
                DatabaseReference messagesRef = charityRef.child("messages").push();
                messagesRef.setValue(messageData);
                Toast.makeText(this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
                etMessageTitle.setText("");
                etMessageContent.setText("");
                finish();
            }
        } else {
            Toast.makeText(this, "Please enter a title and message before sending.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageAndSendMessage(Map<String, Object> messageData) {
        if (selectedImageUri != null) {
            // Reference to Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Create a unique name for the image using the current timestamp
            StorageReference imageRef = storageRef.child("message_images/" + System.currentTimeMillis() + ".jpg");

            // Upload the file to Firebase Storage
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL of the uploaded image
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();  // Get the actual URL

                            // Add the imageUrl to the message data
                            messageData.put("imageUrl", downloadUrl);  // Use the correct URL

                            // Save the message to Firebase Database
                            DatabaseReference messagesRef = charityRef.child("messages").push();
                            messagesRef.setValue(messageData);

                            // Notify the charity of success
                            Toast.makeText(CharitySendMessageActivity.this, "Message with image sent successfully!", Toast.LENGTH_SHORT).show();

                            // Clear the EditTexts and ImageView after sending the message
                            etMessageTitle.setText("");
                            etMessageContent.setText("");
                            selectedImageView.setVisibility(View.GONE); // Hide image after sending

                            // Exit the activity
                            finish();
                        }).addOnFailureListener(e -> {
                            // Handle any errors in getting the download URL
                            Toast.makeText(CharitySendMessageActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                            Log.e("Firebase", "Error getting image URL", e);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors in uploading the file
                        Toast.makeText(CharitySendMessageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error uploading image", e);
                    });
        } else {
            // No image selected, just send the message without an image
            DatabaseReference messagesRef = charityRef.child("messages").push();
            messagesRef.setValue(messageData);
            Toast.makeText(CharitySendMessageActivity.this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
            etMessageTitle.setText("");
            etMessageContent.setText("");
            finish();
        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed(); // This will finish the activity and go back
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            selectedImageView.setImageURI(selectedImageUri);
            selectedImageView.setVisibility(View.VISIBLE);
        }
    }

}
