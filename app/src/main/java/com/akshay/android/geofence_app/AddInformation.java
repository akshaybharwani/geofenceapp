package com.akshay.android.geofence_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddInformation extends AppCompatActivity {

    EditText titleTextView;
    EditText descriptionTextView;
    Button addInformationButton;

    DatabaseReference databaseInformation;

    TextView geofenceLocationNameTextView;
    TextView geofenceLatitudeTextView;
    TextView geofenceLongitudeTextView;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private String placeName;
    private String latitude;
    private String longitude;
    private int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_information);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        geofenceLocationNameTextView = findViewById(R.id.geofence_location_name_textview);
        geofenceLatitudeTextView     = findViewById(R.id.geofence_latitude_textview);
        geofenceLongitudeTextView    = findViewById(R.id.geofence_longitude_textview);

        geofenceLocationNameTextView.setVisibility(View.GONE);
        geofenceLatitudeTextView.setVisibility(View.GONE);
        geofenceLongitudeTextView.setVisibility(View.GONE);

        Button addGeofenceButton = findViewById(R.id.add_geofence_button);
        // Starts Add Geo-fence Activity
        addGeofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddInformation.this, AddGeofence.class);
                startActivity(intent);
            }
        });

        databaseInformation = FirebaseDatabase.getInstance().getReference("information");

        titleTextView = findViewById(R.id.title_text_view);

        descriptionTextView = findViewById(R.id.description_text_view);

        addInformationButton = findViewById(R.id.add_information_button);

        addInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addInformation();
            }
        });

    }

    private void addInformation() {
        String title = titleTextView.getText().toString();
        String description = descriptionTextView.getText().toString();

        if (!TextUtils.isEmpty(title)) {
            String id = databaseInformation.push().getKey();

            Geofence geofence = new Geofence(id, placeName, latitude, longitude, radius);
            Information information = new Information(id, title, description, geofence);

            databaseInformation.child(id).setValue(information);
            editor.remove("placeName");
            editor.remove("lat");
            editor.remove("long");
            editor.remove("radius");

            editor.apply();

            editor.clear();
            editor.apply();

            Toast.makeText(this, "Information added", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(AddInformation.this, MainActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Enter a title", Toast.LENGTH_SHORT).show();
        }

        if (!TextUtils.isEmpty(description)) {

        } else {
            Toast.makeText(this, "Enter a description", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        placeName = pref.getString("placeName", null);
        latitude  = pref.getString("lat", null);
        longitude = pref.getString("long", null);
        radius = pref.getInt("radius", 100);

        // Checking for any of them will let us know if there is info in the SharedPreferences
        if (pref.contains("placeName")) {
            geofenceLocationNameTextView.setVisibility(View.VISIBLE);
            geofenceLatitudeTextView.setVisibility(View.VISIBLE);
            geofenceLongitudeTextView.setVisibility(View.VISIBLE);

            geofenceLocationNameTextView.setText(placeName);
            geofenceLatitudeTextView.setText(latitude);
            geofenceLongitudeTextView.setText(longitude);

        }
    }


}
