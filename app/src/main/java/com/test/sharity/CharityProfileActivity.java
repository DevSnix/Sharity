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

        buttonDonateNow.setOnClickListener(v -> {
            // Open simulation payment activity
            Intent intent = new Intent(CharityProfileActivity.this, SimulationPaymentActivity.class);
            intent.putExtra("licenseNumber", licenseNumber); // Pass license number
            startActivity(intent); // No need to wait for a result
        });
    }

    // Load charity details from Firebase
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
                        loadCharityImage(imageUrl);
                    } else {
                        Toast.makeText(CharityProfileActivity.this, "Image URL is null or empty!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
    // Load charity image from Firebase Storage
    private void loadCharityImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(CharityProfileActivity.this)
                        .load(uri)
                        .into(imageViewCharityProfile);
            }).addOnFailureListener(e -> {
                Toast.makeText(CharityProfileActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            });
        }
    }

}
