package com.test.sharity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DoneeMainActivity extends AppCompatActivity {

    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donee_activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.donee_bottom_navigation);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);

        // Set up the default fragment
        if (savedInstanceState == null) {
            activeFragment = new HomeFragment();
            loadFragment(activeFragment);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getTitle().equals("Home")) {
                activeFragment = new HomeFragment();
            } else if (item.getTitle().equals("Followed")) {
                activeFragment = new FollowedCharitiesFragment();
            } else if(item.getTitle().equals("Campaign")) {
                activeFragment = new CampaignFragment();
            } else if (item.getTitle().equals("Profile")) {
                activeFragment = new ProfileFragment();
            }

            if (activeFragment != null) {
                loadFragment(activeFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        // Replace the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.donee_bottom_navigation);

        if (!(activeFragment instanceof HomeFragment)) {
            // Load the HomeFragment
            activeFragment = new HomeFragment();
            loadFragment(activeFragment);

            // Update the bottom navigation selected item to "Home"
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed(); // Handle the default back behavior
        }
    }

}
