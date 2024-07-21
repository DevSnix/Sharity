package com.test.sharity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class CharityRegisterActivity extends AppCompatActivity {

    private EditText charityLicenseNumber;
    private EditText charityName;
    private EditText charityPassword;
    private EditText charityConfirmPassword;
    private EditText charityEmail;
    private EditText charityBranchAddress;
    private EditText charityContactNumber;
    private EditText charityDescription;
    private String charityType;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageViewCharityLogo;
    private Uri imageUri;
    private String imageUrl;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button buttonRegister;

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
        charityDescription = findViewById(R.id.editTextDescription);
        imageViewCharityLogo = findViewById(R.id.imageViewCharityLogo);
        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonRegister = findViewById(R.id.btnRegister);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("charity_images");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("charities");

        buttonSelectImage.setOnClickListener(v -> openFileChooser());

        Spinner charity_types = findViewById(R.id.charityTypes);
        charity_types.setSelection(0);

        charity_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                charityType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        buttonRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                uploadImage();
                registerCharity();
                finish();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewCharityLogo.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(imageUri)
                    .override(480, 270)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
                            fileReference.putBytes(data)
                                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        imageUrl = uri.toString();
                                    }))
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(CharityRegisterActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                    });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle if needed
                        }
                    });
        }
    }

    private boolean validateInputs() {
        String licenseNumberText = charityLicenseNumber.getText().toString().trim();
        String name = charityName.getText().toString().trim();
        String email = charityEmail.getText().toString().trim();
        String password = charityPassword.getText().toString().trim();
        String confirmPassword = charityConfirmPassword.getText().toString().trim();
        String address = charityBranchAddress.getText().toString().trim();
        String phoneNumber = charityContactNumber.getText().toString().trim();
        String description = charityDescription.getText().toString().trim();

        if (TextUtils.isEmpty(licenseNumberText)) {
            charityLicenseNumber.setError("License Number is required");
            return false;
        }

        if (TextUtils.isEmpty(name)) {
            charityName.setError("Charity Name is required");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            charityEmail.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            charityPassword.setError("Password is required");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            charityConfirmPassword.setError("Confirm Password is required");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            charityConfirmPassword.setError("Passwords do not match");
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            charityBranchAddress.setError("Address is required");
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            charityContactNumber.setError("Phone Number is required");
            return false;
        }

        if (TextUtils.isEmpty(description)) {
            charityDescription.setError("Description is required");
            return false;
        }

        return true;
    }

    private void registerCharity() {
        String licenseNumberText = charityLicenseNumber.getText().toString().trim();
        int licenseNumber = Integer.parseInt(licenseNumberText);
        String name = charityName.getText().toString().trim();
        String email = charityEmail.getText().toString().trim();
        String password = charityPassword.getText().toString().trim();
        String address = charityBranchAddress.getText().toString().trim();
        String phoneNumber = charityContactNumber.getText().toString().trim();
        String description = charityDescription.getText().toString().trim();

        Charity charity = new Charity(licenseNumber, name, email, address, password, phoneNumber, charityType, imageUrl, description);
        Toast.makeText(CharityRegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
        Intent loginPage = new Intent(CharityRegisterActivity.this, LoginActivity.class);
        startActivity(loginPage);
    }
}
