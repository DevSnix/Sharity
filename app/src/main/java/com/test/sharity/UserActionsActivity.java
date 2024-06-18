package com.test.sharity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import android.widget.TextView;

public class UserActionsActivity extends AppCompatActivity {

    private ListView lvUserActions;
    //private ArrayList<String> userActions;

    private TextView tvActionHistoryTitle;
    private TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_actions);


        lvUserActions = findViewById(R.id.lvUserActions);
        tvActionHistoryTitle = findViewById(R.id.tvActionHistoryTitle);
        tvNoData = findViewById(R.id.tvNoData);

       /* userActions = getIntent().getStringArrayListExtra("userActions");
        String actionType = getIntent().getStringExtra("actionType");
        ArrayList<String> actionData = getIntent().getStringArrayListExtra("actionData");
*/
        String actionType = getIntent().getStringExtra("actionType");
        ArrayList<String> actionData = getIntent().getStringArrayListExtra("actionData");

        // Set up adapter for the ListView
      /*  ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, userActions);
        lvUserActions.setAdapter(adapter);
*/

        if (actionData != null && !actionData.isEmpty()) {
            // Set up adapter for the ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, actionData);
            lvUserActions.setAdapter(adapter);
        } else {
            // Show "No data available" message
            tvNoData.setVisibility(View.VISIBLE);
        }

        if ("donation".equals(actionType)) {
            tvActionHistoryTitle.setText("Donation History");
        } else {
            tvActionHistoryTitle.setText("User Actions");
        }
    }
}