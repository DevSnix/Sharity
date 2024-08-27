package com.test.sharity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CharityReviewsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReviews;
    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviewList;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_reviews);

        backButton = findViewById(R.id.backButton);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));

        reviewList = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(this, reviewList);
        recyclerViewReviews.setAdapter(reviewsAdapter);

        backButton.setOnClickListener(v -> onBackPressed());

        int licenseNumber = getIntent().getIntExtra("licenseNumber", -1);
        if (licenseNumber != -1) {
            loadReviews(licenseNumber);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // This will finish the activity and go back
    }


    private void loadReviews(int licenseNumber) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("charities")
                .child(String.valueOf(licenseNumber)).child("reviews");

        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    reviewList.add(review);
                }
                reviewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}

