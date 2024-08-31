package com.test.sharity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CharityViewDonationsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDonations;
    private CharityDonationsAdapter donationsAdapter;
    private List<Donation> donationList;
    private TextView tvTotalAmountRaised;
    private DatabaseReference donationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_view_donations);

        recyclerViewDonations = findViewById(R.id.recyclerViewDonations);
        tvTotalAmountRaised = findViewById(R.id.tvTotalAmountRaised);

        donationList = new ArrayList<>();
        donationsAdapter = new CharityDonationsAdapter(donationList);
        recyclerViewDonations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDonations.setAdapter(donationsAdapter);

        loadDonations();
    }

    private void loadDonations() {
        donationsRef = FirebaseDatabase.getInstance().getReference("donations");

        // Replace with the actual charity license number or retrieve it dynamically
        int charityLicenseNumber = getIntent().getIntExtra("charityLicenseNumber", -1);

        // Query the donations where licenseNumber matches the charity's license number
        Query charityDonationsQuery = donationsRef.orderByChild("licenseNumber").equalTo(charityLicenseNumber);

        charityDonationsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                donationList.clear();
                double totalAmount = 0.0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Donation donation = snapshot.getValue(Donation.class);
                    if (donation != null && donation.getDonationStatus()) { // Only include donations with donationStatus == true
                        donationList.add(donation);
                        totalAmount += donation.getAmount();
                    }
                }

                donationsAdapter.notifyDataSetChanged();
                tvTotalAmountRaised.setText("Total Amount Raised: $" + totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
