package com.test.sharity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Show a notification reminding the user to donate again
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            Notification notification = new NotificationCompat.Builder(context, "donation_reminder")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Donation Reminder")
                    .setContentText("It's time to donate again!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
            notificationManager.notify(1, notification);
        }
    }
}

