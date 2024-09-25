package com.test.sharity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etAddress;
    private EditText etPhoneNumber;
    private Button btnRegister;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etAddress = findViewById(R.id.etAddress);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnRegister = findViewById(R.id.btnRegister);

        // Initialize DatabaseReference for 'users' node
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        btnRegister.setOnClickListener(v -> {
            registerUser();
        });
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String selectedAccountType = getIntent().getStringExtra("accountType");

        // Clear previous errors
        etUsername.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        etAddress.setError(null);
        etPhoneNumber.setError(null);

        // Check if username is valid (non-empty, no spaces, and no special characters)
        if (TextUtils.isEmpty(username) || !isValidUsername(username)) {
            etUsername.setError("Username must be alphanumeric and cannot contain spaces or special characters");
            return;
        }

        // Check if email is valid
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email is required");
            return;
        }

        // Check if password is valid (at least 8 characters, includes letters, numbers, special characters)
        if (TextUtils.isEmpty(password) || !isValidPassword(password)) {
            etPassword.setError("Password must be at least 8 characters long, contain a mix of upper and lowercase letters, numbers, and special characters");
            return;
        }

        // Check if confirm password matches password
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Check if address is not too short or empty
        if (TextUtils.isEmpty(address) || address.length() < 5) {
            etAddress.setError("A valid address is required (minimum 5 characters)");
            return;
        }

        // Check if phone number is valid (only digits and at least 10 characters)
        if (TextUtils.isEmpty(phoneNumber) || !isValidPhoneNumber(phoneNumber)) {
            etPhoneNumber.setError("Valid phone number is required (10-12 digits)");
            return;
        }

        // Check if the email or phone number already exists in Firebase
        checkIfEmailOrPhoneExists(email, phoneNumber, selectedAccountType, username, password, address);
    }

    private void checkIfEmailOrPhoneExists(String email, String phoneNumber, String selectedAccountType, String username, String password, String address) {
        // Check if email exists
        usersRef.orderByChild("userEmail").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            etEmail.setError("Email is already in use");
                        } else {
                            // Check if phone number exists
                            usersRef.orderByChild("userPhoneNumber").equalTo(phoneNumber)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                etPhoneNumber.setError("Phone number is already in use");
                                            } else {
                                                // If both email and phone number are unique, proceed to register
                                                registerUserInFirebase(selectedAccountType, username, email, password, phoneNumber, address);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(RegisterActivity.this, "Database error occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegisterActivity.this, "Database error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUserInFirebase(String accountType, String username, String email, String password, String phoneNumber, String address) {
        if (accountType.equalsIgnoreCase("Donor")) {
            Donor newDonor = new Donor(username, password, accountType, email, phoneNumber, address);
            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (accountType.equalsIgnoreCase("Donee")) {
            Donee newDonee = new Donee(username, password, accountType, email, phoneNumber, address);

            // Save the user object to Firebase
            usersRef.child(String.valueOf(newDonee.getUserId())).setValue(newDonee)
                    .addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            // Save userId to SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("userId", newDonee.getUserId());
                            editor.apply();

                            // Navigate to the next activity after the userId has been saved
                            Intent intent = new Intent(RegisterActivity.this, DoneeRegisterRequestActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean isValidUsername(String username) {
        // Username should only contain letters and numbers, no special characters or spaces
        String usernamePattern = "^[a-zA-Z0-9]+$";
        return username.matches(usernamePattern);
    }

    private boolean isValidPassword(String password) {
        // Password must have at least 8 characters, 1 uppercase, 1 lowercase, 1 digit, and 1 special character
        String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Phone number should only contain digits and be 10-12 digits long
        return phoneNumber.matches("\\d{10,12}");
    }
}
