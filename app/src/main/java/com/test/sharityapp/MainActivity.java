package com.test.sharityapp;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private ImageView ivProfileIcon;
    private ImageButton ivCharity1, ivCharity2, ivCharity3, ivCharity4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ivProfileIcon = findViewById(R.id.ivProfileIcon);
        ivCharity1 = findViewById(R.id.ivCharity1);
        ivCharity2 = findViewById(R.id.ivCharity2);
        ivCharity3 = findViewById(R.id.ivCharity3);
        ivCharity4 = findViewById(R.id.ivCharity4);

        ivProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });



        /*
        ivCharity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnimalCharityActivity.class);
                startActivity(intent);
            }
        });

        ivCharity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WildLifeCharityActivity.class);
                startActivity(intent);
            }
        });

        ivCharity3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ElderlyCharityActivity.class);
                startActivity(intent);
            }
        });

        ivCharity4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChildrenCharityActivity.class);
                startActivity(intent);
            }
        }); */



        ivCharity1.setOnClickListener(createCharityClickListener("Animal Charity"));
        ivCharity2.setOnClickListener(createCharityClickListener("Wildlife Charity"));
        ivCharity3.setOnClickListener(createCharityClickListener("Elderly Charity"));
        ivCharity4.setOnClickListener(createCharityClickListener("Children Charity"));
    }


private View.OnClickListener createCharityClickListener(final String charityType) {
    return new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, CharityActivity.class);
            intent.putExtra("CHARITY_TYPE", charityType);
            startActivity(intent);
        }
    };
}
}