package com.example.trackerapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackerapplication.utils.map.MapManager;
import com.example.trackerapplication.utils.sms.SMSUtils;
import com.example.trackerapplication.utils.tracker.TrackerManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText numberEt;
    private TextView currentTarget;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        numberEt = findViewById(R.id.tracking_number_edit_text);
        mapView = findViewById(R.id.tracking_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        currentTarget = findViewById(R.id.tracking_current_track);
        if (TrackerManager.haveTrack()) {
            currentTarget.setText(getString(R.string.text_tracking_current_track) + " " + TrackerManager.getCurrentTrack());
        } else {
            currentTarget.setText(getString(R.string.text_tracking_current_track) + " personne");
        }
    }

    public void track(View view) {
        String number = numberEt.getText().toString().trim();
        Pattern pattern = Pattern.compile("^\\+\\d{2}\\d{9}$");
        Matcher matcher = pattern.matcher(number);
        if (matcher.matches()) {
            MapManager.reset(1);
            String oldNum = TrackerManager.getCurrentTrack();

            if (oldNum == null || (oldNum != null && !oldNum.equals(number))) {
                SMSUtils.sendStartTrack(number, this);

                if(oldNum != null) {
                    SMSUtils.sendStopTrack(number, this);
                }
                TrackerManager.setCurrentTrack(number);
                currentTarget.setText(getString(R.string.text_tracking_current_track) + " " + number);
            }
        } else {
            Toast.makeText(this, R.string.toast_tracking_error_number, Toast.LENGTH_LONG).show();
        }
    }

    public void stop(View view) {
        MapManager.reset(1);
        if (TrackerManager.haveTrack()) {
            SMSUtils.sendStopTrack(TrackerManager.getCurrentTrack(), this);
            TrackerManager.deleteTrack();
            currentTarget.setText(getString(R.string.text_tracking_current_track) + " personne");
        }
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        GoogleMap map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        new MapManager(1, map, false);
        MapManager.update(1, googleMap);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}