package com.test.sharityapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CharityActivity extends AppCompatActivity {

    private TextView tvCharityTitle, tvTotalDonations, tvAmountRemaining;
    private Button btnDonate;
    private FirebaseFirestore firestore;
    private CollectionReference charityDonationsRef;
    private String charityType;
    private static final int TOTAL_AMOUNT_NEEDED = 10000; // Example total amount needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_charity);


        tvCharityTitle = findViewById(R.id.tvCharityTitle);
        tvTotalDonations = findViewById(R.id.tvTotalDonations);
        tvAmountRemaining = findViewById(R.id.tvAmountRemaining);
        btnDonate = findViewById(R.id.btnDonate);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        charityDonationsRef = firestore.collection("charityDonations");

        // Get the charity type from the intent
        charityType = getIntent().getStringExtra("CHARITY_TYPE");

        // Set the title
        tvCharityTitle.setText(charityType);

        // Update charity details
        updateCharityDetails();

        btnDonate.setOnClickListener(view -> {
            Intent intent = new Intent(CharityActivity.this, DonationActivity.class);
            intent.putExtra("CHARITY_TYPE", charityType);
            startActivity(intent);
        });

    }

    private void updateCharityDetails() {
        charityDonationsRef
                .whereEqualTo("charityType", charityType)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalDonations = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming the donation amount is stored as a field in the document
                            int donationAmount = document.getLong("amount").intValue();
                            totalDonations += donationAmount;
                        }
                        int amountRemaining = TOTAL_AMOUNT_NEEDED - totalDonations;

                        // Update the UI with the total donations and amount remaining
                        tvTotalDonations.setText("Total Donations: $" + totalDonations);
                        tvAmountRemaining.setText("Amount Remaining: $" + amountRemaining);
                    }
                });
    }
}