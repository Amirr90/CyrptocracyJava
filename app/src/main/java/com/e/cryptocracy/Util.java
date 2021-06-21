package com.e.cryptocracy;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static final String SHARED_PREF = "shared_prefs";
    public static final String SHARED_PREFS_KEY_ID = "ids";

    private static final String DEMO_CHANNEL_ID = "demo_notification_id";
    NotificationManagerCompat managerCompat;
    MyFirebaseMessagingService myFirebaseMessagingService;

    public Util(MyFirebaseMessagingService myFirebaseMessagingService) {
        this.myFirebaseMessagingService = myFirebaseMessagingService;
        managerCompat = NotificationManagerCompat.from(myFirebaseMessagingService);
    }

    public void showNotification(String title, String body, String click_action, String notificationId) {

        Intent intent = new Intent(click_action);
        intent.putExtra("notification_id", notificationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(myFirebaseMessagingService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(myFirebaseMessagingService, DEMO_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .build();

        managerCompat.notify((int) System.currentTimeMillis(), notification);
    }


    public static String parseDate(String inDate, String outPattern, String inputPattern) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat(outPattern);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(inDate);
            str = outputFormat.format(date);
            return str;
        } catch (ParseException e) {
            e.printStackTrace();
            return inDate;
        }


    }
}
