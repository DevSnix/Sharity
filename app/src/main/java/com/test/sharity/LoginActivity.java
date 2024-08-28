package com.test.sharity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etLicenseNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etLicenseNumber = findViewById(R.id.etCharityLicenseNumberLogin);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> login());
    }

    public void signUpClick(View view) {
        Intent register = new Intent(LoginActivity.this, AccountTypeActivity.class);
        startActivity(register);
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String licenseNumber = etLicenseNumber.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        if (TextUtils.isEmpty(licenseNumber)) {
            // Normal user (Donor/Donee) login logic
            handleUserLogin(email, password);
        } else {
            // Charity login logic
            handleCharityLogin(email, password, licenseNumber);
        }
    }

    private void handleUserLogin(String email, String password) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("userEmail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null && user.getUserPassword().equals(password)) {
                            if (user.getUserType().equals("Donor")) {
                                if (user.isUserStatus()) { // If user is active
                                    storeUserInPreferences(user);
                                    // Start the main activity
                                    Intent intent = new Intent(LoginActivity.this, DonorMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Account is deleted", Toast.LENGTH_SHORT).show();
                                }
                            } else if (user.getUserType().equals("Donee")) {
                                if (user.isUserStatus()) { // If user is active
                                    storeUserInPreferences(user);
                                    // Start the main activity
                                    Intent intent = new Intent(LoginActivity.this, DoneeMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Account is not active yet or deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCharityLogin(String email, String password, String licenseNumber) {
        DatabaseReference charitiesRef = FirebaseDatabase.getInstance().getReference("charities");

        // Navigate directly to the child with the licenseNumber as the key
        charitiesRef.child(licenseNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Charity charity = dataSnapshot.getValue(Charity.class);
                    if (charity != null && charity.getCharityEmail().equals(email) && charity.getCharityPassword().equals(password)) {
                        if (charity.isCharityStatus()) { // If charity is active
                            // Store charity info in shared preferences
                            SharedPreferences sharedPreferences = getSharedPreferences("CharityDetails", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("charityId", charity.getLicenseNumber());
                            editor.putString("charityName", charity.getCharityName());
                            editor.putString("charityEmail", charity.getCharityEmail());
                            editor.apply();

                            // Start the charity main activity
                            Intent intent = new Intent(LoginActivity.this, CharityManagementActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Charity account is not active", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect license number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    // Store user info in shared preferences
    private void storeUserInPreferences(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", user.getUserId());
        editor.putString("userName", user.getUserName());
        editor.putString("userEmail", user.getUserEmail());
        editor.putString("phoneNumber", user.getUserPhoneNumber());
        editor.apply();
    }

}
