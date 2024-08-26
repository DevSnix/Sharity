package com.test.sharity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserUpdateProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private EditText etUserPassword, etUserEmail, etUserPhoneNumber, etUserAddress;
    private Button btnUpdateProfilePicture, btnSaveAndExit;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private int userIdInt;
    private String userId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_profile);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        etUserPassword = findViewById(R.id.etUserPassword);
        etUserEmail = findViewById(R.id.etUserEmail);
        etUserPhoneNumber = findViewById(R.id.etUserPhoneNumber);
        etUserAddress = findViewById(R.id.etUserAddress);
        btnUpdateProfilePicture = findViewById(R.id.btnUpdateProfilePicture);
        btnSaveAndExit = findViewById(R.id.btnSaveAndExit);

        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        userIdInt = sharedPreferences.getInt("userId", -1);
        userId = String.valueOf(userIdInt); // Convert to String
        Log.d("UserUpdateProfileActivity", "Retrieved userId: $userId, Type: ${userId?.javaClass}");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);


        loadUserDataFromDatabase();

        // Handle profile picture update
        btnUpdateProfilePicture.setOnClickListener(v -> openFileChooser());

        // Handle save and exit
        btnSaveAndExit.setOnClickListener(v -> {
            saveUserDataToDatabase();
        });
    }

    // Open file chooser to select an image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ivProfilePicture.setImageURI(selectedImageUri); // Show the selected image
        }
    }

    // Load user data from Firebase Realtime Database using the userId
    private void loadUserDataFromDatabase() {
        userRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String userEmail = dataSnapshot.child("userEmail").getValue(String.class);
                String userPhoneNumber = dataSnapshot.child("userPhoneNumber").getValue(String.class);
                String userAddress = dataSnapshot.child("userAddress").getValue(String.class);
                String profilePictureUrl = dataSnapshot.child("profilePictureUrl").getValue(String.class);

                etUserEmail.setText(userEmail);
                etUserPhoneNumber.setText(userPhoneNumber);
                etUserAddress.setText(userAddress);

                // Display the current profile picture if available
                if (profilePictureUrl != null) {
                    Glide.with(this)
                            .load(profilePictureUrl)
                            .placeholder(R.drawable.default_profile_picture)
                            .circleCrop()
                            .into(ivProfilePicture);
                } else {
                    // Load default profile picture
                    Glide.with(this)
                            .load(R.drawable.default_profile_picture)
                            .circleCrop()
                            .into(ivProfilePicture);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
        });
    }

    // Save updated user data to Firebase Realtime Database
    private void saveUserDataToDatabase() {
        String newPassword = etUserPassword.getText().toString();
        String newEmail = etUserEmail.getText().toString();
        String newPhoneNumber = etUserPhoneNumber.getText().toString();
        String newAddress = etUserAddress.getText().toString();

        // Update the user's data in the Realtime Database
        // Check if fields are not empty before updating them in Firebase
        if (!newEmail.isEmpty()) {
            userRef.child("userEmail").setValue(newEmail);
        }

        if (!newPhoneNumber.isEmpty()) {
            userRef.child("userPhoneNumber").setValue(newPhoneNumber);
        }

        if (!newAddress.isEmpty()) {
            userRef.child("userAddress").setValue(newAddress);
        }

        if (!newPassword.isEmpty()) {
            userRef.child("userPassword").setValue(newPassword);
        }

        if (selectedImageUri != null) {
            // Upload the selected image to Firebase Storage and update profilePictureUrl
            StorageReference profilePicRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + userId + ".jpg");
            profilePicRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
                profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String newProfilePictureUrl = uri.toString();
                    userRef.child("profilePictureUrl").setValue(newProfilePictureUrl);
                });
            });
        }

        // Finish the activity after saving
        finish();
    }
}