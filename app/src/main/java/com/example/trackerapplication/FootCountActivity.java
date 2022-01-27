package com.example.trackerapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.trackerapplication.entity.StepCount;
import com.example.trackerapplication.utils.StepCountManager;
import com.example.trackerapplication.utils.map.AsyncLocationUpdate;
import com.example.trackerapplication.utils.map.LocationUtils;
import com.example.trackerapplication.utils.map.MapManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.text.DecimalFormat;
import java.util.concurrent.Semaphore;

public class FootCountActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView stepTv;
    private GoogleMap map;
    private MapView mapView;
    private TextView metterTv, metterPerSecondTv;
    private StepCount stepCount;
    private AsyncLocationUpdate locationAsync;
    private static final DecimalFormat df = new DecimalFormat("0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("PALU", StepCountManager.getBundle().toString());
        if (stepCount == null) {
            stepCount = new StepCount();
        }

        setContentView(R.layout.activity_foot_count);
        stepTv = findViewById(R.id.counter);
        metterTv = findViewById(R.id.counter_metter);
        metterPerSecondTv = findViewById(R.id.counter_metter_per_second);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        stepTv.setText(stepCount.getStep() + " pas");
        metterTv.setText(df.format(stepCount.getMetter()) + " m");
        metterPerSecondTv.setText(df.format(stepCount.getMetterPerSecond()) + " m/s");

        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        synchronized (this) {
            locationAsync = new AsyncLocationUpdate(this);
            locationAsync.setRun(false);
            locationAsync.start();
        }
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        stepCount.addStep(sensorEvent.values);
        stepTv.setText(stepCount.getStep() + " pas");
        metterTv.setText(df.format(stepCount.getMetter()) + " m");
        metterPerSecondTv.setText(df.format(stepCount.getMetterPerSecond()) + " m/s");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void clickStartButton(View view) {
        stepCount.start();
        locationAsync.setRun(true);
        LocationUtils.getLocation().ifPresent(location -> MapManager.addLocalisation(0, location));
        MapManager.update(0, map);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void clickRestartButton(View view) {
        clickStopButton(view);
        stepTv.setText("0 pas");
        metterTv.setText("0 m");
        metterPerSecondTv.setText("0 m/s");
        stepCount = new StepCount(false);
        StepCountManager.update(stepCount);
        MapManager.reset(0);
        MapManager.update(0, map);
    }

    public void clickStopButton(View view) {
        stepCount.stop();
        locationAsync.setRun(false);
        sensorManager.unregisterListener(this);
    }

    public void tracker(View view) throws InterruptedException {
        MapManager.update(0, map);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        stepCount = new StepCount(savedInstanceState, sensorManager, this, sensor);

        stepTv.setText(stepCount.getStep() + " pas");
        metterTv.setText(stepCount.getMetter() + " m");
        metterPerSecondTv.setText(stepCount.getMetterPerSecond() + " m/s");
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stepCount.save(outState);
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        new MapManager(0, map, false);
        MapManager.update(0, googleMap);
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
        StepCountManager.update(stepCount);
    }

    @Override
    public void onDestroy() {
        Log.d("PALU", "DESTROY");
        super.onDestroy();
        locationAsync.setRun(false);
        mapView.onDestroy();
        StepCountManager.update(stepCount);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        StepCountManager.update(stepCount);
    }
}