package pro.network.nanjilmartdelivery.app;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import pro.network.nanjilmartdelivery.R;
import pro.network.nanjilmartdelivery.SplashActivity;


public class FirebaseMessageReceiver extends FirebaseMessagingService {
  @Override

    public void onMessageReceived(RemoteMessage remoteMessage) {
        //handle when receive notification via data event
        if (remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("message"));
        }

        //handle when receive notification
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }

    }

    private RemoteViews getCustomDesign(String title, String message) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon, R.drawable.nanjilmart);
        return remoteViews;
    }

    public void showNotification(String title, String message) {
        Intent intent = new Intent(this, SplashActivity.class);
        String channel_id = "web_app_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.nanjilmart)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(false)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(getCustomDesign(title, message));
        } else {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.nanjilmart);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Uri sound = Uri.parse(
                "android.resource://" +
                        getApplicationContext().getPackageName() +
                        "/" +
                        R.raw.nanjil);
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(false);
        try {
            // mediaPlayer.setDataSource(String.valueOf(myUri));
            mediaPlayer.setDataSource(FirebaseMessageReceiver.this, sound);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        notificationManager.notify(0, builder.build());
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), FirebaseMessageReceiver.class),
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }

}
