package com.test.sharity;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

// This class initializes the firebase connection before the application runs
public class SharityApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize Firebase
        FirebaseApp.initializeApp(this);

        //If the user is offline, the data changes will be queued and automatically synchronized when the user goes online.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
