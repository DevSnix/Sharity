package com.test.sharity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
    private List<Charity> filteredCharityList;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewCharities = view.findViewById(R.id.recyclerViewCharities);
        searchView = view.findViewById(R.id.searchView);

        recyclerViewCharities.setLayoutManager(new LinearLayoutManager(getContext()));
        charityList = new ArrayList<>();
        filteredCharityList = new ArrayList<>();
        charityAdapter = new CharityAdapter(getContext(), filteredCharityList);
        recyclerViewCharities.setAdapter(charityAdapter);

        loadCharities();

        // Set up the search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCharities(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCharities(newText);
                return false;
            }
        });

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
                    if (charity != null) {
                        charityList.add(charity);
                    }
                }
                filteredCharityList.clear();
                filteredCharityList.addAll(charityList); // Initially show all charities
                charityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    // Filter charities based on the search query
    private void filterCharities(String query) {
        filteredCharityList.clear();
        if (query.isEmpty()) {
            filteredCharityList.addAll(charityList);
        } else {
            for (Charity charity : charityList) {
                if (charity.getCharityName().toLowerCase().contains(query.toLowerCase()) ||
                        charity.getCharityType().toLowerCase().contains(query.toLowerCase())) {
                    filteredCharityList.add(charity);
                }
            }
        }
        charityAdapter.notifyDataSetChanged();
    }
}
