package com.example.trackerapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.trackerapplication.entity.StepCount;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView stepTv;
    private TextView metterTv, metterPerSecondTv;
    private StepCount stepCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (stepCount == null) {
            stepCount = new StepCount();
        }

        setContentView(R.layout.activity_main);
        stepTv = findViewById(R.id.counter);
        metterTv = findViewById(R.id.counter_metter);
        metterPerSecondTv = findViewById(R.id.counter_metter_per_second);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        stepCount.addStep(sensorEvent.values);
        stepTv.setText(stepCount.getStep() + " pas");
        metterTv.setText(stepCount.getMetter() + " m");
        metterPerSecondTv.setText(stepCount.getMetterPerSecond() + " m/s");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_GAME);
    }

    public void clickStartButton(View view) {
        stepCount.start();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void clickRestartButton(View view) {
        stepTv.setText("0 pas");
        metterTv.setText("0 m");
        metterPerSecondTv.setText("0 m/s");
        stepCount = new StepCount();
        stepCount.start();
    }

    public void clickStopButton(View view) {
        stepCount.stop();
        sensorManager.unregisterListener(this);
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
}