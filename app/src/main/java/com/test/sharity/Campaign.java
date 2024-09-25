package com.test.sharity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Campaign {
    private int campaignId;
    private int charityId;
    private int userId;
    private double targetAmount;
    private double currentAmount;
    private String description;
    private String startDate;
    private String endDate;
    private String imageUrl;
    private boolean isActive;
    private String campaignTitle;

    public Campaign() {

    }

    public Campaign(double targetAmount, String description, String startDate, String endDate, String imageUrl, int charityId, int userId, String campaignTitle) {
        this.targetAmount = targetAmount;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
        this.isActive = true;
        this.currentAmount = 0.0;
        this.charityId = charityId;
        this.userId = userId;
        this.campaignTitle = campaignTitle;
        generateUniqueCampaignId();
    }

    private void generateUniqueCampaignId() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference campaignsRef = database.getReference("campaigns");
        String uniqueCampaignId = campaignsRef.push().getKey();  // Generate a unique key using push()
        Campaign.this.campaignId = uniqueCampaignId.hashCode();  // Convert the unique key to a hash code for an integer ID
    }


    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public int getCharityId() {
        return charityId;
    }

    public void setCharityId(int charityId) {
        this.charityId = charityId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCampaignTitle() {
        return campaignTitle;
    }

    public void setCampaignTitle(String campaignTitle) {
        this.campaignTitle = campaignTitle;
    }
}
