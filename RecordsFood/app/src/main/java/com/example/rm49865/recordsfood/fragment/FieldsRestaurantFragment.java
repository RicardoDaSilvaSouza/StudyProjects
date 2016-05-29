package com.example.rm49865.recordsfood.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rm49865.recordsfood.MainActivity;
import com.example.rm49865.recordsfood.R;
import com.example.rm49865.recordsfood.dao.RestaurantDAO;
import com.example.rm49865.recordsfood.dialog.ConfirmDialog;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class FieldsRestaurantFragment extends Fragment
        implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ConfirmDialog.OnConfirmDialogInteractionListener{

    private final String TAG = FieldsRestaurantFragment.class.getName();
    public static final int ACTION_TYPE_EDIT = 2;
    public static final int ACTION_TYPE_CREATE = 1;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
    public static final int PERMISSION_USE_CAMERA_REQUEST_CODE = 1;
    public static final String PARAM_IMAGE_URI = "imageUri";
    public static final String PARAM_LABEL_ACTION_BUTTON = "labelActionButton";
    public static final String PARAM_SHOW_OPTION_DELETE = "showOptionDelete";
    public static final String PARAM_DETAIL = "detail";
    public static final String PARAM_ID = "id";
    public static final String IMAGE_DIRECTORY_NAME = "RecordsFood";

    private OnFieldsFragmentInteractionListener mListener;
    private String labelActionButton;
    private boolean showOptionDelete;
    private boolean detail;
    private Integer id;
    private Restaurant restaurant;
    private Uri imageUri;
    private String oldPath;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    @Bind(R.id.ivPrev)
    ImageView ivPrev;
    @Bind(R.id.etName)
    EditText etName;
    @Bind(R.id.etTel)
    EditText etTel;
    @Bind(R.id.etType)
    EditText etType;
    @Bind(R.id.etAvg)
    EditText etAvg;
    @Bind(R.id.etObs)
    EditText etObs;
    @Bind(R.id.btAction)
    Button btAction;
    @Bind(R.id.llCommands)
    LinearLayout llCommands;

    public FieldsRestaurantFragment() {}

    public static FieldsRestaurantFragment newInstance(String labelActionButton, Integer id,
                                                       boolean showOptionDelete, boolean detail) {
        FieldsRestaurantFragment fragment = new FieldsRestaurantFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_LABEL_ACTION_BUTTON, labelActionButton);
        args.putBoolean(PARAM_SHOW_OPTION_DELETE, showOptionDelete);
        args.putBoolean(PARAM_DETAIL, detail);
        if (id != null) {
            args.putInt(PARAM_ID, id);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        restaurant = new Restaurant();
        if (getArguments() != null) {
            labelActionButton = getArguments().getString(PARAM_LABEL_ACTION_BUTTON);
            showOptionDelete = getArguments().getBoolean(PARAM_SHOW_OPTION_DELETE);
            detail = getArguments().getBoolean(PARAM_DETAIL);
            if (getArguments().containsKey(PARAM_ID)) {
                id = getArguments().getInt(PARAM_ID);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fields_restaurant, container, false);
        ButterKnife.bind(this, view);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.ftMap);
        mapFragment.getMapAsync(this);

        if(!detail){
            if (LocationUtil.isGooglePlayservicesAvailable(getActivity())) {
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
        }

        setupFields();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PARAM_IMAGE_URI, imageUri);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(PARAM_IMAGE_URI)) {
            imageUri = savedInstanceState.getParcelable(PARAM_IMAGE_URI);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            switch (resultCode) {
                case MainActivity.RESULT_OK:
                    if (oldPath != null && !oldPath.isEmpty()) {
                        new File(oldPath).delete();
                    }
                    previewCapturedImage();
                    break;
                case MainActivity.RESULT_CANCELED:
                    if (oldPath != null && !oldPath.isEmpty()) {
                        imageUri = Uri.fromFile(new File(oldPath));
                    }
                    Toast.makeText(getContext(), R.string.message_cancel_capture, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getContext(), R.string.message_fail_capture, Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == LocationUtil.ENABLE_LOCATION_SERVICES){
            if(LocationUtil.isLocationServiceEnabled(getActivity())){
                setLocation();
            } else {
                Toast.makeText(getContext(),
                        R.string.message_no_enable_location_service, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFieldsFragmentInteractionListener) context;
        } catch (ClassCastException e){
            throw new RuntimeException("The activity "
                    + context.toString() + " must implements the interface "
                    + OnFieldsFragmentInteractionListener.class.getName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
            LocationUtil.startLocationUpdates(mGoogleApiClient, mLocationRequest,
                    this, getActivity(), this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(showOptionDelete){
            inflater.inflate(R.menu.delete_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itDelete:
                ConfirmDialog confirmDialog = ConfirmDialog.newInstance(getString(R.string.message_confirm_exclude), this);
                confirmDialog.show(getChildFragmentManager(), TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_USE_CAMERA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    callIntentCamera();
                } else {
                    Toast.makeText(getContext(), R.string.message_no_permissions_take_pictures,
                            Toast.LENGTH_SHORT).show();
                }
                break;
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

    @OnClick(R.id.btClean)
    public void clean() {
        imageUri = null;
        ivPrev.setImageResource(R.drawable.ic_add_photo);
        etName.setText("");
        etTel.setText("");
        etType.setText("");
        etAvg.setText("");
        etObs.setText("");
    }

    @OnClick(R.id.fbAddLocation)
    public void findCurrentLocation() {
        if(LocationUtil.isLocationServiceEnabled(getActivity())){
            setLocation();
        } else {
            callLocationSettings();
        }
    }

    private void callLocationSettings(){
        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                LocationUtil.ENABLE_LOCATION_SERVICES);
    }

    private void setLocation(){
        if (mCurrentLocation != null) {
            restaurant.setLatitude(String.valueOf(mCurrentLocation.getLatitude()));
            restaurant.setLongitude(String.valueOf(mCurrentLocation.getLongitude()));
            mGoogleMap.clear();
            LatLng latLng= LocationUtil.setLocationOnMap(mGoogleMap,
                    mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(),
                    etName.getText().toString(),
                    R.drawable.ic_location);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
        } else {
            Toast.makeText(getContext(), R.string.message_not_current_location, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btAction)
    public void doAction() {
        if (checkFields()) {
            fillRestaurant();
            if (restaurant.getId() == null) {
                mListener.doAction(restaurant, ACTION_TYPE_CREATE);
            } else {
                mListener.doAction(restaurant, ACTION_TYPE_EDIT);
            }
        }
    }

    @OnClick(R.id.ivPrev)
    public void takePicture() {
        if (deviceHasSupportCamera()) {
            if (checkPermissions()) {
                callIntentCamera();
            }
        } else {
            Toast.makeText(getContext(), R.string.message_not_camera, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFields(){
        if(detail){
            llCommands.setVisibility(View.GONE);
            ivPrev.setEnabled(false);
            etName.setEnabled(false);
            etTel.setEnabled(false);
            etType.setEnabled(false);
            etAvg.setEnabled(false);
            etObs.setEnabled(false);
        } else {
            btAction.setText(labelActionButton);
        }

        if (id != null) {
            RestaurantDAO dao = new RestaurantDAO(getContext());
            restaurant = dao.findById(id);

            if(restaurant.getImagePath() != null && !restaurant.getImagePath().isEmpty()){
                imageUri = Uri.fromFile(new File(restaurant.getImagePath()));
                previewCapturedImage();
            }
            etName.setText(restaurant.getName());
            etTel.setText(restaurant.getTel());
            etType.setText(restaurant.getType());
            etAvg.setText(restaurant.getAverageCost().toString());
            etObs.setText(restaurant.getObservation());

            mCurrentLocation = new Location(restaurant.getName());
            if(restaurant.getLatitude() != null){
                mCurrentLocation.setLatitude(Double.valueOf(restaurant.getLatitude()));
            }
            if(restaurant.getLongitude() != null){
                mCurrentLocation.setLongitude(Double.valueOf(restaurant.getLongitude()));
            }
        }
    }

    private void callIntentCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getOutputMediaImageUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private boolean checkFields() {
        boolean result = true;
        if (imageUri == null || imageUri.getPath().isEmpty()) {
            result = false;
            Toast.makeText(getContext(), R.string.message_obligatory_pic,
                    Toast.LENGTH_SHORT).show();
        } else if (etName.getText() == null || etName.getText().toString().isEmpty()) {
            result = false;
            Toast.makeText(getContext(), R.string.message_obligatory_name,
                    Toast.LENGTH_SHORT).show();
        } else if (etType.getText() == null || etType.getText().toString().isEmpty()) {
            result = false;
            Toast.makeText(getContext(), R.string.message_obligatory_type,
                    Toast.LENGTH_SHORT).show();
        } else if ((restaurant.getLatitude() == null || restaurant.getLongitude() == null)
                || (restaurant.getLatitude().isEmpty() || restaurant.getLongitude().isEmpty())
                || (Double.parseDouble(restaurant.getLatitude()) == 0.0 || Double.parseDouble(restaurant.getLongitude()) == 0.0)){
            result = false;
            Toast.makeText(getContext(), R.string.message_no_location_informed, Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void fillRestaurant() {
        restaurant.setImagePath(imageUri.getPath());
        restaurant.setName(etName.getText().toString());
        restaurant.setTel((etTel.getText() == null || etTel.getText().toString().isEmpty()) ? "" : etTel.getText().toString());
        restaurant.setType(etType.getText().toString());
        restaurant.setAverageCost((etAvg.getText() == null || etAvg.getText().toString().isEmpty()) ? 0.0 : Double.parseDouble(etAvg.getText().toString()));
        restaurant.setObservation((etObs.getText() == null || etObs.getText().toString().isEmpty()) ? "" : etObs.getText().toString());
    }

    private boolean deviceHasSupportCamera() {
        return getActivity().getApplicationContext()
                .getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private boolean checkPermissions() {
        boolean result = false;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            result = true;
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, PERMISSION_USE_CAMERA_REQUEST_CODE);
        }
        return result;
    }

    private void previewCapturedImage() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            ivPrev.setImageBitmap(BitmapFactory.decodeFile(imageUri.getPath(), options));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getContext(), R.string.message_error_load_preview,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getOutputMediaImageUri() {
        if (imageUri != null) {
            oldPath = imageUri.getPath();
        }
        imageUri = Uri.fromFile(getOutputMediaFile());
    }

    private File getOutputMediaFile() {
        File mediaFile = null;
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, getString(R.string.message_error_create_directory)
                        + IMAGE_DIRECTORY_NAME);
                Toast.makeText(getContext(), R.string.message_error_create_directory,
                        Toast.LENGTH_SHORT).show();
                return mediaFile;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        mediaFile = new File(mediaStorageDir.getPath()
                + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if(mCurrentLocation != null) {
            mGoogleMap.clear();
            LatLng latLng = LocationUtil.setLocationOnMap(mGoogleMap,
                    mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(),
                    etName.getText().toString(),
                    R.drawable.ic_location);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationUtil.startLocationUpdates(mGoogleApiClient, mLocationRequest,
                this, getActivity(), this);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void confirm() {
        mListener.delete(restaurant);
    }

    @Override
    public void cancel() {}

    public interface OnFieldsFragmentInteractionListener{
        void doAction(Restaurant restaurant, int actionType);
        void delete(Restaurant restaurant);
    }
}
