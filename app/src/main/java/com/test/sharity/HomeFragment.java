package com.test.sharity;

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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewCharities;
    private CharityAdapter charityAdapter;
    private List<Charity> charityList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewCharities = view.findViewById(R.id.recyclerViewCharities);
        recyclerViewCharities.setLayoutManager(new LinearLayoutManager(getContext()));
        charityList = new ArrayList<>();
        charityAdapter = new CharityAdapter(getContext(), charityList);
        recyclerViewCharities.setAdapter(charityAdapter);

        loadCharities();

        return view;
    }

    // Load charities from Firebase
    private void loadCharities() {
        DatabaseReference charitiesRef = FirebaseDatabase.getInstance().getReference("charities");
        charitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                charityList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Charity charity = snapshot.getValue(Charity.class);
                    charityList.add(charity);
                }
                charityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
