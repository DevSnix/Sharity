package com.test.sharity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

    private List<Donation> donationList;

    public DonationAdapter(List<Donation> donationList) {
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_item, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation donation = donationList.get(position);

        // Set the donation amount
        holder.textViewDonationAmount.setText("Amount: $" + donation.getAmount());

        // Check if Charity object is present in the donation
        if (donation.getCharity() != null) {
            holder.textViewDonationCharity.setText("Charity: " + donation.getCharity().getCharityName());
        } else {
            holder.textViewDonationCharity.setText("Charity: Not available");
        }

        // Set the donation message and status
        holder.textViewDonationMessage.setText("Message: " + donation.getDonationMessage());
        holder.textViewDonationStatus.setText("Payment Succeeded: " + (donation.getDonationStatus() ? "Yes" : "No"));
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDonationAmount;
        TextView textViewDonationCharity;
        TextView textViewDonationMessage;
        TextView textViewDonationStatus;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDonationAmount = itemView.findViewById(R.id.textViewDonationAmount);
            textViewDonationCharity = itemView.findViewById(R.id.textViewDonationCharity);
            textViewDonationMessage = itemView.findViewById(R.id.textViewDonationMessage);
            textViewDonationStatus = itemView.findViewById(R.id.textViewDonationStatus);
        }
    }
}
