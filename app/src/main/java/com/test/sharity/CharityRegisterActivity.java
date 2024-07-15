package com.test.sharity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CharityRegisterActivity extends AppCompatActivity {

    private EditText charityLicenseNumber;
    private EditText charityName;
    private EditText charityPassword;
    private EditText charityConfirmPassword;
    private EditText charityEmail;
    private EditText charityBranchAddress;
    private EditText charityContactNumber;
    private String charityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_charity);

        charityLicenseNumber = findViewById(R.id.etLicenseNumber);
        charityName = findViewById(R.id.etCharityName);
        charityPassword = findViewById(R.id.etPassword);
        charityConfirmPassword = findViewById(R.id.etConfirmPassword);
        charityEmail = findViewById(R.id.etCharityEmail);
        charityBranchAddress = findViewById(R.id.etBranchAddress);
        charityContactNumber = findViewById(R.id.etCharityContactNumber);


        Spinner charity_types = findViewById(R.id.charityTypes);
        charity_types.setSelection(0);

        charity_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selected item
                String selectedOption = parent.getItemAtPosition(position).toString();
                charityType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    }

    private void registerUser() {
        String licenseNumber = charityLicenseNumber.getText().toString().trim();
        String name = charityName.getText().toString().trim();
        String email = charityEmail.getText().toString().trim();
        String password = charityPassword.getText().toString().trim();
        String confirmPassword = charityConfirmPassword.getText().toString().trim();
        String address = charityBranchAddress.getText().toString().trim();
        String phoneNumber = charityContactNumber.getText().toString().trim();

        if (TextUtils.isEmpty(licenseNumber)) {
            charityLicenseNumber.setError("License Number is required");
            return;
        }

        if (TextUtils.isEmpty(name)) {
            charityName.setError("Charity Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            charityEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            charityPassword.setError("Password is required");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            charityConfirmPassword.setError("Confirm Password is required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            charityConfirmPassword.setError("Passwords do not match");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            charityBranchAddress.setError("Address is required");
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            charityContactNumber.setError("Phone Number is required");
            return;
        }
    }
}
