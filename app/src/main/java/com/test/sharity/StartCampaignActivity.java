package com.test.sharity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StartCampaignActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivCampaignImage;
    private EditText etCampaignTitle, etCampaignDescription, etTargetAmount, etLicenseNumber, etUserId, etEndDate;
    private Button btnStart;
    private Uri imageUri;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private int charityLicenseNumber;
    private String selectedEndDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_start_campaign);

        ivCampaignImage = findViewById(R.id.ivCampaignImage);
        etCampaignTitle = findViewById(R.id.etCampaignTitle);
        etCampaignDescription = findViewById(R.id.etCampaignDescription);
        etTargetAmount = findViewById(R.id.etTargetAmount);
        etLicenseNumber = findViewById(R.id.etLicenseNumber);
        etUserId = findViewById(R.id.etUserId);
        etEndDate = findViewById(R.id.etEndDate);
        btnStart = findViewById(R.id.btnStart);

        // Initialize Firebase storage reference
        storageReference = FirebaseStorage.getInstance().getReference("campaign_images");

        // Retrieve charity details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("CharityDetails", MODE_PRIVATE);
        charityLicenseNumber = sharedPreferences.getInt("charityLicenseNumber", -1);

        // Automatically set the license number in the EditText
        etLicenseNumber.setText(String.valueOf(charityLicenseNumber));

        ivCampaignImage.setOnClickListener(v -> openFileChooser());

        etEndDate.setOnClickListener(v -> showDatePickerDialog());

        btnStart.setOnClickListener(v -> {
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(StartCampaignActivity.this, "Campaign creation in progress", Toast.LENGTH_SHORT).show();
            } else {
                checkUserTypeAndStartCampaign();
                finish();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivCampaignImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                StartCampaignActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date in the EditText
                    selectedEndDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    etEndDate.setText(selectedEndDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void checkUserTypeAndStartCampaign() {
        String userIdStr = etUserId.getText().toString().trim();

        // Validate userId input
        if (TextUtils.isEmpty(userIdStr)) {
            Toast.makeText(this, "Please enter a User ID", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = Integer.parseInt(userIdStr);

        // Check if the userType is Donee
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(userId));
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("userType").getValue(String.class);
                    if ("Donee".equals(userType)) {
                        // User is a Donee, proceed with campaign creation
                        startCampaign();
                    } else {
                        // User is not a Donee, show dialog
                        showDonorErrorDialog();
                    }
                } else {
                    Toast.makeText(StartCampaignActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StartCampaignActivity.this, "Failed to check user type: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDonorErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Campaign cannot be started for a Donor. Please use a Donee ID.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void startCampaign() {
        String campaignTitle = etCampaignTitle.getText().toString().trim();
        String campaignDescription = etCampaignDescription.getText().toString().trim();
        String targetAmountStr = etTargetAmount.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(campaignTitle) || TextUtils.isEmpty(campaignDescription) ||
                TextUtils.isEmpty(targetAmountStr) || TextUtils.isEmpty(selectedEndDate) || imageUri == null) {
            Toast.makeText(this, "Please fill all fields, select an end date, and choose an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double targetAmount = Double.parseDouble(targetAmountStr);
        int userId = Integer.parseInt(etUserId.getText().toString().trim());

        // Upload image and save campaign data to Firebase
        uploadImageAndSaveCampaign(campaignTitle, campaignDescription, targetAmount, charityLicenseNumber, userId, selectedEndDate);
    }

    private void uploadImageAndSaveCampaign(String campaignTitle, String campaignDescription, double targetAmount, int licenseNumber, int userId, String endDate) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveCampaignToDatabase(campaignTitle, campaignDescription, targetAmount, licenseNumber, userId, imageUrl, endDate);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(StartCampaignActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void saveCampaignToDatabase(String campaignTitle, String campaignDescription, double targetAmount, int licenseNumber, int userId, String imageUrl, String endDate) {
        // Generate the current date for the start date
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Create Campaign object
        Campaign campaign = new Campaign(targetAmount, campaignDescription, startDate, endDate, imageUrl, licenseNumber, userId, campaignTitle);

        // Save campaign to both the user's and the charity's nodes in Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(userId)).child("campaign");
        DatabaseReference charityRef = FirebaseDatabase.getInstance().getReference("charities").child(String.valueOf(licenseNumber)).child("campaign");

        databaseReference.setValue(campaign)
                .addOnSuccessListener(aVoid -> {
                    charityRef.setValue(campaign)
                            .addOnSuccessListener(aVoid2 -> Toast.makeText(StartCampaignActivity.this, "Campaign started successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(StartCampaignActivity.this, "Failed to save campaign to charity: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(StartCampaignActivity.this, "Failed to start campaign: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
