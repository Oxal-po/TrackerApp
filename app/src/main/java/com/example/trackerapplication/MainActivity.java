package com.example.trackerapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.trackerapplication.utils.context.ContextUtils;
import com.example.trackerapplication.utils.map.LocationUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ContextUtils(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPerm();
        }
        new LocationUtils(this);
    }

    public void askPerm() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET, Manifest.permission.ACCESS_NOTIFICATION_POLICY, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
    }

    public void count(View view) {
        Intent intent = new Intent(this, FootCountActivity.class);
        startActivity(intent);
    }

    public void testSMS(View view) {
        Intent intent = new Intent(this, TrackingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (requestCode == 1 && grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                Log.v("PALU", "Permission: " + permissions[i] + " was " + grantResults[i]);
            }
        }
    }
}