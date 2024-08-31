package com.test.sharity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class CharityProfilePreviewActivity extends AppCompatActivity {

    private ImageView imageViewCharityProfile;
    private TextView textViewCharityName;
    private TextView textViewCharityType;
    private TextView textViewCharityRating;
    private TextView textViewCharityDescription;
    private TextView textViewMostRecentReview;
    private TextView textViewSeeAllReviews;
    private Button btnNavigateToCharity, btnViewMessage;
    private int licenseNumber;
    private String stringLicenseNumber;
    private DatabaseReference charityRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_profile_preview);

        imageViewCharityProfile = findViewById(R.id.imageViewCharityProfile);
        textViewCharityName = findViewById(R.id.textViewCharityName);
        textViewCharityType = findViewById(R.id.textViewCharityType);
        textViewCharityRating = findViewById(R.id.textViewCharityRating);
        textViewCharityDescription = findViewById(R.id.textViewCharityDescription);
        textViewMostRecentReview = findViewById(R.id.textViewMostRecentReview);
        textViewSeeAllReviews = findViewById(R.id.textViewSeeAllReviews);
        btnNavigateToCharity = findViewById(R.id.btnNavigateToCharity);
        btnViewMessage = findViewById(R.id.btnViewMessage);

        SharedPreferences sharedPreferences = getSharedPreferences("CharityDetails", MODE_PRIVATE);
        licenseNumber = sharedPreferences.getInt("charityLicenseNumber", -1);
        stringLicenseNumber = String.valueOf(licenseNumber);
        charityRef = FirebaseDatabase.getInstance().getReference("charities").child(stringLicenseNumber);

        loadCharityDataFromDatabase();

        btnViewMessage.setOnClickListener(v -> {
            Intent intent = new Intent(CharityProfilePreviewActivity.this, CharityMessagesActivity.class);
            intent.putExtra("licenseNumber", licenseNumber); // Pass the license number to the next activity
            startActivity(intent);
        });

        textViewSeeAllReviews.setOnClickListener(v -> {
            Intent intent = new Intent(CharityProfilePreviewActivity.this, CharityReviewsActivity.class);
            intent.putExtra("licenseNumber", licenseNumber); // Pass the license number to the next activity
            startActivity(intent);
        });
    }

    private void loadCharityDataFromDatabase() {
        charityRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String charityProfilePictureUrl = dataSnapshot.child("imgUrl").getValue(String.class);
                String charityName = dataSnapshot.child("charityName").getValue(String.class);
                String charityType = dataSnapshot.child("charityType").getValue(String.class);
                double charityRating = dataSnapshot.child("rating").getValue(double.class);
                String charityDescription = dataSnapshot.child("charityDescription").getValue(String.class);

                textViewCharityName.setText(charityName);
                textViewCharityType.setText(charityType + " Charity");
                textViewCharityRating.setText("Rating: " + charityRating);
                textViewCharityDescription.setText(charityDescription);

                loadCharityProfilePicture(charityProfilePictureUrl);
                loadMostRecentReview();
            }
        });
    }

    private void loadCharityProfilePicture(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(imageViewCharityProfile);
        }
    }

    // Load most recent review
    private void loadMostRecentReview() {
        charityRef.child("reviews").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Display the most recent review
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    if (review != null) {
                        String reviewText = review.getReviewText() + "\nRating: " + review.getRating() + "\nBy: " + review.getUser().getUserName() + "\nDate: " + review.getDate();
                        textViewMostRecentReview.setText(reviewText);
                    }
                }

                // Calculate the average rating using an array to hold the total rating and review count
                final double[] totalRating = {0.0};
                final int[] reviewCount = {0};

                charityRef.child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot allReviewsSnapshot) {
                        for (DataSnapshot snapshot : allReviewsSnapshot.getChildren()) {
                            Review review = snapshot.getValue(Review.class);
                            if (review != null) {
                                totalRating[0] += review.getRating();
                                reviewCount[0]++;
                            }
                        }

                        if (reviewCount[0] > 0) {
                            double averageRating = totalRating[0] / reviewCount[0];
                            textViewCharityRating.setText(String.format(Locale.getDefault(), "Rating: %.1f (%d reviews)", averageRating, reviewCount[0]));
                        } else {
                            textViewCharityRating.setText("Rating: N/A");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CharityProfilePreviewActivity.this, "Failed to calculate average rating", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CharityProfilePreviewActivity.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
