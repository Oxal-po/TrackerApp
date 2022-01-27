package com.example.trackerapplication.utils.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.trackerapplication.FootCountActivity;
import com.example.trackerapplication.MainActivity;
import com.example.trackerapplication.R;

public class NotificationUtils {

    public static void startTrackinkNotif(Context context, String number) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
        Intent ii = new Intent(context.getApplicationContext(), FootCountActivity.class);
        Intent notif = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        notif.putExtra("NUMBER", number);
        notif.putExtra("ID", 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);
        PendingIntent notifIntent = PendingIntent.getBroadcast(context, 0, notif, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Ask start tracking");
        mBuilder.setContentText(number + " : veux vous tracker");
        mBuilder.addAction(R.drawable.ic_accept, "Accepter", notifIntent);

        String channelId = "Palu_menfou_channel";
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        mBuilder.setChannelId(channelId);
        notificationManager.notify(0, mBuilder.build());
    }
}
