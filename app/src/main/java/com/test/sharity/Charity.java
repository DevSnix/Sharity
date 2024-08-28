package com.test.sharity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class Charity {
    private int licenseNumber;
    private String charityName;
    private String charityEmail;
    private String branchAddress;
    private String charityPassword;
    private String charityPhoneNumber;
    private String charityType;
    private String imgUrl;
    private String charityDescription;
    private Campaign campaign;
    private float rating;
    private HashMap<String, Review> reviews;

    //if charityStatus = false -> charity account is not active, otherwise it is active (true)
    private boolean charityStatus;

    public Charity(int licenseNumber, String charityName, String charityEmail, String branchAddress, String charityPassword, String charityPhoneNumber, String charityType, String imgUrl, String charityDescription) {
        this.licenseNumber = licenseNumber;
        this.charityName = charityName;
        this.charityEmail = charityEmail;
        this.branchAddress = branchAddress;
        this.charityPassword = charityPassword;
        this.charityPhoneNumber = charityPhoneNumber;
        this.charityType = charityType;
        this.imgUrl = imgUrl;
        this.charityDescription = charityDescription;
        this.rating = 0;
    }

    public Charity() {

    }

    public int getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(int licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getCharityName() {
        return charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public String getCharityEmail() {
        return charityEmail;
    }

    public void setCharityEmail(String charityEmail) {
        this.charityEmail = charityEmail;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getCharityPassword() {
        return charityPassword;
    }

    public void setCharityPassword(String charityPassword) {
        this.charityPassword = charityPassword;
    }

    public String getCharityPhoneNumber() {
        return charityPhoneNumber;
    }

    public void setCharityPhoneNumber(String charityPhoneNumber) {
        this.charityPhoneNumber = charityPhoneNumber;
    }

    public String getCharityType() {
        return charityType;
    }

    public void setCharityType(String charityType) {
        this.charityType = charityType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCharityDescription() {
        return charityDescription;
    }

    public void setCharityDescription(String charityDescription) {
        this.charityDescription = charityDescription;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isCharityStatus() {
        return charityStatus;
    }

    public void setCharityStatus(boolean charityStatus) {
        this.charityStatus = charityStatus;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public HashMap<String, Review> getReviews() {
        return reviews;
    }

    public void setReviews(HashMap<String, Review> reviews) {
        this.reviews = reviews;
    }

    //Register charity to Firebase Realtime Database
    public void registerCharity(int licenseNumber, String charityName, String charityEmail, String branchAddress, String charityPassword, String charityPhoneNumber, String charityType, String imgUrl, String charityDescription) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference charitiesRef = database.getReference("charities");

        charitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(String.valueOf(licenseNumber))) {
                    // Charity with this license number already exists, do nothing
                    return;
                } else {
                    Charity.this.licenseNumber = licenseNumber;
                    Charity.this.charityName = charityName;
                    Charity.this.charityEmail = charityEmail;
                    Charity.this.branchAddress = branchAddress;
                    Charity.this.charityPassword = charityPassword;
                    Charity.this.charityPhoneNumber = charityPhoneNumber;
                    Charity.this.charityType = charityType;
                    Charity.this.imgUrl = imgUrl;
                    Charity.this.charityDescription = charityDescription;
                    Charity.this.rating = 0;
                    Charity.this.charityStatus = false;
                    Charity.this.campaign = null;
                    saveToFirebase();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void saveToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference charitiesRef = database.getReference("charities");
        charitiesRef.child(String.valueOf(this.licenseNumber)).setValue(this);
    }

}
