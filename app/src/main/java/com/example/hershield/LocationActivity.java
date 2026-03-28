package com.example.hershield;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IMapController;
import org.osmdroid.views.overlay.Marker;
import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity {

    private MapView map;
    private double currentLat = 0;
    private double currentLon = 0;
    private RequestQueue queue;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE)
        );

        setContentView(R.layout.activity_location);

        queue = Volley.newRequestQueue(this);

        SharedPreferences prefs = getSharedPreferences("HerShield", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        getCurrentLocation();
        setupNavigationIcons();
        if (getIntent().getBooleanExtra("TRIGGER_SOS", false)) {
            onSOSTriggered();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location location = null;
        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (location != null) {
            currentLat = location.getLatitude();
            currentLon = location.getLongitude();

            GeoPoint userLocation = new GeoPoint(currentLat, currentLon);

            IMapController controller = map.getController();
            controller.setZoom(15.0);
            controller.setCenter(userLocation);

            map.getOverlays().clear();

            Marker marker = new Marker(map);
            marker.setPosition(userLocation);
            marker.setTitle("You are here");
            map.getOverlays().add(marker);

            map.invalidate();
        } else {
            Toast.makeText(this, "Unable to get location. Turn on GPS.", Toast.LENGTH_LONG).show();
        }
    }

    // ✅ Call this when Bluetooth signal is received from Arduino
    public void onSOSTriggered() {
        if (currentLat == 0 && currentLon == 0) {
            Toast.makeText(this, "Location not available yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 2);
            return;
        }

        // Fetch contacts from server and send SMS
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiClient.BASE_URL + "/contacts/" + userId,
                null,
                response -> {
                    try {
                        JSONArray contacts = response.getJSONArray("contacts");
                        if (contacts.length() == 0) {
                            Toast.makeText(this, "No contacts saved!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Build location link (OpenStreetMap)
                        String locationLink = "https://www.openstreetmap.org/?mlat="
                                + currentLat + "&mlon=" + currentLon
                                + "#map=15/" + currentLat + "/" + currentLon;

                        String message = "🚨 SOS ALERT! I might be in danger!\n"
                                + "My live location:\n" + locationLink;

                        // Send SMS to each contact
                        SmsManager smsManager;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            smsManager = getSystemService(SmsManager.class);
                        } else {
                            smsManager = SmsManager.getDefault();
                        }
                        int sentCount = 0;
                        for (int i = 0; i < contacts.length(); i++) {
                            String phone = contacts.getJSONObject(i).getString("phone");
                            if (!phone.startsWith("+")) {
                                phone = "+91" + phone;
                            }
                            try {
                                ArrayList<String> parts = smsManager.divideMessage(message);
                                smsManager.sendMultipartTextMessage(phone, null, parts, null, null);
                                sentCount++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(this,
                                "SOS sent to " + sentCount + " contact(s)!",
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch contacts", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else if (requestCode == 2 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // SMS permission granted, trigger SOS again
            onSOSTriggered();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNavigationIcons() {
        findViewById(R.id.settings_icon).setOnClickListener(v ->
                startActivity(new Intent(this, HomePage.class)));
        findViewById(R.id.btn_test_sos).setOnClickListener(v -> onSOSTriggered());
    }
}