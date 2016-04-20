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
 */

class GPSHelper {
    private LocationManager locationManager;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;

    private final long MINIMUM_TIME = 100;
    private final float MINIMUM_DISTANCE = 10;

    public GPSHelper(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        requestPermissionForGPS(context);
    }

    private void requestPermissionForGPS(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void stopGPS(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates((LocationListener) context);
        }
    }

    public void requestLocation(Context context, String gpsProvider) {
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
        if (location != null) {
            CurrentUser.getInstance().loadUserLocation(location.getLatitude(), location.getLongitude());
            Log.d("coords", location.getLatitude() + " "+ location.getLongitude());
        }
    }

    public boolean isGPSEnabled(String gpsProvider) {
        return locationManager.isProviderEnabled(gpsProvider);
    }

    public void setToLastLocation(Context context, String gpsProvider) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setLocation(locationManager.getLastKnownLocation(gpsProvider));
        }
    }
}