package com.test.sharity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CharityProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PAYMENT = 1;

    private ImageView imageViewCharityProfile;
    private TextView textViewCharityName;
    private TextView textViewCharityType;
    private TextView textViewCharityRating;
    private TextView textViewCharityDescription;
    private Button buttonDonateNow;

    private Charity charity;
    private int licenseNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_profile);

        imageViewCharityProfile = findViewById(R.id.imageViewCharityProfile);
        textViewCharityName = findViewById(R.id.textViewCharityName);
        textViewCharityType = findViewById(R.id.textViewCharityType);
        textViewCharityRating = findViewById(R.id.textViewCharityRating);
        textViewCharityDescription = findViewById(R.id.textViewCharityDescription);
        buttonDonateNow = findViewById(R.id.buttonDonateNow);

        licenseNumber = getIntent().getIntExtra("licenseNumber", -1);
        if (licenseNumber != -1) {
            loadCharityDetails(licenseNumber);
        }

        buttonDonateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open simulation payment activity
                Intent intent = new Intent(CharityProfileActivity.this, SimulationPaymentActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
            }
        });
    }

    private void loadCharityDetails(int licenseNumber) {
        DatabaseReference charityRef = FirebaseDatabase.getInstance().getReference("charities").child(String.valueOf(licenseNumber));
        charityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                charity = dataSnapshot.getValue(Charity.class);
                if (charity != null) {
                    textViewCharityName.setText(charity.getCharityName());
                    textViewCharityType.setText(charity.getCharityType() + " Charity");
                    textViewCharityRating.setText("Rating: " + charity.getRating());
                    textViewCharityDescription.setText(charity.getCharityDescription());

                    String imageUrl = charity.getImgUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Log.d("CharityProfileActivity", "Image URL: " + imageUrl);
                        loadCharityImage(imageUrl);
                    } else {
                        Log.d("CharityProfileActivity", "Image URL is null or empty");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void loadCharityImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(CharityProfileActivity.this)
                        .load(uri)
                        .into(imageViewCharityProfile);
            }).addOnFailureListener(e -> {
                Log.e("CharityProfileActivity", "Failed to get download URL", e);
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT && resultCode == RESULT_OK) {
            boolean paymentStatus = data.getBooleanExtra("paymentStatus", false);
            double amount = data.getDoubleExtra("amount", 0.0);
            handlePaymentResult(paymentStatus, amount);
        }
    }

    private void handlePaymentResult(boolean paymentStatus, double amount) {
        // Retrieve the logged-in user's ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the logged-in donor's details from Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(userId));
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Donor donor = dataSnapshot.getValue(Donor.class);
                if (donor != null) {
                    // Collect donation details
                    Donation donation = new Donation();
                    donation.setAmount(amount);
                    donation.setDonationStatus(paymentStatus);
                    donation.setRepeatable(false);
                    donation.setLicenseNumber(licenseNumber); // Use actual license number
                    donation.setUserId(userId); // Use retrieved user ID
                    donation.setDonationType("One-time");
                    donation.setAnonymous(false);
                    donation.setDonationMessage("Thank you for your support!");
                    donation.setDonor(donor); // Set actual donor object
                    donation.setCharity(charity); // Use the loaded charity object

                    // Save donation details to Firebase
                    DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference("donations");
                    String donationId = donationsRef.push().getKey();
                    donationsRef.child(donationId).setValue(donation);

                    // Notify user
                    if (paymentStatus) {
                        Toast.makeText(CharityProfileActivity.this, "Donation successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CharityProfileActivity.this, "Donation failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
