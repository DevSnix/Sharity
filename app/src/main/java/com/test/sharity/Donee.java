package com.test.sharity;

import java.util.ArrayList;
import java.util.List;

public class Donee extends User {
    private String donationRequestDescription;
    private String charityType;
    private Campaign campaign;
    private List<Charity> followedCharities;

    // Default constructor for Firebase
    public Donee() {
        this.followedCharities = new ArrayList<>();
    }

    // Constructor
    public Donee(String userName, String userPassword, String userType, String userEmail, String userPhoneNumber, String userAddress) {
        super(userName, userPassword, userType, userEmail, userPhoneNumber, userAddress);
        this.followedCharities = new ArrayList<>();
    }

    public List<Charity> getFollowedCharities() {
        return followedCharities;
    }

    public void setFollowedCharities(List<Charity> followedCharities) {
        this.followedCharities = followedCharities;
    }

    public String getCharityType() {
        return charityType;
    }

    public void setCharityType(String charityType) {
        this.charityType = charityType;
    }

    public String getDonationRequestDescription() {
        return donationRequestDescription;
    }

    public void setDonationRequestDescription(String donationRequestDescription) {
        this.donationRequestDescription = donationRequestDescription;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }
}
