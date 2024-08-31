package com.test.sharity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CampaignFragment extends Fragment {

    private ImageView ivCampaignImage;
    private TextView tvCampaignTitle, tvCampaignDescription, tvStartDate, tvEndDate, tvProgress, tvNoCampaign;
    private ProgressBar progressBar;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.campaign_fragment, container, false);

        // Initialize views
        ivCampaignImage = view.findViewById(R.id.ivCampaignImage);
        tvCampaignTitle = view.findViewById(R.id.tvCampaignTitle);
        tvCampaignDescription = view.findViewById(R.id.tvCampaignDescription);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        tvProgress = view.findViewById(R.id.tvProgress);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoCampaign = view.findViewById(R.id.tvNoCampaign);

        // Retrieve the userId from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserDetails", getActivity().MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Load the campaign data
        loadCampaignData();

        return view;
    }

    private void loadCampaignData() {
        DatabaseReference campaignRef = FirebaseDatabase.getInstance().getReference("users")
                .child(String.valueOf(userId)).child("campaign");

        campaignRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Campaign campaign = dataSnapshot.getValue(Campaign.class);
                    if (campaign != null) {
                        displayCampaignData(campaign);
                    }
                } else {
                    showNoCampaignMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load campaign data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCampaignData(Campaign campaign) {
        // Hide the "No Campaign" message
        tvNoCampaign.setVisibility(View.GONE);

        // Show campaign details
        ivCampaignImage.setVisibility(View.VISIBLE);
        tvCampaignTitle.setVisibility(View.VISIBLE);
        tvCampaignDescription.setVisibility(View.VISIBLE);
        tvStartDate.setVisibility(View.VISIBLE);
        tvEndDate.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        tvProgress.setVisibility(View.VISIBLE);

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

    private void showNoCampaignMessage() {
        // Hide all the campaign-related views
        ivCampaignImage.setVisibility(View.GONE);
        tvCampaignTitle.setVisibility(View.GONE);
        tvCampaignDescription.setVisibility(View.GONE);
        tvStartDate.setVisibility(View.GONE);
        tvEndDate.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvProgress.setVisibility(View.GONE);

        // Show the "No Campaign" message
        tvNoCampaign.setVisibility(View.VISIBLE);
    }
}
