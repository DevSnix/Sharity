package com.test.sharity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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
            } else if (item.getTitle().equals("Donations")) {
                activeFragment = new UserDonationsHistoryFragment();
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
        if (!(activeFragment instanceof HomeFragment)) {
            activeFragment = new HomeFragment();
            loadFragment(activeFragment);
        } else {
            super.onBackPressed(); // Call super to handle default back behavior on HomeFragment}
        }

    }
}
