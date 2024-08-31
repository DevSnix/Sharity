package com.test.sharity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class ViewCampaignActivity extends AppCompatActivity {

    private ImageView ivCampaignImage;
    private TextView tvCampaignTitle, tvCampaignDescription, tvStartDate, tvEndDate, tvProgress;
    private Button btnDonateNow;
    private ProgressBar progressBar;
    private int licenseNumber;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_campaign_activity);

        // Initialize views
        ivCampaignImage = findViewById(R.id.ivCampaignImage);
        tvCampaignTitle = findViewById(R.id.tvCampaignTitle);
        tvCampaignDescription = findViewById(R.id.tvCampaignDescription);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.progressBar);
        btnDonateNow = findViewById(R.id.btnDonateNow);

        licenseNumber = getIntent().getIntExtra("licenseNumber", -1);
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        userType = sharedPreferences.getString("userType", "");

        // Load the campaign data
        loadCampaignData();

        btnDonateNow.setOnClickListener(v -> {
            if (userType.equals("Donor")) {
                // Start the SimulationPaymentActivity
                Intent intent = new Intent(ViewCampaignActivity.this, SimulationPaymentActivity.class);
                intent.putExtra("licenseNumber", licenseNumber);
                startActivity(intent);
            }
        });
    }

    private void loadCampaignData() {
        DatabaseReference campaignRef = FirebaseDatabase.getInstance().getReference("charities")
                .child(String.valueOf(licenseNumber)).child("campaign");

        campaignRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Campaign campaign = dataSnapshot.getValue(Campaign.class);
                    if (campaign != null) {
                        displayCampaignData(campaign);
                    }
                } else {
                    Toast.makeText(ViewCampaignActivity.this, "No campaign found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewCampaignActivity.this, "Failed to load campaign data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCampaignData(Campaign campaign) {
        // Set the campaign data to the views
        tvCampaignTitle.setText(campaign.getCampaignTitle());
        tvCampaignDescription.setText(campaign.getDescription());
        tvStartDate.setText("Start: " + campaign.getStartDate());
        tvEndDate.setText("End: " + campaign.getEndDate());

        // Load the campaign image using Glide
        Glide.with(this).load(campaign.getImageUrl()).into(ivCampaignImage);

        // Calculate the progress and update the progress bar
        double progress = (campaign.getCurrentAmount() / campaign.getTargetAmount()) * 100;
        progressBar.setProgress((int) progress);
        tvProgress.setText("$" + campaign.getCurrentAmount() + " / $" + campaign.getTargetAmount());
    }
}
