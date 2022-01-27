package com.example.trackerapplication.utils.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.trackerapplication.utils.map.LocationUtils;
import com.example.trackerapplication.utils.sms.SMSUtils;
import com.example.trackerapplication.utils.tracker.TrackerManager;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //todo faire un event pour le tracker manager
        Log.d("PALU","ALO");
        Toast.makeText(context, "PALU MENFOU", Toast.LENGTH_SHORT).show();
        TrackerManager trackerManager = new TrackerManager();
        if (intent.getStringExtra("NUMBER") != null) {
            trackerManager.addTracker(intent.getStringExtra("NUMBER"));
            LocationUtils.getLocation().ifPresent(location -> SMSUtils.sendPos(intent.getStringExtra("NUMBER"), location, context));
        }
        int notificationId = intent.getIntExtra("notificationId", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
