package com.akshay.android.geofence_app;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    public static final String TAG = "MainActivity";
    private GoogleApiClient googleApiClient = null;

    private GeofencingClient mGeofencingClient;

    private DatabaseReference databaseInformation;

    private static ListView listViewInformation;
    private List<Information> databaseInformationList;

    private List<Geofence> mGeofenceList = new ArrayList<>();
    private List<Information> triggeredGeofenceList = new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Gson gson;
    public InformationAdapter informationAdapter;
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseInformation = FirebaseDatabase.getInstance().getReference("information");
        listViewInformation = findViewById(R.id.list_view_information);
        databaseInformationList = new ArrayList<>();

        // Creates a GeofencingClient
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        pref = getApplicationContext().getSharedPreferences("MyGeofencePref", 0); // 0 - for private mode
        editor = pref.edit();
        gson = new Gson();

        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (!intent.getParcelableArrayListExtra("informationList").isEmpty()) {
                    triggeredGeofenceList = intent.getParcelableArrayListExtra("informationList");
                    informationAdapter = new InformationAdapter(MainActivity.this, triggeredGeofenceList);
                    listViewInformation.setAdapter(informationAdapter);
                } else {
                    listViewInformation.setVisibility(View.GONE);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("geofence_app");

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddInformation.class);
                startActivity(intent);
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "Connected to Google Api");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Failed to connect to GoogleApiClient");
                    }
                })
                .build();

        databaseInformation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                databaseInformationList.clear();

                for (DataSnapshot informationSnapshot : dataSnapshot.getChildren()) {
                    Information information = informationSnapshot.getValue(Information.class);
                    databaseInformationList.add(information);
                    Double latitude  = Double.parseDouble(information.getmGeofence().getLatitude());
                    Double longitude = Double.parseDouble(information.getmGeofence().getLongitude());
                    int radius = information.getmGeofence().getRadius();
                    Log.d(TAG, "latitude: " + latitude + " longitude: " + longitude + "radius" + radius);
                    mGeofenceList.add(new Geofence.Builder()
                            .setRequestId(information.getmId())
                            .setCircularRegion(latitude, longitude, radius)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setNotificationResponsiveness(1000)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build());
                    gson = new Gson();
                    String json = gson.toJson(information);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(information.getmId(), json);

                    editor.apply();
                }

                /*InformationAdapter informationAdapter = new InformationAdapter(MainActivity.this, databaseInformationList);
                listViewInformation.setAdapter(informationAdapter);*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
        requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);
*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mGeofenceList.isEmpty()) {
            addGeofences();
        }

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(receiver, new IntentFilter("RECEIVER_FILTER"));

        /*Log.d(TAG, "startLocation called");
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(TAG, "Location update lat/long" + location.getLatitude() + " " + location.getLongitude());
                        }
                    });
        } catch (SecurityException e) {
            Log.d(TAG, "Security Exception - " + e.getMessage());
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google play Services Not available");
            GoogleApiAvailability.getInstance().getErrorDialog(this, response, 1).show();
        } else {
            Log.d(TAG, "Google play services is available");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
            }
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

        return mGeofencePendingIntent;
    }

    private void addGeofences() {
        try {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Geofences added
                            // ...
                            Log.d(TAG, "Geofences added");
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to add geofences
                            // ...
                            Log.d(TAG, "Failed to add Geofences");
                        }
                    });
        } catch (SecurityException e) {
            Log.d(TAG, "Security exception");
        }
    }

    public static void removeListViewWhenExitGeofence() {
        listViewInformation.setVisibility(View.GONE);
    }
}
