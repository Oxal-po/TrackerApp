package com.example.trackerapplication.utils.sms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.trackerapplication.MainActivity;
import com.example.trackerapplication.R;
import com.example.trackerapplication.utils.map.MapManager;
import com.example.trackerapplication.utils.notification.NotificationUtils;
import com.example.trackerapplication.utils.tracker.TrackerManager;
import com.google.android.gms.maps.model.LatLng;

public class SMSReceiver extends BroadcastReceiver {
    private final String DEBUG_TAG = getClass().getSimpleName().toString();
    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private Context mContext;
    private Intent mIntent;
    private NotificationManager notificationManager;

    // Retrieve SMS
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        String action = intent.getAction();

        if(action.equals(ACTION_SMS_RECEIVED)){

            String address, str = "";

            SmsMessage[] msgs = getMessagesFromIntent(mIntent);
            if (msgs != null) {
                for (int i = 0; i < msgs.length; i++) {
                    address = msgs[i].getOriginatingAddress();
                    //contactId = ContactsUtils.getContactId(mContext, address, "address");
                    str += msgs[i].getMessageBody();
                    if (startTracker(str)) {
                        NotificationUtils.startTrackinkNotif(context, address);
                    } else if (isTrackerPos(str)) {
                        String finalStr = str;
                        if (new MapManager().exist(1)) {
                            new MapManager().addPoint(1, getLatLng(finalStr));
                        }
                    } else if (stopTracker(str)){
                        TrackerManager.deleteTracker(address);
                    }
                }
            }

            // ---send a broadcast intent to update the SMS received in the
            // activity---
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("sms", str);
            context.sendBroadcast(broadcastIntent);
        }

    }

    public static SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }


    public boolean isTrackerPos(String message) {
        String[] split = message.split(";");
        if (split.length != 3 || !split[0].equals("TRACKER")) return false;
        try {
            double lat = Double.valueOf(split[1]);
            double lon = Double.valueOf(split[2]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public LatLng getLatLng(String message) {
        String[] split = message.split(";");
        double lat = Double.valueOf(split[1]);
        double lon = Double.valueOf(split[2]);
        return new LatLng(lat, lon);
    }

    public boolean startTracker(String message) {
        String[] split = message.split(";");
        if (split.length != 2 || !split[0].equals("TRACKER")) return false;
        return split[1].equals("START");
    }

    public boolean stopTracker(String message) {
        String[] split = message.split(";");
        if (split.length != 2 || !split[0].equals("TRACKER")) return false;
        return split[1].equals("STOP");
    }
}
