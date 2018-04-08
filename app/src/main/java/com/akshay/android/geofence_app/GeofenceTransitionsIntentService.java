package com.akshay.android.geofence_app;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ListView;
import android.view.View;

import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.Geofence;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int counter;
    private ArrayList<Information> informationList = new ArrayList<>();

    private static final String TAG = "GeofenceService";

    protected void onHandleIntent(Intent intent) {
        counter = 1;
        Log.d(TAG, "IN THE INTENT");
        prefs = getApplicationContext().getSharedPreferences("MyGeofencePref", 0); // 0 - for private mode
        editor = prefs.edit();
        gson = new Gson();

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            // TODO: Handle error
        } else {

            int transition = event.getGeofenceTransition();

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    transition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                    transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                List<String> geofenceIds = new ArrayList<>();

                // 3. Accumulate a list of event geofences
                for (Geofence geofence : event.getTriggeringGeofences()) {
                    geofenceIds.add(geofence.getRequestId());
                }
                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                        transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                    // 4. Pass the geofence list to the notification method
                    Log.d(TAG, "There's a transition");
                    onEnteredGeofences(geofenceIds);
                } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    // TODO: Remove the exited geofences
                }
            }
        }
    }

    private void onEnteredGeofences(List<String> geofenceIds) {
        // 1. Outer loop over all geofenceIds

        for (String geofenceId : geofenceIds) {
            String informationTitle = "";
            String informationDescription = "";

            // 2, Loop over all geofence keys in prefs and retrieve NamedGeofence from SharedPreferences
            Map<String, ?> keys = prefs.getAll();
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                String jsonString = prefs.getString(entry.getKey(), null);
                Information information = gson.fromJson(jsonString, Information.class);
                if (information.getmId().equals(geofenceId)) {
                    informationTitle = information.getmTitle();
                    informationDescription = information.getmDescription();
                    Log.d(TAG, "tile: " + informationTitle + " description: " + informationDescription);
                    informationList.add(information);
                    break;
                }
            }

            // For API 27 and up (Android 8 and up)
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                CharSequence name = getString(R.string.channel_name);
                String description = getString(R.string.channel_description);
                int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.createNotificationChannel(channel);
            }*/

            // Create an explicit intent for an Activity in your app
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            // 1. Create a NotificationManager
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(informationTitle)
                    .setContentText(informationDescription)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            int notificationId = 123;
            // TODO: Fix the behaviour of single notifications
            Log.d(TAG, "Sent a notification");
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, mBuilder.build());
        }
        Intent intent = new Intent("RECEIVER_FILTER");
        intent.putExtra("informationList", informationList);
        Log.d(TAG, "Intent is firing");
        LocalBroadcastManager.getInstance(GeofenceTransitionsIntentService.this).sendBroadcast(intent);
    }

}
