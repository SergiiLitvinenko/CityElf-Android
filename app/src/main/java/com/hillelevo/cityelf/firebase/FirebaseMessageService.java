package com.hillelevo.cityelf.firebase;

import static com.hillelevo.cityelf.Constants.TAG;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hillelevo.cityelf.Constants.Prefs;
import com.hillelevo.cityelf.activities.MainActivity;
import com.hillelevo.cityelf.data.UserLocalStore;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class FirebaseMessageService extends FirebaseMessagingService {


  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {

    Log.d(TAG, "FBMessageService Message from: " + remoteMessage.getFrom());

    // Check if message contains a data payload.
    if (remoteMessage.getData().size() > 0) {
      Log.d(TAG, "FBMessageService Message data payload: " + remoteMessage.getData());
    }

    //Build notification
    String notification;

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      notification = remoteMessage.getNotification().getBody();
      Log.d(TAG, "FBMessageService message Notification Body: " + notification);
    } else {
      notification = "error";
    }
    Uri not = getSoundFromPref();
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this)
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .setContentTitle("Новое событие:")
            .setContentText(notification)
            .setVibrate(new long[]{10, 700})
            .setSound(not);

    int NOTIFICATION_ID = 1;

    // Notification click
    Intent targetIntent = new Intent(this, MainActivity.class);
    PendingIntent contentIntent = PendingIntent
        .getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(contentIntent);
    NotificationManager nManager = (NotificationManager) getSystemService(
        Context.NOTIFICATION_SERVICE);
    nManager.notify(NOTIFICATION_ID, builder.build());
  }

  public Uri getSoundFromPref() {
    Uri notification = null;
    if (UserLocalStore.containsInSharedPrefs(getApplicationContext(), Prefs.RINGTONE)) {
      notification = Uri
          .parse(UserLocalStore.loadStringFromSharedPrefs(getApplicationContext(), Prefs.RINGTONE));
    } else {
      notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }
    return notification;
  }


}
