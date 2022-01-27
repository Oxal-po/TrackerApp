package com.example.trackerapplication.utils.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.util.Log;
import android.widget.Toast;

import com.example.trackerapplication.utils.sms.SMSUtils;
import com.example.trackerapplication.utils.tracker.TrackerManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;

public class ChangeLocationListener implements LocationListener
{

    private Context context;

    public ChangeLocationListener(Context context) {
        this.context = context;
        Log.d("PALU", "INIT ChangeLocationListener");
    }

    public void onLocationChanged(Location localisation)
    {
    }
}