package com.test.sharity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SimulationPaymentActivity extends Activity {

    private EditText editTextAmount;
    private Button buttonConfirmPayment;
    private Button buttonCancelPayment;
    private CheckBox checkBoxAnonymous;

    private int licenseNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_payment);

        editTextAmount = findViewById(R.id.editTextAmount);
        buttonConfirmPayment = findViewById(R.id.buttonConfirmPayment);
        buttonCancelPayment = findViewById(R.id.buttonCancelPayment);
        checkBoxAnonymous = findViewById(R.id.checkBoxAnonymous);

        licenseNumber = getIntent().getIntExtra("licenseNumber", -1);

        buttonConfirmPayment.setOnClickListener(v -> {
            double amount = Double.parseDouble(editTextAmount.getText().toString());
            boolean isAnonymous = checkBoxAnonymous.isChecked(); // Check if anonymous
            handlePayment(true, amount, isAnonymous); // Pass isAnonymous to handlePayment
        });

        buttonCancelPayment.setOnClickListener(v -> handlePayment(false, 0, false));
    }

    private void handlePayment(boolean paymentStatus, double amount, boolean isAnonymous) {
        // Retrieve the logged-in user's ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Generate a new unique donation ID using Firebase push
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference("donations");
        String donationId = donationsRef.push().getKey();

        // Retrieve the charity details based on the license number
        DatabaseReference charityRef = FirebaseDatabase.getInstance().getReference("charities").child(String.valueOf(licenseNumber));
        charityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Charity charity = dataSnapshot.getValue(Charity.class);
                if (charity != null) {
                    // Create the donation object and set the charity
                    Donation donation = new Donation();
                    String donationDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                    donation.setDonationId(donationId);
                    donation.setAmount(amount);
                    donation.setDonationStatus(paymentStatus);
                    donation.setRepeatable(false);
                    donation.setLicenseNumber(licenseNumber);
                    donation.setUserId(userId);
                    donation.setDonationType("One-time");
                    donation.setAnonymous(isAnonymous);
                    donation.setDonationMessage("Thank you for your support!");
                    donation.setCharity(charity);
                    donation.setDonationDate(donationDate);

                    // Save donation details to Firebase
                    donationsRef.child(donationId).setValue(donation);

                    // Add donation to donor's donation list
                    addDonationToDonor(userId, donation);

                    // Return the result to the CharityProfileActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("paymentStatus", paymentStatus);
                    resultIntent.putExtra("amount", amount);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(SimulationPaymentActivity.this, "Charity not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SimulationPaymentActivity.this, "Failed to retrieve charity data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addDonationToDonor(int userId, Donation donation) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(userId));

        // Save the donation under the user's donations using the same donationId
        userRef.child("userDonations").child(donation.getDonationId()).setValue(donation);
    }
}
