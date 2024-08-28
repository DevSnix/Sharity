package com.test.sharity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class CharityAdapter extends RecyclerView.Adapter<CharityAdapter.CharityViewHolder> {

    private Context context;
    private List<Charity> charityList;

    public CharityAdapter(Context context, List<Charity> charityList) {
        this.context = context;
        this.charityList = charityList;
    }

    // Create a new ViewHolder which defines the views in the layout
    @NonNull
    @Override
    public CharityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.charity_item, parent, false);
        return new CharityViewHolder(view);
    }

    // Bind data to the views
    @Override
    public void onBindViewHolder(@NonNull CharityViewHolder holder, int position) {
        Charity charity = charityList.get(position);
        holder.textViewCharityName.setText(charity.getCharityName());
        holder.textViewCharityType.setText(charity.getCharityType());

        // Load the charity image using Glide
        Glide.with(context)
                .load(charity.getImgUrl())
                .into(holder.imageViewCharity);

        // Query Firebase for the reviews of this charity and calculate the average rating
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance()
                .getReference("charities")
                .child(String.valueOf(charity.getLicenseNumber()))
                .child("reviews");

        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double totalRating = 0.0;
                int reviewCount = 0;

                // Iterate through all reviews and calculate the average rating
                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null) {
                        totalRating += review.getRating();
                        reviewCount++;
                    }
                }

                if (reviewCount > 0) {
                    double averageRating = totalRating / reviewCount;
                    holder.textViewCharityRating.setText(String.format(Locale.getDefault(), "%.1f", averageRating));
                } else {
                    holder.textViewCharityRating.setText("No reviews yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.textViewCharityRating.setText("Error");
            }
        });

        // Handle the item click to open the CharityProfileActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CharityProfileActivity.class);
            intent.putExtra("licenseNumber", charity.getLicenseNumber());
            context.startActivity(intent);
        });
    }


    // Update the data in the adapter
    @Override
    public int getItemCount() {
        return charityList.size();
    }

    static class CharityViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewCharity;
        TextView textViewCharityName;
        TextView textViewCharityType;
        TextView textViewCharityRating;

        // Initialize views in the ViewHolder
        public CharityViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCharity = itemView.findViewById(R.id.imageViewCharity);
            textViewCharityName = itemView.findViewById(R.id.textViewCharityName);
            textViewCharityType = itemView.findViewById(R.id.textViewCharityType);
            textViewCharityRating = itemView.findViewById(R.id.textViewCharityRating);
        }
    }
}
