package com.dushan.dev.mapper.Services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.dushan.dev.mapper.Data.LocationData;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Data.SocialData;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.Handlers.NotificationHandler;
import com.google.firebase.auth.FirebaseAuth;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class LocationService extends Service {
    private static final String TAG = "LocationService";

    private LocationManager mLocationManager = null;
    private static int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 0;

    private SocialData socialData;
    private LocationData locationData;
    private NotificationHandler notificationHandler;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        private LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "locationUpdated: " + location);
            mLastLocation.set(location);
            locationData.changeLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            withMarkersNearby(mLastLocation);
            if (inBackground() && withUsersNearby(mLastLocation) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                notificationHandler
                        .createSimpleNotification(getApplicationContext(), "There is someone nearby. Tap to open application.");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "locationService: started");
        initializeService();
        initializeLocationManager();
        SharedPreferences sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);

        try {
            LOCATION_INTERVAL = sharedPref.getInt("backgroundInterval", 10000);
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        return;
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
    }

    private void withMarkersNearby(Location lastLocation) {
        for (Marker marker: socialData.getSocialMarkers()) {
            float[] distance = new float[3];
            android.location.Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    marker.getLatitude(), marker.getLongitude(), distance);
            if (distance[0] <= 30)
                socialData.visitedMarkerEvent(marker);
        }
    }

    private boolean withUsersNearby(Location lastLocation) {
        for (User user: socialData.getSocialFriends()) {
            float[] distance = new float[3];
            android.location.Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    user.getLatitude(), user.getLongitude(), distance);
            if (distance[0] <= 30)
                return true;
        }
        return false;
    }

    private boolean inBackground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return !(appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void initializeService() {
        Log.e(TAG, "initializeServiceComponents");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        locationData = LocationData.getInstance(mAuth.getCurrentUser().getUid(), getApplicationContext());
        socialData = SocialData.getInstance(mAuth.getCurrentUser().getUid(), getApplicationContext());
        notificationHandler = NotificationHandler.getInstance(getApplicationContext());
    }
}
