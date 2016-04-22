package com.ohiostate.chuckmyphone.chuckmyphone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Joao Pedro on 3/24/2016.
 *
 * This class is intended to help making requests for user location
 * and giving permission to do so.
 */

class GPSHelper {
    // random number to represent permission for fine location access
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;

    // minimum time in miliseconds between listening to location changes
    private final long MINIMUM_TIME = 100;
    // minimum distance in meters to consider changes in location
    private final float MINIMUM_DISTANCE = 10;

    private LocationManager locationManager;

    public GPSHelper(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        requestPermissionForGPS(context);
    }

    public void stopGPS(Context context) {
        // method to stop gps from listening to location changes
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates((LocationListener) context);
        }
    }

    public void requestLocation(Context context, String gpsProvider) {
        // method to make gps start listening for location changes
        if (isGPSEnabled(gpsProvider)) {
            CurrentUser.getInstance().updateGPSEnabled(true);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d("coords", "requesting");
                locationManager.requestLocationUpdates(gpsProvider, MINIMUM_TIME, MINIMUM_DISTANCE, (LocationListener) context);
            }
        } else {
            CurrentUser.getInstance().updateGPSEnabled(false);
        }
    }

    public void setLocation(Location location) {
        // method to be called to update the location of the user
        if (location != null) {
            CurrentUser.getInstance().loadUserLocation(location.getLatitude(), location.getLongitude());
            Log.d("coords", location.getLatitude() + " "+ location.getLongitude());
        }
    }

    public boolean isGPSEnabled(String gpsProvider) {
        // method to check if gps is enabled
        return locationManager.isProviderEnabled(gpsProvider);
    }

    public void setToLastLocation(Context context, String gpsProvider) {
        // method to be called to update user location with the last location provided by the gps
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setLocation(locationManager.getLastKnownLocation(gpsProvider));
        }
    }

    private void requestPermissionForGPS(Context context) {
        // method to request permission to use gps
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}