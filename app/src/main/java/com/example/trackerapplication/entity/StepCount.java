package com.example.trackerapplication.entity;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.example.trackerapplication.utils.StepCountManager;

import java.util.Date;

public class StepCount {

    private boolean first, run, up = false;
    private float d0, d = 0f;
    public static final float AVG_LONG_STEP = 0.6076f;
    private int step = 0;
    private long startTime, stopTime;

    public StepCount() {
        this(true);
    }

    public StepCount(boolean bundleCheck) {
        if (bundleCheck) {
            StepCountManager.getBundle().ifPresent(bundle -> {
                d0 = bundle.getFloat("D0");
                step = bundle.getInt("STEP");
                startTime = bundle.getLong("TIME");
                stopTime = bundle.getLong("STOP");
            });
            StepCountManager.update(this);
        }
    }

    public StepCount(Bundle bundle, SensorManager sensorManager, SensorEventListener sensorEventListener, Sensor sensor) {
        if(bundle.getBoolean("IS_RUN")) {
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME);
            run = true;
            first = false;
        } else {
            first = true;
        }
        d0 = bundle.getFloat("D0");
        step = bundle.getInt("STEP");
        startTime = bundle.getLong("TIME");
    }

    public void start() {
        if (stopTime != 0) {
            startTime = new Date().getTime() - Math.abs(stopTime - startTime);
        } else if(startTime == 0) {
            startTime = new Date().getTime();
        }
        stopTime = 0;
        setRun(true);
    }

    public void stop() {
        if (stopTime == 0 && stopTime != 0) {
            stopTime = new Date().getTime();
        }
        setRun(false);
    }

    public void addStep(float[] values) {
        addStep((float) Math.sqrt(Math.pow(values[0], 2) + Math.pow(values[1], 2) + Math.pow(values[2], 2)));
    }

    public void addStep(float sum) {
        if (first) {
            first = false;
            up = true;
            d0 = AVG_LONG_STEP * sum;
        } else {
            d = AVG_LONG_STEP * sum + (1 - AVG_LONG_STEP) * d0;
            if (up && d < d0) {
                up = false;
                step++;
            } else if (!up && d > d0) {
                up = true;
                d0 = d;
            }
        }
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public float getD0() {
        return d0;
    }

    public void setD0(float d0) {
        this.d0 = d0;
    }

    public float getD() {
        return d;
    }

    public void setD(float d) {
        this.d = d;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public double getMetter() {
        return step * AVG_LONG_STEP;
    }

    public double getMetterPerSecond() {
        long time;
        if (stopTime == 0) {
            time = new Date().getTime() - startTime;
        } else {
            time = stopTime - startTime;
        }
        return getMetter() / getSeconde(time);
    }

    public int getSeconde(long time) {
        return (int) (time/1000);
    }

    public void save(Bundle bundle) {
        StepCountManager.update(this);
        bundle.putBoolean("IS_RUN", run);
        bundle.putInt("STEP", step);
        bundle.putLong("TIME", startTime);
        bundle.putLong("STOP", stopTime);
        bundle.putFloat("D0", d0);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("IS_RUN", run);
        bundle.putInt("STEP", step);
        bundle.putLong("TIME", startTime);
        if (stopTime == 0) {
            bundle.putLong("STOP", new Date().getTime());
        } else {
            bundle.putLong("STOP", stopTime);
        }
        bundle.putFloat("D0", d0);
        Log.d("PALU BUNDLE", bundle.toString());
        return bundle;
    }
}
