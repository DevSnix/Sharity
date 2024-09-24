package com.test.sharity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    private ImageView ivProfilePicture;
    private TextView tvUsername, tvEmail, tvPhoneNumber;
    private DatabaseReference userDatabase;
    private Button btnLogout, btnDeleteAccount, btnUpdateProfile, btnFAQ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        btnFAQ = view.findViewById(R.id.btnFAQ);


        // Retrieve user details from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        int userIdInt = sharedPreferences.getInt("userId", -1); // Retrieve as int
        String userId = String.valueOf(userIdInt); // Convert to String
        String userName = sharedPreferences.getString("userName", "Username");
        String userEmail = sharedPreferences.getString("userEmail", "Email");
        String phoneNumber = sharedPreferences.getString("phoneNumber", "05112233");

        // Set user details to TextViews
        tvUsername.setText(userName);
        tvEmail.setText(userEmail);
        tvPhoneNumber.setText(phoneNumber);

        if (userId != null) {
            // Initialize Firebase Database reference
            userDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Load profile picture
            loadProfilePictureFromDatabase(userId);

        }

        btnUpdateProfile.setOnClickListener(v -> {
            // Navigate to the UserUpdateProfileActivity
            Intent intent = new Intent(getActivity(), UserUpdateProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Clear SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Clears all stored data
            editor.apply();

            // Navigate to the LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears the activity stack
            startActivity(intent);

            // Finish the current activity
            getActivity().finish();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            // Create an AlertDialog to ask for confirmation
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes", (dialog, which) -> { // User clicked Yes, so delete the account
                        userDatabase.child("userStatus").setValue(false).addOnCompleteListener(task -> { //Set userStatus to false, clear shared preferences and navigate to login

                            // Clear SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();  // Clears all stored data
                            editor.apply();

                            // Navigate to the LoginActivity
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            // Finish the current activity
                            getActivity().finish();
                            Toast.makeText(getContext(), "Account deleted and logged out.", Toast.LENGTH_SHORT).show();

                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // User clicked No, just dismiss the dialog
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });

        btnFAQ.setOnClickListener(v -> {
            // Navigate to the FAQActivity
            Intent intent = new Intent(getActivity(), FAQActivity.class);
            startActivity(intent);
        });

        return view;
    }

    // This function loads the profile picture from the database and caches it using Glide
    private void loadProfilePictureFromDatabase(String userId) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userDatabase.child("profilePictureUrl").get().addOnSuccessListener(dataSnapshot -> {
            String profilePictureUrl = dataSnapshot.getValue(String.class);
            if (profilePictureUrl != null) {
                // Use Glide to load and cache the image
                Glide.with(this)
                        .load(profilePictureUrl)
                        .placeholder(R.drawable.default_profile_picture)
                        .circleCrop()
                        .into(ivProfilePicture);
            } else {
                // Load default image
                Glide.with(this)
                        .load(R.drawable.default_profile_picture)
                        .circleCrop()
                        .into(ivProfilePicture);
            }
        }).addOnFailureListener(e -> {
            // Handle failure, load default image
            Glide.with(this)
                    .load(R.drawable.default_profile_picture)
                    .circleCrop()
                    .into(ivProfilePicture);
        });
    }
}
