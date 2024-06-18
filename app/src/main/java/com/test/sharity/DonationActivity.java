package com.test.sharity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DonationActivity extends AppCompatActivity {

    private Button btnMaterialDonation;
    private Button btnPaypal;
    private Button btnGooglePay;

    //private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        //db = FirebaseFirestore.getInstance();

        btnMaterialDonation = findViewById(R.id.btnMaterialDonation);
        btnPaypal = findViewById(R.id.btnPaypal);
        btnGooglePay = findViewById(R.id.btnGooglePay);

        btnMaterialDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle material donation logic here
                // For now, just show a message
                Toast.makeText(DonationActivity.this, "Material Donation Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle PayPal donation logic
                processPayPalDonation();
            }
        });

        btnGooglePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Google Pay donation logic
                processGooglePayDonation();
            }
        });

    }

    private void processPayPalDonation() {
        // This is where you would integrate with the PayPal API
        // Simulate a successful donation for now
        addToDatabase("PayPal");
        Toast.makeText(this, "Donation Approved via PayPal", Toast.LENGTH_LONG).show();
    }

    private void processGooglePayDonation() {
        // This is where you would integrate with the Google Pay API
        // Simulate a successful donation for now
        addToDatabase("Google Pay");
        Toast.makeText(this, "Donation Approved via Google Pay", Toast.LENGTH_LONG).show();
    }

    private void addToDatabase(String method) {
        Map<String, Object> donation = new HashMap<>();
        donation.put("method", method);
        donation.put("amount", "50.00"); // This should be dynamic based on the user input

        /*db.collection("donations")
                .add(donation)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Donation added to database", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add donation", Toast.LENGTH_SHORT).show());
         */
    }
}