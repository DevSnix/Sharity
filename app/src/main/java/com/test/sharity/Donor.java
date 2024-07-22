package com.test.sharity;

import java.util.List;

public class Donor extends User {
    private List<Donation> userDonations;
    private List<Charity> myCharities;

    // Default constructor for Firebase
    public Donor() {
    }

    // Constructor
    public Donor(String userName, String userPassword, String userType, String userEmail, String userPhoneNumber, String userAddress, List<Donation> userDonations, List<Charity> myCharities) {
        super(userName, userPassword, userType, userEmail, userPhoneNumber, userAddress);
        this.userDonations = userDonations;
        this.myCharities = myCharities;
    }

    // Getters and setters
    public List<Donation> getUserDonations() {
        return userDonations;
    }

    public void setUserDonations(List<Donation> userDonations) {
        this.userDonations = userDonations;
    }

    public List<Charity> getMyCharities() {
        return myCharities;
    }

    public void setMyCharities(List<Charity> myCharities) {
        this.myCharities = myCharities;
    }
}
