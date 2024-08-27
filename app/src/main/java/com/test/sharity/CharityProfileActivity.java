package com.test.sharity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class CharityProfileActivity extends AppCompatActivity {

    private ImageView imageViewCharityProfile;
    private TextView textViewCharityName;
    private TextView textViewCharityType;
    private TextView textViewCharityRating;
    private TextView textViewCharityDescription;
    private Button buttonDonateNow, btnFollow;
    private Charity charity;
    private int licenseNumber;
    private DatabaseReference userRef;
    private String userId;
    private boolean isFollowing;

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
        btnFollow = findViewById(R.id.btnFollow);

        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userId = String.valueOf(sharedPreferences.getInt("userId", -1));
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        licenseNumber = getIntent().getIntExtra("licenseNumber", -1);
        if (licenseNumber != -1) {
            loadCharityDetails(licenseNumber);
        }

        // Set up donate now button click listener
        buttonDonateNow.setOnClickListener(v -> {
            Intent intent = new Intent(CharityProfileActivity.this, SimulationPaymentActivity.class);
            intent.putExtra("licenseNumber", licenseNumber);
            startActivity(intent);
        });

        checkIfUserIsFollowing();
        btnFollow.setOnClickListener(v -> toggleFollowStatus());
    }

    // Check if user is following the charity
    private void checkIfUserIsFollowing() {
        userRef.child("followedCharities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> followedCharities = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Integer followedCharity = snapshot.getValue(Integer.class);
                    followedCharities.add(followedCharity);
                }
                isFollowing = followedCharities.contains(licenseNumber);
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

    // Load charity details from firebase
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
                    loadCharityImage(charity.getImgUrl());
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
}
