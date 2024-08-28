package com.test.sharity;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Campaign {
    private int campaignId;
    private double targetAmount;
    private double currentAmount;
    private String description;
    private String startDate;
    private String endDate;
    private String imageUrl;
    private boolean isActive;
    private Donee donee;

    public Campaign() {

    }

    public Campaign(double targetAmount, String description, String startDate, String endDate, String imageUrl, Donee donee) {
        this.targetAmount = targetAmount;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
        this.donee = donee;
        this.isActive = true;
        this.currentAmount = 0.0;
        generateUniqueCampaignId();
    }

    private void generateUniqueCampaignId() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference campaignsRef = database.getReference("campaigns");
        Random rand = new Random();
        campaignsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int uniqueCampaignId;
                do {
                    uniqueCampaignId = rand.nextInt(100000);  // Generate a random campaign ID between 0 - 100,000
                } while (snapshot.hasChild(String.valueOf(uniqueCampaignId)));
                Campaign.this.campaignId = uniqueCampaignId;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
