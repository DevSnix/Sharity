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
    private boolean userStatus;

    public User() {

    }

    public User(String userName, String userPassword, String userType, String userEmail, String userPhoneNumber, String userAddress) {
        generateAndSaveUniqueUserId(userName, userPassword, userType, userEmail, userPhoneNumber, userAddress);
    }

    private void generateAndSaveUniqueUserId(String userName, String userPassword, String userType, String userEmail, String userPhoneNumber, String userAddress) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        Random rand = new Random();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int newUserId;
                do {
                    newUserId = rand.nextInt(100000);  // Generate a random user ID
                } while (snapshot.hasChild(String.valueOf(newUserId)));  // Check if the ID already exists

                userId = newUserId;
                User.this.userName = userName;
                User.this.userPassword = userPassword;
                User.this.userType = userType;
                User.this.userEmail = userEmail;
                User.this.userPhoneNumber = userPhoneNumber;
                User.this.userAddress = userAddress;
                userStatus = true;
                saveToFirebase();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void saveToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.child(String.valueOf(this.userId)).setValue(this);
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
}
