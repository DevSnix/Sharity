package com.test.sharity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DoneeRegisterActivity extends AppCompatActivity {

    private Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_donee);

        btnSubmit = findViewById(R.id.btnSubmitForm);

        btnSubmit.setOnClickListener(view -> {
            Intent intent = new Intent(DoneeRegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
