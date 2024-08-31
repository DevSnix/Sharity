package com.test.sharity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CharityMessagesActivity extends AppCompatActivity {

    private ImageView backButton;
    private int licenseNumber;
    private String stringLicenseNumber;
    private RecyclerView recyclerViewMessages;
    private MessagesAdapter messagesAdapter;
    private List<Message> messagesList;
    private DatabaseReference charityRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_messages);

        backButton = findViewById(R.id.backButton);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        licenseNumber = getIntent().getIntExtra("licenseNumber", -1);
        stringLicenseNumber = String.valueOf(licenseNumber);

        messagesList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messagesList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messagesAdapter);

        backButton.setOnClickListener(v -> onBackPressed());

        loadMessages();
    }

    private void loadMessages() {
        charityRef = FirebaseDatabase.getInstance()
                .getReference("charities")
                .child(stringLicenseNumber)
                .child("messages");

        charityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messagesList.add(message);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // This will finish the activity and go back
    }
}
