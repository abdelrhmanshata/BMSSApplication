package com.example.bmssapplication.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.bmssapplication.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "my_channel_id";
    private static final CharSequence CHANNEL_NAME = "My Channel";

    public static void showNotification(Context context, int notificationId, String title, String message) {
        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for devices running Android Oreo (API 26) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.saving) // Set the small icon
                .setContentTitle(title) // Set the title
                .setContentText(message) // Set the message
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // Set the priority

        // Show the notification
        notificationManager.notify(notificationId, builder.build());
    }
}
