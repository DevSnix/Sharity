package com.test.sharity;

public class Donation {
    private double amount;
    private boolean donationStatus;
    private boolean isRepeatable;
    private int licenseNumber;
    private int userId;
    private String donationType;
    private boolean isAnonymous;
    private String donationMessage;
    private Donor donor;
    private Charity charity;

    // Default constructor for Firebase
    public Donation() {

    }

    // Parameterized constructor
    public Donation(double amount, boolean donationStatus, boolean isRepeatable, int licenseNumber, int userId, String donationType, boolean isAnonymous, String donationMessage, Donor donor, Charity charity) {
        this.amount = amount;
        this.donationStatus = donationStatus;
        this.isRepeatable = isRepeatable;
        this.licenseNumber = licenseNumber;
        this.userId = userId;
        this.donationType = donationType;
        this.isAnonymous = isAnonymous;
        this.donationMessage = donationMessage;
        this.donor = donor;
        this.charity = charity;
    }

    // Getters and setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isDonationStatus() {
        return donationStatus;
    }

    public void setDonationStatus(boolean donationStatus) {
        this.donationStatus = donationStatus;
    }

    public boolean isRepeatable() {
        return isRepeatable;
    }

    public void setRepeatable(boolean repeatable) {
        isRepeatable = repeatable;
    }

    public int getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(int licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDonationType() {
        return donationType;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getDonationMessage() {
        return donationMessage;
    }

    public void setDonationMessage(String donationMessage) {
        this.donationMessage = donationMessage;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public Charity getCharity() {
        return charity;
    }

    public void setCharity(Charity charity) {
        this.charity = charity;
    }
}
