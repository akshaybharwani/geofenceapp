package com.akshay.android.geofence_app;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddGeofence extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng mLatLng;
    public static final String TAG = "AddGeofenceActivity";
    private String placeName;
    private int radius;

    GoogleMap mGoogleMap;
    Circle circleMap;
    CircleOptions circleOptions;
    public SupportMapFragment mapFragment;
    public LatLng defaultLatLng = new LatLng(21.007658,75.562604);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geofence);

        mLatLng = defaultLatLng;
        placeName = "Jalgaon";
        radius = 100;
        /*circleOptions = new CircleOptions()
                .center(mLatLng)
                .radius(radius)
                .strokeColor(Color.BLACK)
                .fillColor(0x30ff0000);*/

        EditText geofenceRadiusEditText = findViewById(R.id.geofence_radius_edit_text);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                mLatLng = place.getLatLng();
                placeName = place.getName().toString();

                mGoogleMap.clear();

                mGoogleMap.addMarker(new MarkerOptions().position(mLatLng)).setVisible(true);
                circleOptions = new CircleOptions()
                        .center(mLatLng)
                        .radius(radius)
                        .strokeColor(Color.BLACK)
                        .fillColor(0x30ff0000);
                circleMap = mGoogleMap.addCircle(circleOptions);
                circleMap.setStrokeWidth(2);

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        FloatingActionButton addGeofenceButton = findViewById(R.id.add_geofence_button);
        addGeofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToAddInformationActivityWithData();
            }
        });

        geofenceRadiusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(circleMap!=null){
                    circleMap.remove();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    Log.d(TAG, "Inside the onTextChanged");
                    Log.d(TAG, "editable: " + editable);
                    radius = Integer.parseInt(editable.toString());
                    circleOptions = new CircleOptions()
                            .center(mLatLng)
                            .radius(radius)
                            .strokeColor(Color.BLACK)
                            .fillColor(0x30ff0000);
                    circleMap = mGoogleMap.addCircle(circleOptions);
                    circleMap.setStrokeWidth(2);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.addMarker(new MarkerOptions().position(defaultLatLng)).setVisible(true);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 10));
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        /*circleMap = mGoogleMap.addCircle(circleOptions);*/
    }

    private void returnToAddInformationActivityWithData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lat", String.valueOf(mLatLng.latitude));
        editor.putString("long", String.valueOf(mLatLng.longitude));
        editor.putString("placeName", placeName);
        editor.putInt("radius", radius);
        editor.apply();
        this.finish();
    }
}
