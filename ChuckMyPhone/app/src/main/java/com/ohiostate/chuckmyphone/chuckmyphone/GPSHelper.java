package com.ohiostate.chuckmyphone.chuckmyphone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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
    private double latitude;
    private double longitude;

    public GPSHelper(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        latitude = 0.0;
        longitude = 0.0;
        setLocation(getLastLocation(context, LocationManager.GPS_PROVIDER));
    }

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            setLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    private Location getLastLocation(Context context, String chosenGPS) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return locationManager.getLastKnownLocation(chosenGPS);
        } else {
            Location location = new Location("No provider");
            location.setLatitude(0.0);
            location.setLongitude(0.0);
            return location;
        }
    }

    public void stopGPS(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

    public void requestLocation(Context context, String chosenGPS) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(chosenGPS, 0, 0, locationListener);
        }
    }

    private void setLocation(Location location){
        if(location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public boolean isPreciseGPSPresent(){return locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER);}

    public boolean isPreciseGPSEnabled(){return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}

    public boolean isOtherGPSEnabled(){return locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);}
}