package com.test.sharity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CharityManagementActivity extends AppCompatActivity {

    Button btnLogout, btnUpdateCharityProfile, btnProfilePreview, btnUpdateMessage, btnViewDonations, btnMessagesSent, btnViewReviews, btnStartCampaign, btnViewCampaign, btnEndCampaign;
    TextView tvWelcome, tvProgress, tvRaised;
    ProgressBar progressBar;
    private int charityLicenseNumber;
    private String stringCharityLicenseNumber;
    private DatabaseReference charityRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_profile_management);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvProgress = findViewById(R.id.tvProgress);
        tvRaised = findViewById(R.id.tvRaised);
        progressBar = findViewById(R.id.progressBar);
        btnUpdateCharityProfile = findViewById(R.id.btnUpdateCharityProfile);
        btnProfilePreview = findViewById(R.id.btnProfilePreview);
        btnUpdateMessage = findViewById(R.id.btnUpdateMessage);
        btnViewDonations = findViewById(R.id.btnViewDonations);
        btnMessagesSent = findViewById(R.id.btnMessagesSent);
        btnViewReviews = findViewById(R.id.btnViewReviews);
        btnStartCampaign = findViewById(R.id.btnStartCampaign);
        btnViewCampaign = findViewById(R.id.btnViewCampaign);
        btnEndCampaign = findViewById(R.id.btnEndCampaign);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences loggedCharityDetails = getSharedPreferences("CharityDetails", MODE_PRIVATE);
        charityLicenseNumber = loggedCharityDetails.getInt("charityLicenseNumber", -1);
        stringCharityLicenseNumber = String.valueOf(charityLicenseNumber);
        charityRef = FirebaseDatabase.getInstance().getReference("charities").child(stringCharityLicenseNumber);

        welcomeCharity();
        checkRunningCampaign();
        calculateTotalRaised(); // Calculate total amount raised

        btnUpdateCharityProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateCharityProfileActivity.class);
            startActivity(intent);
        });

        btnProfilePreview.setOnClickListener(v -> {
            Intent intent = new Intent(this, CharityProfilePreviewActivity.class);
            startActivity(intent);
        });

        btnUpdateMessage.setOnClickListener(v -> {
            Intent intent = new Intent(this, CharitySendMessageActivity.class);
            startActivity(intent);
        });

        btnViewDonations.setOnClickListener(v -> {
            Intent intent = new Intent(this, CharityViewDonationsActivity.class);
            intent.putExtra("charityLicenseNumber", charityLicenseNumber);
            startActivity(intent);
        });

        btnMessagesSent.setOnClickListener(v -> {
            Intent intent = new Intent(this, CharityMessagesActivity.class);
            intent.putExtra("licenseNumber", charityLicenseNumber);
            startActivity(intent);
        });

        btnViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(this, CharityReviewsActivity.class);
            intent.putExtra("licenseNumber", charityLicenseNumber);
            startActivity(intent);
        });

        btnStartCampaign.setOnClickListener(v -> {
            charityRef.child("campaign").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        showRunningCampaignDialog();
                    } else {
                        Intent intent = new Intent(CharityManagementActivity.this, StartCampaignActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CharityManagementActivity.this, "Failed to check campaign status", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnViewCampaign.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewCampaignActivity.class);
            intent.putExtra("licenseNumber", charityLicenseNumber);
            startActivity(intent);
        });

        btnEndCampaign.setOnClickListener(v -> {
            charityRef.child("campaign").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Campaign campaign = dataSnapshot.getValue(Campaign.class);
                        if (campaign != null && campaign.isActive()) {
                            // Confirm if the user wants to end the campaign
                            new AlertDialog.Builder(CharityManagementActivity.this)
                                    .setTitle("End Campaign")
                                    .setMessage("Are you sure you want to end the current campaign?")
                                    .setPositiveButton("Yes", (dialog, which) -> endCampaign(campaign))
                                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                    .show();
                        }
                    } else {
                        // No campaign running
                        new AlertDialog.Builder(CharityManagementActivity.this)
                                .setTitle("No Campaign Running")
                                .setMessage("There is no campaign to end.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CharityManagementActivity.this, "Failed to check campaign status", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnLogout.setOnClickListener(v -> {
            // Clear shared preferences when logging out
            SharedPreferences sharedPreferences = getSharedPreferences("CharityDetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Navigate to the LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears the activity stack
            startActivity(intent);

            // Finish the current activity
            finish();
        });
    }

    // Welcome charity method which welcomes whichever charity is logged in, emphasizing the name of the charity with different color
    private void welcomeCharity() {
        charityRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String charityName = dataSnapshot.child("charityName").getValue(String.class);

                // Create the text "Welcome CharityName"
                String welcomeText = "Welcome " + charityName;

                // Create a SpannableString
                SpannableString spannableString = new SpannableString(welcomeText);

                // Apply color to "Welcome"
                spannableString.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)),
                        0, 7, // Index range for "Welcome"
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                // Apply a different color to "CharityName"
                spannableString.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(this, R.color.dark_orange)),
                        8, welcomeText.length(), // Index range for "CharityName"
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                // Set the spannable string to the TextView
                tvWelcome.setText(spannableString);
            }
        });
    }

    // Check if there is a running campaign and update the progress bar accordingly
    private void checkRunningCampaign() {
        DatabaseReference charityCampaignRef = charityRef.child("campaign");

        charityCampaignRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Campaign campaign = dataSnapshot.getValue(Campaign.class);
                    double progress = (campaign.getCurrentAmount() / campaign.getTargetAmount()) * 100;
                    progressBar.setProgress((int) progress);
                    tvProgress.setText("Current: $" + campaign.getCurrentAmount() + " / Target: $" + campaign.getTargetAmount());
                }
                else {
                    progressBar.setProgress(0);
                    tvProgress.setText("No running campaign");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CharityManagementActivity.this, "Failed to load campaign progress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show dialog when trying to start a new campaign while one is already running
    private void showRunningCampaignDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("The charity cannot run more than one campaign at a time.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // End campaign method when user clicks end campaign button
    private void endCampaign(Campaign campaign) {
        // Reference to the user's campaign in Firebase
        DatabaseReference userCampaignRef = FirebaseDatabase.getInstance().getReference("users")
                .child(String.valueOf(campaign.getUserId()))
                .child("campaign");

        // Delete the campaign from both charity and donee
        charityRef.child("campaign").removeValue().addOnSuccessListener(aVoid -> {
            userCampaignRef.removeValue().addOnSuccessListener(aVoid1 -> {
                Toast.makeText(CharityManagementActivity.this, "Campaign ended successfully!", Toast.LENGTH_SHORT).show();
                // Update the UI
                progressBar.setProgress(0);
                tvProgress.setText("No running campaign");
            }).addOnFailureListener(e -> {
                Toast.makeText(CharityManagementActivity.this, "Failed to remove campaign from donee: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(CharityManagementActivity.this, "Failed to end campaign: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Calculate the total amount raised by the charity from successful donations
    private void calculateTotalRaised() {
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference("donations");

        // Query the donations where licenseNumber matches the charity's license number
        Query charityDonationsQuery = donationsRef.orderByChild("licenseNumber").equalTo(charityLicenseNumber);

        charityDonationsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double totalRaised = 0.0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Donation donation = snapshot.getValue(Donation.class);
                    if (donation != null && donation.getDonationStatus()) {
                        totalRaised += donation.getAmount();
                    }
                }

                tvRaised.setText("Total Raised: $" + totalRaised);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CharityManagementActivity.this, "Failed to calculate total raised amount.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
