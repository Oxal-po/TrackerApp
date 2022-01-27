package com.example.trackerapplication.utils.map;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.example.trackerapplication.utils.context.ContextUtils;
import com.example.trackerapplication.utils.sms.SMSUtils;
import com.example.trackerapplication.utils.tracker.TrackerManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.Optional;

public class AsyncLocationUpdate extends Thread {

    private static Location location;
    private static final double R = 6371e3;
    private boolean run = true;
    private long sleep = 500;
    private double metter = 0.1;
    private Activity activity;


    public AsyncLocationUpdate(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        new LocationUtils(activity);
        LocationUtils.lookLocation();
        LocationUtils.getLocation().ifPresent(l -> location = l);
        while (location != null) {
            if(run) {
                LocationUtils.getLocation().ifPresent(l -> {
                    if (getMetterDiff(location, l) > metter) {

                        ContextUtils.getInstance().ifPresent(context -> {
                            new TrackerManager();
                            TrackerManager.apply(num -> SMSUtils.sendPos(num, l, context));
                            activity.runOnUiThread(() -> MapManager.addLocalisation(0, l));
                        });

                        location = l;
                    }
                });
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                //e.printStackTrace();
            }
        }
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public long getSleep() {
        return sleep;
    }

    public double getMetterDiff(Location l1, Location l2) {
        double p1 = l1.getLatitude() * Math.PI/180;
        double p2 = l2.getLatitude() * Math.PI/180;

        double deltaP = (l2.getLatitude() - l1.getLatitude()) * Math.PI/180;
        double deltaLambda = (l2.getLongitude() - l1.getLongitude()) * Math.PI/180;

        double a = Math.sin(deltaP/2) * Math.sin(deltaP/2) +
                Math.cos(p1) * Math.cos(p2) * Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);

        double c = Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Log.d("PALU", "dist : " + R * c);
        return R * c;
    }
}
