package com.example.trackerapplication.utils.map;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MapManager {


    private static Map<Integer, GoogleMap> dictMap;
    private static Map<Integer, Polyline> dictLine;
    private static Map<Integer, List<LatLng>> dictList;
    private static Map<Integer, Marker> dictMarker;

    public MapManager(int id, GoogleMap map) {
        this(id, map, true);
    }

    public MapManager(int id, GoogleMap map, boolean replace) {
        if (dictMap == null) {
            dictMap = new HashMap<>();
            dictLine = new HashMap<>();
            dictList = new HashMap<>();
            dictMarker = new HashMap<>();
        }
        if (!exist(id) || replace) {
            dictMap.put(id, map);
            dictLine.put(id, map.addPolyline(new PolylineOptions()
                    .add()
                    .width(5)
                    .color(Color.RED)));
            dictList.put(id, dictLine.get(id).getPoints());
        }
    }

    public MapManager(){
        if (dictMap == null) {
            dictMap = new HashMap<>();
            dictLine = new HashMap<>();
            dictList = new HashMap<>();
            dictMarker = new HashMap<>();
        }
    }

    public static void update(int id, GoogleMap map) {
        if (!exist(id)) return;
        dictMap.put(id, map);
        dictLine.put(id, map.addPolyline(new PolylineOptions()
                .add()
                .width(5)
                .color(Color.RED)));
        dictLine.get(id).setPoints(dictList.get(id));
        Log.d("PALU", dictList.get(id).toString());
        if (dictList.get(id).size() > 0) {
            dictMarker.put(id, dictMap.get(id).addMarker((new MarkerOptions()
                            .position(dictList.get(id).get(0))
                            .title("départ"))));
            dictMap.get(id).animateCamera(CameraUpdateFactory.newLatLngZoom(dictList.get(id).get(dictList.get(id).size() - 1), 15.0f));
        }
    }

    public static boolean exist(int id) {
        return dictMap != null && dictMap.containsKey(id);
    }


    public static void addPoint(int id, LatLng point) {
        addPoint(id, point, true);
    }

    public static void addPoint(int id, LatLng point, boolean move) {
        if (!exist(id)) return;
        if (dictList.get(id).size() == 0 && move) {
            dictMap.get(id).addMarker(new MarkerOptions()
                    .position(point)
                    .title("départ"));
            dictMap.get(id).animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15.0f));
        }
        Log.d("PALU", dictList.get(id).toString());
        dictList.get(id).add(point);
        Log.d("PALU", dictList.get(id).toString());
        if (move) {
            dictLine.get(id).setPoints(dictList.get(id));
            if (dictList.get(id).size() > 2) {
                dictMap.get(id).animateCamera(CameraUpdateFactory.newLatLng(point));
            }
        }
    }

    public static void addLocalisation(int id, Location point) {
        addPoint(id, new LatLng(point.getLatitude(), point.getLongitude()));
    }

    public static void addLocalisation(int id, Location point, boolean move) {
        addPoint(id, new LatLng(point.getLatitude(), point.getLongitude()), move);
    }

    public static void reset(int id) {
        if (!exist(id)) return;
        dictList.put(id, new ArrayList<>());
        dictMap.get(id).clear();
    }
}
