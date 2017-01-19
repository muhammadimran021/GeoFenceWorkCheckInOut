package com.example.kamranali.geofencedummyproject;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by kamranali on 04/01/2017.
 */

public class Constants  {
    public static final String PREFERNCES_KEY_FOR_EXIT = "isExit";

    private Constants() {
    }
    public static class SharedPrefs {
        public static String Geofences = "SHARED_PREFS_GEOFENCES";
    }
    public static final String PACKAGE_NAME = "com.example.kamranali.geofencedummyproject";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    //public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km

    public static final float GEOFENCE_RADIUS_IN_METERS = 50; // 1 mile, 1.6 km

    public static final HashMap<String, LatLng> PanacloudLatLong = new HashMap<String, LatLng>();
    static {

        // OFFICE
        PanacloudLatLong.put("PANACLOUD ", new LatLng(24.813486, 67.048381));

        // OFFICE
        PanacloudLatLong.put("HOME ", new LatLng(24.923880,67001465));

        //HOTEL
        PanacloudLatLong.put("Hotel ", new LatLng(24.920905,66.992984));
//        // OFFICE
//        PanacloudLatLong.put("MeatOne ", new LatLng(24.813602758751703, 67.04840116202831));
//
//        // OFFICE
//        PanacloudLatLong.put("HOME ", new LatLng(24.882545824417782,67.05190446227789));
//
//        //HOTEL
//        PanacloudLatLong.put("Agha ", new LatLng(24.814108846402565,67.050461769104));
//
//        PanacloudLatLong.put("Saylani ", new LatLng(24.88304828447051,67.06828840076923));
//        PanacloudLatLong.put("SSUET-F ", new LatLng(24.935934148512622,67.03469607979059));
//        PanacloudLatLong.put("SSUET-B ", new LatLng(24.91561927070885,67.09298986941576));
    }

}
