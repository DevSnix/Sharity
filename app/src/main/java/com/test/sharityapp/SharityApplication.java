package com.test.sharityapp;

import android.app.Application;
import com.google.firebase.FirebaseApp;

// This class initializes the firebase connection before the application runs
public class SharityApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
