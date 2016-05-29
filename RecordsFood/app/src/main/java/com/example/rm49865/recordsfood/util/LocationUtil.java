package com.example.rm49865.recordsfood.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Ricardo on 13/02/2016.
 */
public class LocationUtil {

    public static final String TAG = LocationUtil.class.getName();
    public static final long INTERVAL = 1000 * 10;
    public static final long FASTEST_INTERVAL = 1000 * 5;
    public static final int PERMISSION_USE_LOCATION_REQUEST_CODE = 100;
    public static final int ENABLE_LOCATION_SERVICES = 3;
    public static final LatLng DEFAULT_LOCATION = new LatLng(-23.549645,-46.6344553);

    public static MarkerOptions createMarker(LatLng latLng, String title, int icon) {
        return new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(icon));
    }

    public static LatLng setLocationOnMap(GoogleMap mGoogleMap, Double lat, Double lon,
                                  String title, int icon){
        LatLng latLng = new LatLng(lat, lon);
        mGoogleMap.addMarker(LocationUtil.createMarker(latLng, title, icon));
        return  latLng;
    }

    public static boolean isGooglePlayservicesAvailable(Activity activity) {
        boolean result = true;
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (!(ConnectionResult.SUCCESS == status)) {
            result = false;
            GooglePlayServicesUtil.getErrorDialog(status, activity, 0).show();
        }
        return result;
    }

    public static LocationRequest initLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public static void stopLocationuUpdates(GoogleApiClient mGoogleApiClient, LocationListener locationListener){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
    }

    public static void startLocationUpdates(GoogleApiClient mGoogleApiClient,
                                            LocationRequest mLocationRequest,
                                            LocationListener locationListener,
                                            Activity activity, Fragment fragment) {
        if (checkPermisssionsToAccessLocation(activity, fragment)){
            try{
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, locationListener);
            } catch (SecurityException e){
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public static boolean checkPermisssionsToAccessLocation(Activity activity, Fragment fragment){
        boolean result = false;
        if (ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_WIFI_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            result = true;
        } else {
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_USE_LOCATION_REQUEST_CODE);
        }
        return result;
    }

    public static boolean isLocationServiceEnabled(Activity activity){
        boolean result;
        LocationManager manager = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);
        result = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return result;
    }
}
