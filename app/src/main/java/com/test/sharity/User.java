package com.test.sharity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Random;

public class User {
    private int userId;
    private String userName;
    private String userPassword;
    private String userType;
    private String userEmail;
    private String userPhoneNumber;
    private String userAddress;
    // if userStatus = false -> user is not active, otherwise it is active (true)
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
                userStatus = true;
                setDefaultPictureAndSaveUser();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
    //Retrieve default picture from database and set it to the user, then save the user to the database
    private void setDefaultPictureAndSaveUser() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pictures/default profile picture.png");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            setProfilePictureUrl(downloadUrl);
            saveToFirebase();
        }).addOnFailureListener(e -> {
            // Handle the failure
        });
    }
    //Save user to Firebase Realtime Database
    public void saveToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.child(String.valueOf(this.userId)).setValue(this);
    }

}
