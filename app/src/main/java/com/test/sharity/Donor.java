package com.test.sharity;

import java.util.ArrayList;
import java.util.List;

public class Donor extends User {
    private List<Donation> userDonations;
    private List<Charity> followedCharities;

    // Default constructor for Firebase
    public Donor() {
        this.userDonations = new ArrayList<>();
        this.followedCharities = new ArrayList<>();
    }

    // Constructor
    public Donor(String userName, String userPassword, String userType, String userEmail, String userPhoneNumber, String userAddress) {
        super(userName, userPassword, userType, userEmail, userPhoneNumber, userAddress);
        this.userDonations = new ArrayList<>();
        this.followedCharities = new ArrayList<>();
    }

    // Getters and setters
    public List<Donation> getUserDonations() {
        return userDonations;
    }

    public void setUserDonations(List<Donation> userDonations) {
        this.userDonations = userDonations;
    }

    public List<Charity> getFollowedCharities() {
        return followedCharities;
    }

    public void setFollowedCharities(List<Charity> followedCharities) {
        this.followedCharities = followedCharities;
    }
}
