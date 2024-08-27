package com.test.sharity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoneeRegisterRequestActivity extends AppCompatActivity {

    private Button btnSubmit;
    private EditText editTextDescription;
    private DatabaseReference userRef;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donee_request_description);

        btnSubmit = findViewById(R.id.btnSubmitForm);
        editTextDescription = findViewById(R.id.editTextDescription);

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        String stringUserId = String.valueOf(userId);

        if (stringUserId != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(stringUserId);
        }

        btnSubmit.setOnClickListener(view -> {
            String description = editTextDescription.getText().toString().trim();

            if (!description.isEmpty()) {
                saveDoneeRequest(description);

                // Navigate to login screen
                Intent intent = new Intent(DoneeRegisterRequestActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDoneeRequest(String description) {
        // Update the Donee's donationRequestDescription in Firebase
        if (userRef != null) {
            userRef.child("donationRequestDescription").setValue(description);
            Toast.makeText(this, "Request submitted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
        }
    }
}
