package com.test.sharity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

public class UpdateCharityProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private int charityLicenseNumber;
    private String stringCharityLicenseNumber;
    private DatabaseReference charityRef;
    ImageView imageUpdateCharityProfile;
    Button btnUpdateCharityProfile, btnSaveAndExit;
    EditText etCharityPassword, etCharityEmail, etCharityPhoneNumber, etCharityAddress, etCharityDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_charity_profile);
        // Initialize views
        imageUpdateCharityProfile = findViewById(R.id.imageUpdateCharityProfile);
        btnUpdateCharityProfile = findViewById(R.id.btnUpdateProfilePicture);
        btnSaveAndExit = findViewById(R.id.btnSaveAndExit);
        etCharityPassword = findViewById(R.id.etCharityPassword);
        etCharityEmail = findViewById(R.id.etCharityEmail);
        etCharityPhoneNumber = findViewById(R.id.etCharityPhoneNumber);
        etCharityAddress = findViewById(R.id.etCharityAddress);
        etCharityDescription = findViewById(R.id.etCharityDescription);
        SharedPreferences loggedCharityDetails = getSharedPreferences("CharityDetails", MODE_PRIVATE);
        charityLicenseNumber = loggedCharityDetails.getInt("charityLicenseNumber", -1);
        stringCharityLicenseNumber = String.valueOf(charityLicenseNumber);
        charityRef = FirebaseDatabase.getInstance().getReference("charities").child(stringCharityLicenseNumber);

        loadCharityDataFromDatabase();

        // Set click listeners for buttons
        btnUpdateCharityProfile.setOnClickListener(v -> openFileChooser());
        btnSaveAndExit.setOnClickListener(v -> sendProfileUpdateRequestToAdmin());
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
            imageUpdateCharityProfile.setImageURI(selectedImageUri); // Show the selected image
        }
    }

    private void loadCharityDataFromDatabase() {
        charityRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String charityProfilePictureUrl = dataSnapshot.child("imgUrl").getValue(String.class);
                String charityEmail = dataSnapshot.child("charityEmail").getValue(String.class);
                String charityPhoneNumber = dataSnapshot.child("charityPhoneNumber").getValue(String.class);
                String charityAddress = dataSnapshot.child("branchAddress").getValue(String.class);
                String charityDescription = dataSnapshot.child("charityDescription").getValue(String.class);

                etCharityEmail.setText(charityEmail);
                etCharityPhoneNumber.setText(charityPhoneNumber);
                etCharityAddress.setText(charityAddress);
                etCharityDescription.setText(charityDescription);
                loadCharityProfilePicture(charityProfilePictureUrl);
            }
        });
    }

    // ~~~~~IMPORTANT~~~~~ This method needs to be updated in a way that it sends the new details to the admin INSTEAD of saving to the database without a request.
    private void sendProfileUpdateRequestToAdmin() {
        String newCharityPassword = etCharityPassword.getText().toString();
        String newCharityEmail = etCharityEmail.getText().toString();
        String newCharityPhoneNumber = etCharityPhoneNumber.getText().toString();
        String newCharityAddress = etCharityAddress.getText().toString();
        String newCharityDescription = etCharityDescription.getText().toString();


        if (!newCharityPassword.isEmpty()) {
            charityRef.child("charityPassword").setValue(newCharityPassword);
        }

        if (!newCharityEmail.isEmpty()) {
            charityRef.child("userEmail").setValue(newCharityEmail);
        }

        if (!newCharityPhoneNumber.isEmpty()) {
            charityRef.child("userPhoneNumber").setValue(newCharityPhoneNumber);
        }

        if (!newCharityAddress.isEmpty()) {
            charityRef.child("branchAddress").setValue(newCharityAddress);
        }

        if (!newCharityDescription.isEmpty()) {
            charityRef.child("charityDescription").setValue(newCharityDescription);
        }

        if (selectedImageUri != null) {
            // Upload the selected image to Firebase Storage and update profilePictureUrl
            StorageReference profilePicRef = FirebaseStorage.getInstance().getReference("charity_images/" + stringCharityLicenseNumber + ".jpg");
            profilePicRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
                profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String newProfilePictureUrl = uri.toString();
                    charityRef.child("imgUrl").setValue(newProfilePictureUrl)
                            .addOnSuccessListener(aVoid -> {
                                // Only finish the activity after the profile picture has been successfully updated in the database
                                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                finish();  // Finish the activity after everything is successfully updated
                            });
                });
            });
        } else {
            finish();
        }
    }

    private void loadCharityProfilePicture(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(imageUpdateCharityProfile);
        }
    }
}
