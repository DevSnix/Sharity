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

    public void registerUser(String userName, String userPassword, String userType, String userEmail, String userPhoneNumber, String userAddress) {
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

    //Save user to Firebase Realtime Database
    public void saveToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.child(String.valueOf(this.userId)).setValue(this);
    }

    //Login user to application, checks if user email exists in the process
    public static void login(String userEmail, String userPassword, Activity activity) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.orderByChild("userEmail").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);

                        if (user != null && user.getUserPassword().equals(userPassword)) {
                            Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.putExtra("userId", user.getUserId()); // Pass the user ID to MainActivity
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            Toast.makeText(activity, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(activity, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(activity, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
