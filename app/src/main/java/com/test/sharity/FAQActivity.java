package com.test.sharity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class FAQActivity extends AppCompatActivity {

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq_activity);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // This will finish the activity and go back
    }
}
