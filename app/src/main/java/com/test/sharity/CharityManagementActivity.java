package com.test.sharity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CharityManagementActivity extends AppCompatActivity {

    Button btnLogout, btnUpdateCharityProfile, btnProfilePreview, btnUpdateMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_profile_management);

        btnUpdateCharityProfile = findViewById(R.id.btnUpdateCharityProfile);
        btnProfilePreview = findViewById(R.id.btnProfilePreview);
        btnUpdateMessage = findViewById(R.id.btnUpdateMessage);
        btnLogout = findViewById(R.id.btnLogout);

        btnUpdateCharityProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateCharityProfileActivity.class);
            startActivity(intent);
        });

        btnProfilePreview.setOnClickListener(v -> {
            Intent intent = new Intent(this, CharityProfilePreviewActivity.class);
            startActivity(intent);
        });

        btnUpdateMessage.setOnClickListener(v -> {
            Intent intent = new Intent(this, CharitySendMessageActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Clear shared preferences when logging out
            SharedPreferences sharedPreferences = getSharedPreferences("CharityDetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Navigate to the LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears the activity stack
            startActivity(intent);

            // Finish the current activity
            finish();
        });
    }

}
