package com.test.sharityapp;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhoneNumber;
    private Button btnViewActions;
    private ArrayList<String> userActions;
    private Button btnViewDonationHistory;
    private ArrayList<String> donationHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        btnViewActions = findViewById(R.id.btnViewActions);
        btnViewDonationHistory = findViewById(R.id.btnViewDonationHistory);

        // Fetch and display user details. Here, we use hardcoded values for demonstration
        // Replace this with actual user data fetching logic (e.g., from a database or API)
        tvUsername.setText("John Doe");
        tvEmail.setText("john.doe@example.com");
        tvPhoneNumber.setText("+1 234 567 890");

        // Sample user actions
        userActions = new ArrayList<>();
        userActions.add("Logged in");
        userActions.add("Updated profile picture");
        userActions.add("Changed password");
        userActions.add("Logged out");
        // Add more user actions as needed

        donationHistory = new ArrayList<>();
        donationHistory.add("Donated $50 on 01/01/2024");
        donationHistory.add("Donated $30 on 01/02/2024");
        donationHistory.add("Donated $100 on 01/03/2024");

       /* btnViewActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UserActionsActivity.class);
                intent.putStringArrayListExtra("userActions", userActions);
                startActivity(intent);
            }
        });
        */


        btnViewActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UserActionsActivity.class);
                intent.putStringArrayListExtra("actionData", userActions);
                intent.putExtra("actionType", "actions");
                startActivity(intent);
            }
        });

        btnViewDonationHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UserActionsActivity.class);
                intent.putStringArrayListExtra("actionData", donationHistory);
                intent.putExtra("actionType", "donation");
                startActivity(intent);
            }
        });

    }
}