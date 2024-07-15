package com.test.sharity;

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
 private float rating;
 private Donor charityDonors[];
 private Donation charityDonations[];
 private boolean charityStatus;

 public Charity(int licenseNumber, String charityName, String charityEmail, String branchAddress, String charityPassword, String charityPhoneNumber, String charityType, String imgUrl
 , String charityDescription) {
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
     this.charityDonors = new Donor[0];
     this.charityDonations = new Donation[0];
     this.charityStatus = false;
 }
    public Charity(String name, String description, String imageUrl, float rating) {
        this.charityName = name;
        this.charityDescription = description;
        this.imgUrl = imageUrl;
        this.rating = rating;
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

    public Donor[] getCharityDonors() {
        return charityDonors;
    }

    public void setCharityDonors(Donor[] charityDonors) {
        this.charityDonors = charityDonors;
    }

    public Donation[] getCharityDonations() {
        return charityDonations;
    }

    public void setCharityDonations(Donation[] charityDonations) {
        this.charityDonations = charityDonations;
    }

    public boolean isCharityStatus() {
        return charityStatus;
    }

    public void setCharityStatus(boolean charityStatus) {
        this.charityStatus = charityStatus;
    }
}
