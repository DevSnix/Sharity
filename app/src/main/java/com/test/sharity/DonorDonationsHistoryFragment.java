package com.test.sharity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DonorDonationsHistoryFragment extends Fragment {

    private RecyclerView recyclerViewDonations;
    private DonationAdapter donationAdapter;
    private List<Donation> donationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.donation_history_fragment, container, false);
        recyclerViewDonations = view.findViewById(R.id.recyclerViewDonations);
        recyclerViewDonations.setLayoutManager(new LinearLayoutManager(getContext()));

        donationList = new ArrayList<>();
        donationAdapter = new DonationAdapter(donationList);
        recyclerViewDonations.setAdapter(donationAdapter);

        loadUserDonations();

        return view;
    }
    // Load user donations from Firebase
    private void loadUserDonations() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId == -1) {
            // Handle error: User ID not found
            return;
        }

        // Fetch donations from Firebase
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference("donations");
        donationsRef.keepSynced(true); // This ensures donations node is always available offline

        donationsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                donationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Donation donation = snapshot.getValue(Donation.class);
                    if (donation != null) {
                        donationList.add(donation);
                    }
                }
                donationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
