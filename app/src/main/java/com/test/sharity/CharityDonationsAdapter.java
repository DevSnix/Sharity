package com.test.sharity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CharityDonationsAdapter extends RecyclerView.Adapter<CharityDonationsAdapter.DonationViewHolder> {

    private List<Donation> donationList;

    public CharityDonationsAdapter(List<Donation> donationList) {
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.charity_donation_item, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DonationViewHolder holder, int position) {
        Donation donation = donationList.get(position);

        // Set donation amount
        holder.tvDonationAmount.setText("Donation Amount: $" + donation.getAmount());

        // Set user ID
        holder.tvUserId.setText("User ID: " + donation.getUserId());

        // Handle anonymous donations
        if (donation.isAnonymous()) {
            holder.tvUserName.setText("User Name: Anonymous");
            holder.tvUserEmail.setText("User Email: Anonymous");
            holder.tvUserPhoneNumber.setText("User Phone Number: Anonymous");
        } else {
            // Fetch the donor details from Firebase using the userId
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(String.valueOf(donation.getUserId()));

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("userName").getValue(String.class);
                        String userEmail = dataSnapshot.child("userEmail").getValue(String.class);
                        String userPhoneNumber = dataSnapshot.child("userPhoneNumber").getValue(String.class);

                        holder.tvUserName.setText("User Name: " + (userName != null ? userName : "Unknown"));
                        holder.tvUserEmail.setText("User Email: " + (userEmail != null ? userEmail : "Unknown"));
                        holder.tvUserPhoneNumber.setText("User Phone Number: " + (userPhoneNumber != null ? userPhoneNumber : "Unknown"));
                    } else {
                        holder.tvUserName.setText("User Name: Unknown");
                        holder.tvUserEmail.setText("User Email: Unknown");
                        holder.tvUserPhoneNumber.setText("User Phone Number: Unknown");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors here
                }
            });
        }

        // Set donation date
        holder.tvDonationDate.setText("Donation Date: " + donation.getDonationDate());
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView tvDonationAmount, tvUserId, tvUserName, tvUserEmail, tvUserPhoneNumber, tvDonationDate;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDonationAmount = itemView.findViewById(R.id.tvDonationAmount);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserPhoneNumber = itemView.findViewById(R.id.tvUserPhoneNumber);
            tvDonationDate = itemView.findViewById(R.id.tvDonationDate);
        }
    }
}
