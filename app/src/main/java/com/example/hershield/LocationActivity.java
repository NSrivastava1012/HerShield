package com.example.hershield;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IMapController;
import org.osmdroid.views.overlay.Marker;

public class LocationActivity extends AppCompatActivity {

    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OSMDroid config (VERY IMPORTANT)
        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE)
        );

        setContentView(R.layout.activity_location);

        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        // Get location on start
        getCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        // ✅ Permission check
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
            // Try GPS first
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Fallback to network
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (location != null) {

            double lat = location.getLatitude();
            double lon = location.getLongitude();

            GeoPoint userLocation = new GeoPoint(lat, lon);

            // Map controller
            IMapController controller = map.getController();
            controller.setZoom(15.0);
            controller.setCenter(userLocation);

            // Clear old markers
            map.getOverlays().clear();

            // Add marker
            Marker marker = new Marker(map);
            marker.setPosition(userLocation);
            marker.setTitle("You are here");
            map.getOverlays().add(marker);

            map.invalidate();

        } else {
            Toast.makeText(this, "Unable to get location. Turn on GPS.", Toast.LENGTH_LONG).show();
        }
    }

    // ✅ Permission result handler
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}