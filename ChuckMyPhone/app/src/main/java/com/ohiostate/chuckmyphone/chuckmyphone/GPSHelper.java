package com.ohiostate.chuckmyphone.chuckmyphone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Joao Pedro on 3/24/2016.
 */

public class GPSHelper {
    private LocationManager locationManager;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;
    private boolean nullLocation;

    public GPSHelper(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        requestPermissionForGPS(context);
        nullLocation = true;
    }

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            setLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    private Location getLastLocation(Context context, String gpsProvider) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("coordslast", "called");
            return locationManager.getLastKnownLocation(gpsProvider);
        } else {
            Log.d("coordslast", "other");
            Location location = new Location("No provider");
            location.setLatitude(0.0);
            location.setLongitude(0.0);
            return location;
        }
    }

    public void stopGPS(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public void requestPermissionForGPS(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void requestLocation(Context context, String gpsProvider) {
        if(isGPSEnabled(gpsProvider)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(gpsProvider, 0, 0, locationListener);
            }
        }
    }

    private void setLocation(Location location){
        if(location != null) {
            CurrentUser.getInstance().updateLatitude(location.getLatitude());
            CurrentUser.getInstance().updateLongitude(location.getLongitude());
            nullLocation = false;
        }
    }

    public boolean isGPSEnabled(String gpsProvider){return locationManager.isProviderEnabled(gpsProvider);}

    public boolean cannotUseCoordinates(String gpsProvider){
        return nullLocation && !isGPSEnabled(gpsProvider);
    }
}