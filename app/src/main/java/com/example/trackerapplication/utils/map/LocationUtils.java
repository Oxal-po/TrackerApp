package com.example.trackerapplication.utils.map;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.trackerapplication.utils.context.ContextUtils;

import java.util.Optional;

public class LocationUtils {
    private static LocationManager locationManager;
    private static String fournisseur;
    private static ChangeLocationListener myLocationListener;
    private static Activity activity;

    public LocationUtils(Activity a) {
        activity = a;
        ContextUtils.getInstance().ifPresent(context -> {
            if (locationManager == null) {
                locationManager = context.getSystemService(LocationManager.class);
                Criteria criteres = new Criteria();
                criteres.setAccuracy(Criteria.ACCURACY_FINE);
                criteres.setAltitudeRequired(true);
                criteres.setBearingRequired(true);
                criteres.setCostAllowed(true);
                criteres.setPowerRequirement(Criteria.POWER_HIGH);

                fournisseur = locationManager.getBestProvider(criteres, true);

                lookLocation();
            }
        });
    }

    public static void lookLocation() {
        ContextUtils.getInstance().ifPresent(context -> {
            if (fournisseur != null) {
                // dernière position connue
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Location localisation = locationManager.getLastKnownLocation(fournisseur);

                if (myLocationListener == null) {
                    myLocationListener = new ChangeLocationListener(context);
                }

                if (localisation != null) {
                    myLocationListener.onLocationChanged(localisation);
                }

                activity.runOnUiThread(() -> locationManager.requestLocationUpdates(fournisseur, 10, 0.01f, myLocationListener));

            }
        });
    }

    public static Optional<LocationManager> getManager(){
        return Optional.of(locationManager);
    }

    public static Optional<Location> getLocation() {
        return ContextUtils.getInstance().map(context -> {
            if (locationManager != null & fournisseur != null && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            return locationManager.getLastKnownLocation(fournisseur);
        });
    }

    public static void stop() {
        if (locationManager != null) {
            locationManager.removeUpdates(myLocationListener);
            myLocationListener = null;
        }
    }
}
