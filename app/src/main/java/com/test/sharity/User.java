package com.test.sharity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class User {
    private int userId;
    private String userName;
    private String userPassword;
    private String userType;
    private String userEmail;
    private String userPhoneNumber;
    private String userAddress;
    // If userStatus = false -> user is not active, otherwise it is active (true)
    private boolean userStatus;
    private String profilePictureUrl;

    public User() {

    }

    public User(String userName, String userPassword, String userType, String userEmail, String userPhoneNumber, String userAddress) {
        registerUser(userName, userPassword, userType, userEmail, userPhoneNumber, userAddress);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public boolean isUserStatus() {
        return userStatus;
    }

    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    // Takes user input and registers the user
    public void registerUser(String userName, String userPassword, String userType, String userEmail, String userPhoneNumber, String userAddress) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        Random rand = new Random();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int newUserId;
                do {
                    newUserId = rand.nextInt(100000);  // Generate a random user ID between 0 - 100,000
                } while (snapshot.hasChild(String.valueOf(newUserId)));  // Check if the ID already exists

                userId = newUserId;
                User.this.userName = userName;
                User.this.userPassword = userPassword;
                User.this.userType = userType;
                User.this.userEmail = userEmail;
                User.this.userPhoneNumber = userPhoneNumber;
                User.this.userAddress = userAddress;
                if(userType.equalsIgnoreCase("Donor")) {
                    userStatus = true;
                }
                else if (userType.equalsIgnoreCase("Donee")) {
                    userStatus = false; //Account is not activated until admin grants approval of it
                }
                setDefaultPictureAndSaveUser();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    // Set a default picture URL directly and save the user to the database
    private void setDefaultPictureAndSaveUser() {
        // Using the provided URL for the default profile picture
        String defaultPictureUrl = "https://firebasestorage.googleapis.com/v0/b/sharity-5e06e.appspot.com/o/profile_pictures%2Fdefault%20profile%20picture.png?alt=media&token=55ab750a-6563-4278-b3fe-fd7a884f7692";

        // Set the default picture URL
        setProfilePictureUrl(defaultPictureUrl);

        // Save the user to Firebase
        saveToFirebase();
    }
    //Save user to Firebase Realtime Database
    public void saveToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.child(String.valueOf(this.userId)).setValue(this);
    }

}
