package com.test.sharity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviewList;

    public ReviewsAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    // Bind data to the view holder which is displayed in the recycler view
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        // Set review data
        holder.textViewReviewText.setText(review.getReviewText());
        holder.textViewReviewDate.setText(review.getDate());
        holder.textViewReviewRating.setText(String.valueOf(review.getRating()));
        holder.textViewUsername.setText(review.getUser().getUserName());

        // Load profile image with circular cropping using Glide
        String profilePictureUrl = review.getUser().getProfilePictureUrl();  // Assuming the User class has this method

        Glide.with(context)
                .load(profilePictureUrl)
                .circleCrop()  // Applies circular cropping
                .placeholder(R.drawable.default_profile_picture)  // Fallback image
                .into(holder.imageViewProfilePicture);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    // View holder class for the reviews which is displayed in the recycler view
    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProfilePicture;
        TextView textViewUsername, textViewReviewDate, textViewReviewRating, textViewReviewText;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfilePicture = itemView.findViewById(R.id.imageViewProfilePicture);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewReviewDate = itemView.findViewById(R.id.textViewReviewDate);
            textViewReviewRating = itemView.findViewById(R.id.textViewReviewRating);
            textViewReviewText = itemView.findViewById(R.id.textViewReviewText);
        }
    }
}
