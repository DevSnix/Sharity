package com.test.sharity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    private ImageView imageViewCharityProfile;
    private TextView textViewCharityName;
    private TextView textViewCharityType;
    private TextView textViewCharityRating;
    private TextView textViewCharityDescription;
    private Button buttonDonateNow;

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

        int licenseNumber = getIntent().getIntExtra("licenseNumber", -1);
        if (licenseNumber != -1) {
            loadCharityDetails(licenseNumber);
        }
    }

    private void loadCharityDetails(int licenseNumber) {
        DatabaseReference charityRef = FirebaseDatabase.getInstance().getReference("charities").child(String.valueOf(licenseNumber));
        charityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Charity charity = dataSnapshot.getValue(Charity.class);
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
}
