package com.example.rm49865.recordsfood.fragment;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rm49865.recordsfood.MainActivity;
import com.example.rm49865.recordsfood.R;
import com.example.rm49865.recordsfood.model.Restaurant;
import com.example.rm49865.recordsfood.util.LocationUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapRestaurantsFragment extends Fragment implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private final String TAG = MapRestaurantsFragment.class.getName();
    public static final String PARAM_RESTAURANTS = "restaurants";
    private List<Restaurant> restaurants;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    public MapRestaurantsFragment() {}

    public static MapRestaurantsFragment newInstance(List<Restaurant> restaurants){
        MapRestaurantsFragment fragment = new MapRestaurantsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PARAM_RESTAURANTS,
                (ArrayList<? extends Parcelable>) restaurants);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            restaurants = getArguments().getParcelableArrayList(PARAM_RESTAURANTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_restaurants, container, false);
        ButterKnife.bind(this, view);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.ftMap);
        mapFragment.getMapAsync(this);
        if(LocationUtil.isGooglePlayservicesAvailable(getActivity())){
            mLocationRequest = LocationUtil.initLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        } else {
            Toast.makeText(getContext(), R.string.message_not_available_gps,
                    Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        loadMarkers();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            LocationUtil.stopLocationuUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            LocationUtil.startLocationUpdates(mGoogleApiClient, mLocationRequest, this,
                    getActivity(), this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationUtil.startLocationUpdates(mGoogleApiClient, mLocationRequest, this,
                getActivity(), this);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LocationUtil.PERMISSION_USE_LOCATION_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED){
                    LocationUtil.startLocationUpdates(mGoogleApiClient, mLocationRequest,
                            this, getActivity(), this);
                } else {
                    Toast.makeText(getContext(), R.string.message_no_permissions_use_gps,
                            Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LocationUtil.ENABLE_LOCATION_SERVICES){
            if(LocationUtil.isLocationServiceEnabled(getActivity())){
                setLocation();
            } else {
                Toast.makeText(getContext(),
                        R.string.message_no_enable_location_service, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.fbCurrentLocation)
    public void findCurrentLocation(){
        if(LocationUtil.isLocationServiceEnabled(getActivity())){
            setLocation();
        } else {
            callLocationSettings();
        }
    }

    private void setLocation(){
        if(mCurrentLocation != null){
            loadMarkers();
            LatLng latLng = LocationUtil.setLocationOnMap(googleMap,
                    mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(),
                    getString(R.string.label_your_location),
                    R.drawable.ic_own_location);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
        } else {
            Toast.makeText(getContext(), R.string.message_not_current_location, Toast.LENGTH_SHORT).show();
        }
    }

    private void callLocationSettings(){
        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                LocationUtil.ENABLE_LOCATION_SERVICES);
    }

    private void loadMarkers(){
        if(restaurants != null && !restaurants.isEmpty()){
            googleMap.clear();
            for(Restaurant restaurant : restaurants){
                LocationUtil.setLocationOnMap(googleMap,
                        Double.parseDouble(restaurant.getLatitude()),
                        Double.parseDouble(restaurant.getLongitude()),
                        restaurant.getName(),
                        R.drawable.ic_location);
            }
            googleMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(LocationUtil.DEFAULT_LOCATION, 3.0f));
        }
    }
}
