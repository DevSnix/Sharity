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

public class FollowedCharitiesFragment extends Fragment {

    private RecyclerView recyclerViewFollowedCharities;
    private CharityAdapter charityAdapter;
    private List<Charity> charityList;
    private DatabaseReference userRef;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followed_charities, container, false);

        recyclerViewFollowedCharities = view.findViewById(R.id.recyclerViewFollowedCharities);
        recyclerViewFollowedCharities.setLayoutManager(new LinearLayoutManager(getContext()));
        charityList = new ArrayList<>();
        charityAdapter = new CharityAdapter(getContext(), charityList);
        recyclerViewFollowedCharities.setAdapter(charityAdapter);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        userId = String.valueOf(sharedPreferences.getInt("userId", -1));
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        loadFollowedCharities();

        return view;
    }

    // Load followed charities from Firebase
    private void loadFollowedCharities() {
        userRef.child("followedCharities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> followedCharities = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Integer licenseNumber = snapshot.getValue(Integer.class);
                    followedCharities.add(licenseNumber);
                }
                loadCharitiesDetails(followedCharities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    // Load charity details from Firebase
    private void loadCharitiesDetails(List<Integer> followedCharities) {
        DatabaseReference charitiesRef = FirebaseDatabase.getInstance().getReference("charities");

        for (int licenseNumber : followedCharities) {
            charitiesRef.child(String.valueOf(licenseNumber)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Charity charity = dataSnapshot.getValue(Charity.class);
                    if (charity != null) {
                        charityList.add(charity);
                    }
                    charityAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }
}
