package com.example.trackerapplication.utils.tracker;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class TrackerManager {

    private static List<String> trackers;
    private static List<Consumer<String>> listeners;
    private static String currentTrack;

    public TrackerManager() {
        if (trackers == null) {
            trackers = new ArrayList<>();
            listeners = new ArrayList<>();
        }
    }

    public static void addTracker(String number) {
        if (!trackers.contains(number)) {
            trackers.add(number);
        }

        Log.d("PALU", trackers.toString());
    }

    public static void deleteTracker(String number) {
        for (int i = 0; i < trackers.size(); i++) {
            if (trackers.get(i).equals(number)) {
                trackers.remove(i);
                return;
            }
        }
    }

    public static void resetTrackers() {
        trackers = new ArrayList<>();
    }

    public static void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    public static void applies() {
        for (String number : trackers) {
            for (Consumer<String> fun : listeners) {
                fun.accept(number);
            }
        }
    }

    public static void apply(Consumer<String> fun) {
        for (String number : trackers) {
            fun.accept(number);
        }
    }

    public static void setCurrentTrack(String track) {
        currentTrack = track;
    }

    public static void deleteTrack() {
        currentTrack = null;
    }

    public static boolean haveTrack() {
        return currentTrack != null;
    }

    public static String getCurrentTrack(){
        return currentTrack;
    }
}
