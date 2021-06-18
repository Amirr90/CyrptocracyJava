package com.e.cryptocracy;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class NotificationClass extends Application {

    public static final String DEMO_CHANNEL_ID = "demo_notification_id";

    @Override
    public void onCreate() {
        super.onCreate();
        crateNotificationChannel();
    }

    private void crateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel demoNotificationChannel = new NotificationChannel(
                    DEMO_CHANNEL_ID,
                    "price_alert_notification",
                    NotificationManager.IMPORTANCE_HIGH );
            demoNotificationChannel.setDescription( "DemoNotification" );

            NotificationManager notificationManager = getSystemService( NotificationManager.class );
            notificationManager.createNotificationChannel( demoNotificationChannel ) ;
        }
    }




}
