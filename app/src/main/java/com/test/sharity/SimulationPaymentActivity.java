package com.test.sharity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SimulationPaymentActivity extends Activity {

    private EditText editTextAmount;
    private Button buttonConfirmPayment;
    private Button buttonCancelPayment;
    private CheckBox checkBoxAnonymous;
    private CheckBox checkBoxCampaign;
    private CheckBox checkBoxReminder;
    private DatePicker datePickerReminder;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;

    private int licenseNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_payment);

        editTextAmount = findViewById(R.id.editTextAmount);
        buttonConfirmPayment = findViewById(R.id.buttonConfirmPayment);
        buttonCancelPayment = findViewById(R.id.buttonCancelPayment);
        checkBoxAnonymous = findViewById(R.id.checkBoxAnonymous);
        checkBoxCampaign = findViewById(R.id.checkBoxCampaign);
        checkBoxReminder = findViewById(R.id.checkBoxReminder);
        datePickerReminder = findViewById(R.id.datePickerReminder);

        licenseNumber = getIntent().getIntExtra("licenseNumber", -1);

        buttonConfirmPayment.setOnClickListener(v -> {
            String amountText = editTextAmount.getText().toString();

            // Ensure the amount is a valid double and non-negative
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    Toast.makeText(SimulationPaymentActivity.this, "Please enter a positive amount.", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isAnonymous = checkBoxAnonymous.isChecked();
                    boolean isCampaign = checkBoxCampaign.isChecked();
                    handlePayment(true, amount, isAnonymous, isCampaign);
                }
            } else {
                Toast.makeText(SimulationPaymentActivity.this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancelPayment.setOnClickListener(v -> {
            handlePayment(false, Double.parseDouble(editTextAmount.getText().toString()), false, false);
        });

        checkBoxReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show the DatePicker if the reminder is checked
                datePickerReminder.setVisibility(View.VISIBLE);
            } else {
                // Hide the DatePicker if the reminder is unchecked
                datePickerReminder.setVisibility(View.GONE);
            }
        });
    }

    private void handlePayment(boolean paymentStatus, double amount, boolean isAnonymous, boolean isCampaign) {
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

                    // If it's a campaign donation, update the currentAmount
                    if (isCampaign) {
                        updateCampaignAmount(licenseNumber, amount);
                    }

                    // If the user wants a reminder, check the checkbox and request permission
                    if (checkBoxReminder.isChecked()) {
                        requestNotificationPermission();  // Request notification permission
                        // After permission is granted, schedule the reminder
                        if (ContextCompat.checkSelfPermission(SimulationPaymentActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                            scheduleReminder();  // Schedule the reminder for the selected date
                        }
                    }

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

    private void scheduleReminder() {
        // Get the selected date from the DatePicker
        int day = datePickerReminder.getDayOfMonth();
        int month = datePickerReminder.getMonth();
        int year = datePickerReminder.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 9, 0); // Remind at 9 AM on the selected date

        // Create an intent for the notification
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Schedule the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void updateCampaignAmount(int licenseNumber, double amount) {
        // Retrieve the campaign from the charity's node using the license number
        DatabaseReference charityCampaignRef = FirebaseDatabase.getInstance().getReference("charities")
                .child(String.valueOf(licenseNumber)).child("campaign");

        charityCampaignRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Save the campaign object to a variable
                    Campaign campaign = dataSnapshot.getValue(Campaign.class);

                    if (campaign != null) {
                        // Extract the doneeUserId from the campaign
                        int doneeUserId = campaign.getUserId();

                        // Update the campaign's currentAmount in the donee's node
                        DatabaseReference doneeCampaignRef = FirebaseDatabase.getInstance().getReference("users")
                                .child(String.valueOf(doneeUserId)).child("campaign");

                        doneeCampaignRef.child("currentAmount").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double currentAmount = snapshot.exists() ? snapshot.getValue(Double.class) : 0.0;
                                double updatedAmount = currentAmount + amount;
                                doneeCampaignRef.child("currentAmount").setValue(updatedAmount);

                                // Also update the currentAmount in the charity's campaign node
                                charityCampaignRef.child("currentAmount").setValue(updatedAmount);
                                Toast.makeText(SimulationPaymentActivity.this, "Donated successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle the error
                            }
                        });
                    }
                } else {
                    // Handle case where campaign doesn't exist
                    Toast.makeText(SimulationPaymentActivity.this, "Campaign not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }

    private void addDonationToDonor(int userId, Donation donation) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(userId));

        // Save the donation under the user's donations using the same donationId
        userRef.child("userDonations").child(donation.getDonationId()).setValue(donation);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }
}

