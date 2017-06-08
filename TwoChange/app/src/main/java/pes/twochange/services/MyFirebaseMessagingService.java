package pes.twochange.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import pes.twochange.R;
import pes.twochange.presentation.activity.MainMenuActivity;
import pes.twochange.presentation.activity.RecyclerChatActivity;

/**
 * Created by Victor on 10/04/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessaginService";

    @Override
    public void onMessageReceived (RemoteMessage remoteMessage) {
        showNotification();
    }

    private void showNotification() {
        Intent i = new Intent(this, RecyclerChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("2Change");
        builder.setContentText("New message recived.");
        Uri soundNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(soundNotification);
        builder.setSmallIcon(R.drawable.ic_2change_notification);
        builder.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap());
        builder.setTicker("2Change");
        builder.setShowWhen(true);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }
}
