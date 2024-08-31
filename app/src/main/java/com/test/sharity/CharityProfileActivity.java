package com.test.sharity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CharityProfileActivity extends AppCompatActivity {

    private ImageView imageViewCharityProfile;
    private ImageView imageViewReportCharity;
    private TextView textViewCharityName;
    private TextView textViewCharityType;
    private TextView textViewCharityRating;
    private TextView textViewCharityDescription;
    private TextView textViewMostRecentReview;
    private TextView textViewSeeAllReviews;
    private EditText editTextReview;
    private RatingBar ratingBarReview;
    private Button buttonDonateNow, btnFollow, btnSubmitReview, btnViewMessage;
    private Charity charity;
    private int licenseNumber;
    private DatabaseReference userRef;
    private String userId;
    private boolean isFollowing;
    private DatabaseReference charityRef;
    private User currentUser;
    private Button btnViewCampaign;
    private String userType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_profile);

        imageViewReportCharity = findViewById(R.id.imageViewReportCharity);
        imageViewCharityProfile = findViewById(R.id.imageViewCharityProfile);
        textViewCharityName = findViewById(R.id.textViewCharityName);
        textViewCharityType = findViewById(R.id.textViewCharityType);
        textViewCharityRating = findViewById(R.id.textViewCharityRating);
        textViewCharityDescription = findViewById(R.id.textViewCharityDescription);
        textViewMostRecentReview = findViewById(R.id.textViewMostRecentReview);
        textViewSeeAllReviews = findViewById(R.id.textViewSeeAllReviews);
        editTextReview = findViewById(R.id.editTextReview);
        ratingBarReview = findViewById(R.id.ratingBarReview);
        buttonDonateNow = findViewById(R.id.buttonDonateNow);
        btnFollow = findViewById(R.id.btnFollow);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        btnViewMessage = findViewById(R.id.btnViewMessage);
        btnViewCampaign = findViewById(R.id.btnViewCampaign);


        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userId = String.valueOf(sharedPreferences.getInt("userId", -1));
        userType = sharedPreferences.getString("userType", "");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Fetch current user details
        fetchCurrentUser();

        licenseNumber = getIntent().getIntExtra("licenseNumber", -1);
        if (licenseNumber != -1) {
            loadCharityDetails(licenseNumber);
        }

        buttonDonateNow.setOnClickListener(v -> {
            if (userType.equals("Donor")) {
                Intent intent = new Intent(CharityProfileActivity.this, SimulationPaymentActivity.class);
                intent.putExtra("licenseNumber", licenseNumber);
                startActivity(intent);
            }
        });

        checkIfUserIsFollowing();
        btnFollow.setOnClickListener(v -> toggleFollowStatus());

        btnSubmitReview.setOnClickListener(v -> submitReview());

        textViewSeeAllReviews.setOnClickListener(v -> {
            Intent intent = new Intent(CharityProfileActivity.this, CharityReviewsActivity.class);
            intent.putExtra("licenseNumber", licenseNumber); // Pass the license number to the next activity
            startActivity(intent);
        });

        imageViewReportCharity.setOnClickListener(v -> showReportCharityDialog());

        btnViewMessage.setOnClickListener(v -> {
            Intent intent = new Intent(CharityProfileActivity.this, CharityMessagesActivity.class);
            intent.putExtra("licenseNumber", licenseNumber); // Pass the license number to the next activity
            startActivity(intent);
        });

        btnViewCampaign.setOnClickListener(v ->{
            Intent intent = new Intent(CharityProfileActivity.this, ViewCampaignActivity.class);
            intent.putExtra("licenseNumber", licenseNumber); // Pass the license number to the next activity
            startActivity(intent);
        });
    }

    // Fetch current user details from Firebase
    private void fetchCurrentUser() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CharityProfileActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Check if user is following the charity
    private void checkIfUserIsFollowing() {
        userRef.child("followedCharities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isFollowing = dataSnapshot.hasChild(String.valueOf(licenseNumber));
                updateFollowButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CharityProfileActivity.this, "Failed to load follow status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Toggle follow status
    private void toggleFollowStatus() {
        if (isFollowing) {
            unfollowCharity();
        } else {
            followCharity();
        }
    }

    // Follow charity
    private void followCharity() {
        userRef.child("followedCharities").child(String.valueOf(licenseNumber)).setValue(licenseNumber)
                .addOnSuccessListener(aVoid -> {
                    isFollowing = true;
                    updateFollowButton();
                    Toast.makeText(CharityProfileActivity.this, "Followed " + charity.getCharityName(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(CharityProfileActivity.this, "Failed to follow charity", Toast.LENGTH_SHORT).show());
    }

    // Unfollow charity
    private void unfollowCharity() {
        userRef.child("followedCharities").child(String.valueOf(licenseNumber)).removeValue()
                .addOnSuccessListener(aVoid -> {
                    isFollowing = false;
                    updateFollowButton();
                    Toast.makeText(CharityProfileActivity.this, "Unfollowed " + charity.getCharityName(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(CharityProfileActivity.this, "Failed to unfollow charity", Toast.LENGTH_SHORT).show());
    }

    // Update follow button text
    private void updateFollowButton() {
        if (isFollowing) {
            btnFollow.setText("Unfollow");
        } else {
            btnFollow.setText("Follow");
        }
    }

    // Load charity details from Firebase
    private void loadCharityDetails(int licenseNumber) {
        charityRef = FirebaseDatabase.getInstance().getReference("charities").child(String.valueOf(licenseNumber));
        charityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                charity = dataSnapshot.getValue(Charity.class);
                if (charity != null) {
                    textViewCharityName.setText(charity.getCharityName());
                    textViewCharityType.setText(charity.getCharityType() + " Charity");
                    textViewCharityRating.setText("Rating: " + charity.getRating());
                    textViewCharityDescription.setText(charity.getCharityDescription());
                    loadCharityImage(charity.getImgUrl());
                    loadMostRecentReview();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CharityProfileActivity.this, "Failed to load charity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load charity image
    private void loadCharityImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(CharityProfileActivity.this)
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
                        Toast.makeText(CharityProfileActivity.this, "Failed to calculate average rating", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CharityProfileActivity.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Submit a review which includes the current user
    private void submitReview() {
        // Check if the user has already submitted a review
        charityRef.child("reviews").orderByChild("user/userId").equalTo(currentUser.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User has already submitted a review
                    Toast.makeText(CharityProfileActivity.this, "You have already submitted a review for this charity.", Toast.LENGTH_SHORT).show();
                } else {
                    // User has not submitted a review, proceed with submission
                    String reviewText = editTextReview.getText().toString().trim();
                    double rating = ratingBarReview.getRating();

                    if (reviewText.isEmpty() || rating == 0.0) {
                        Toast.makeText(CharityProfileActivity.this, "Please enter a review and rating", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create the date string
                    String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                    // Create the Review object with the current date
                    Review review = new Review(reviewText, rating, currentUser, date);

                    // Submit the review to Firebase
                    charityRef.child("reviews").push().setValue(review)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(CharityProfileActivity.this, "Review submitted", Toast.LENGTH_SHORT).show();
                                loadMostRecentReview(); // Reload the most recent review after submission
                                editTextReview.setText(""); // Clear the input field
                                ratingBarReview.setRating(0.0f); // Reset the rating bar
                            })
                            .addOnFailureListener(e -> Toast.makeText(CharityProfileActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CharityProfileActivity.this, "Failed to check for existing review", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showReportCharityDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CharityProfileActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_report_charity, null);
        dialogBuilder.setView(dialogView);

        Spinner spinnerReportReason = dialogView.findViewById(R.id.spinnerReportReason);
        EditText editTextReportDetails = dialogView.findViewById(R.id.editTextReportDetails);
        Button buttonSubmitReport = dialogView.findViewById(R.id.buttonSubmitReport);

        // Populate the spinner with reasons
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.report_reasons_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReportReason.setAdapter(adapter);

        AlertDialog alertDialog = dialogBuilder.create();

        buttonSubmitReport.setOnClickListener(v -> {
            String selectedReason = spinnerReportReason.getSelectedItem().toString();
            String additionalDetails = editTextReportDetails.getText().toString().trim();

            // Handle the report submission (e.g., save to Firebase or send to admin)
            Toast.makeText(CharityProfileActivity.this, "Report submitted: " + selectedReason, Toast.LENGTH_SHORT).show();

            // Close the dialog
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

}
