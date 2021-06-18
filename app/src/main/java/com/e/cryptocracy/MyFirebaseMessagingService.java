package com.e.cryptocracy;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Util utils;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived( remoteMessage );
        if (remoteMessage.getNotification() != null) {
            String body = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();
            String click_action = remoteMessage.getNotification().getClickAction();
            String notificationId = remoteMessage.getData().get( "notification_id" );
            utils = new Util( this );
            if (utils != null) {
                utils.showNotification( title, body, click_action, notificationId );
            }

        }
    }
}
