package com.example.trackerapplication.utils;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.example.trackerapplication.entity.StepCount;

import java.util.Optional;

public class StepCountManager {

    private static StepCount stepCount;

    public static void update(StepCount sc) {
        stepCount = sc;
    }

    public static Optional<Bundle> getBundle() {
        if (stepCount != null) {
            return Optional.of(stepCount.toBundle());
        }
        return Optional.empty();
    }
}
