package com.test.sharity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CharityManagementActivity extends AppCompatActivity {

    Button btnLogout, btnUpdateCharityProfile, btnProfilePreview, btnUpdateMessage, btnViewDonations, btnMessagesSent, btnViewReviews;
    TextView tvWelcome;
    private int charityLicenseNumber;
    private String stringCharityLicenseNumber;
    private DatabaseReference charityRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_profile_management);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnUpdateCharityProfile = findViewById(R.id.btnUpdateCharityProfile);
        btnProfilePreview = findViewById(R.id.btnProfilePreview);
        btnUpdateMessage = findViewById(R.id.btnUpdateMessage);
        btnViewDonations = findViewById(R.id.btnViewDonations);
        btnMessagesSent = findViewById(R.id.btnMessagesSent);
        btnViewReviews = findViewById(R.id.btnViewReviews);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences loggedCharityDetails = getSharedPreferences("CharityDetails", MODE_PRIVATE);
        charityLicenseNumber = loggedCharityDetails.getInt("charityLicenseNumber", -1);
        stringCharityLicenseNumber = String.valueOf(charityLicenseNumber);
        charityRef = FirebaseDatabase.getInstance().getReference("charities").child(stringCharityLicenseNumber);

        welcomeCharity();

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

}
