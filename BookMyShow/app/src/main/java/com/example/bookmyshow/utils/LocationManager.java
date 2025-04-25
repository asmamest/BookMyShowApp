// LocationManager.java
package com.example.bookmyshow.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.bookmyshow.models.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationManager {
    private static final String TAG = "LocationManager";
    private static final String PREFS_NAME = "LocationPrefs";
    private static final String KEY_LOCATION_NAME = "locationName";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_RADIUS = "radius";

    private static final String DEFAULT_LOCATION_NAME = "Lieu";
    private static final double DEFAULT_LATITUDE = 9.1450; // Centre de l'Afrique
    private static final double DEFAULT_LONGITUDE = 18.4283; // Centre de l'Afrique
    private static final int DEFAULT_RADIUS = 0;

    private Context context;
    private SharedPreferences prefs;

    public LocationManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getLocationName() {
        return prefs.getString(KEY_LOCATION_NAME, DEFAULT_LOCATION_NAME);
    }

    public double getLatitude() {
        return Double.longBitsToDouble(prefs.getLong(KEY_LATITUDE, Double.doubleToLongBits(DEFAULT_LATITUDE)));
    }

    public double getLongitude() {
        return Double.longBitsToDouble(prefs.getLong(KEY_LONGITUDE, Double.doubleToLongBits(DEFAULT_LONGITUDE)));
    }

    public int getRadius() {
        return prefs.getInt(KEY_RADIUS, DEFAULT_RADIUS);
    }

    public void saveLocation(String locationName, double latitude, double longitude) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LOCATION_NAME, locationName);
        editor.putLong(KEY_LATITUDE, Double.doubleToRawLongBits(latitude));
        editor.putLong(KEY_LONGITUDE, Double.doubleToRawLongBits(longitude));
        editor.apply();
    }

    public void saveRadius(int radius) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_RADIUS, radius);
        editor.apply();
    }

    public boolean hasCustomLocation() {
        return !prefs.getString(KEY_LOCATION_NAME, DEFAULT_LOCATION_NAME).equals(DEFAULT_LOCATION_NAME);
    }

    public GeoPoint getGeoPoint() {
        return new GeoPoint(getLatitude(), getLongitude());
    }

    public String getLocationWithRadius() {
        String location = getLocationName();
        int radius = getRadius();
        return location + " " + radius + " km";
    }

    public void resetLocation() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LOCATION_NAME, DEFAULT_LOCATION_NAME);
        editor.putLong(KEY_LATITUDE, Double.doubleToRawLongBits(DEFAULT_LATITUDE));
        editor.putLong(KEY_LONGITUDE, Double.doubleToRawLongBits(DEFAULT_LONGITUDE));
        editor.putInt(KEY_RADIUS, DEFAULT_RADIUS);
        editor.apply();
    }

    public GeoPoint geocodeLocation(String locationName) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new GeoPoint(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e(TAG, "Erreur de géocodage", e);
        }
        return null;
    }
}