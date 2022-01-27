package com.example.trackerapplication.utils.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.telephony.SmsManager;

import com.example.trackerapplication.utils.map.MapManager;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class SMSUtils {

    private final static String SENT = "SMS_SENT";
    private final static String DELIVERED = "SMS_DELIVERED";
    private final static int MAX_SMS_MESSAGE_LENGTH = 160;

    public static void sendSMS(String phoneNumber, String message, Context context) {

        PendingIntent piSent = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(context, 0,new Intent(DELIVERED), 0);
        SmsManager smsManager = SmsManager.getDefault();

        int length = message.length();
        if(length > MAX_SMS_MESSAGE_LENGTH) {
            ArrayList<String> messagelist = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phoneNumber, null, messagelist, null, null);
        }
        else
            smsManager.sendTextMessage(phoneNumber, null, message, piSent, piDelivered);
    }

    public static void sendPos(String phoneNumber, Location location, Context context) {
        sendSMS(phoneNumber, "TRACKER;" + location.getLatitude() + ";" + location.getLongitude(), context);
    }

    public static void sendStartTrack(String phoneNumber, Context context) {
        sendSMS(phoneNumber, "TRACKER;START", context);
    }

    public static void sendStopTrack(String phoneNumber, Context context) {
        sendSMS(phoneNumber, "TRACKER;STOP", context);
    }
}
