package com.example.hershield;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;

public class HomePage extends AppCompatActivity {

    private ProgressBar battery_progress_bar;
    private TextView tv_battery_percentage;
    private RequestQueue queue;
    private BluetoothService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        queue = Volley.newRequestQueue(this);

        // Show welcome message
        SharedPreferences prefs = getSharedPreferences("HerShield", MODE_PRIVATE);
        String name = prefs.getString("name", "");
        if (!name.isEmpty()) {
            Toast.makeText(this, "Welcome, " + name + "!", Toast.LENGTH_SHORT).show();
        }

        battery_progress_bar = findViewById(R.id.battery_progress_bar);
        tv_battery_percentage = findViewById(R.id.tv_battery_percentage);
        updateBluetoothBatteryUI(0, false); // Start at 0 until hardware connects

        setupWidgetNavigation();
        setupNavigationIcons();
        loadSavedContacts();
        setupBluetooth(); // Connect to ESP32
    }

    private void setupBluetooth() {
        bluetoothService = new BluetoothService(this, new BluetoothService.BluetoothListener() {
            @Override
            public void onConnected() {
                Toast.makeText(HomePage.this, "Hardware connected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnected() {
                Toast.makeText(HomePage.this, "Hardware disconnected", Toast.LENGTH_SHORT).show();
                updateBluetoothBatteryUI(0, false);
            }

            @Override
            public void onSOSReceived() {
                // Navigate to LocationActivity and trigger SOS
                Intent intent = new Intent(HomePage.this, LocationActivity.class);
                intent.putExtra("TRIGGER_SOS", true);
                startActivity(intent);
            }

            @Override
            public void onBatteryReceived(int level) {
                // Update battery ring with hardware battery level
                updateBluetoothBatteryUI(level, true);
            }

            @Override
            public void onConnectionFailed(String error) {
                Toast.makeText(HomePage.this, error, Toast.LENGTH_LONG).show();
                // Keep showing 0% if hardware not connected
                updateBluetoothBatteryUI(0, false);
            }
        });

        bluetoothService.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothService != null) bluetoothService.disconnect();
    }

    private void setupWidgetNavigation() {
        MaterialCardView widgetEmergency = findViewById(R.id.widget_1);
        widgetEmergency.setOnClickListener(v ->
                startActivity(new Intent(this, HelplineNumberActivity.class)));

        MaterialCardView widgetPhone = findViewById(R.id.widget_2);
        widgetPhone.setOnClickListener(v ->
                startActivity(new Intent(this, EmergencyContactPageActivity.class)));

        MaterialCardView widgetLocation = findViewById(R.id.widget_3);
        widgetLocation.setOnClickListener(v ->
                startActivity(new Intent(this, LocationActivity.class)));

        MaterialCardView widgetProfile = findViewById(R.id.widget_4);
        widgetProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void setupNavigationIcons() {
        findViewById(R.id.settings_icon).setOnClickListener(v ->
                startActivity(new Intent(this, SettingActivity.class)));
    }

    private void loadSavedContacts() {
        SharedPreferences prefs = getSharedPreferences("HerShield", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) return;

        int[] contactLayoutIds = {R.id.contact_1, R.id.contact_2, R.id.contact_3};
        int[] contactNameIds = {R.id.tv_contact_name_1, R.id.tv_contact_name_2, R.id.tv_contact_name_3};

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiClient.BASE_URL + "/contacts/" + userId,
                null,
                response -> {
                    try {
                        JSONArray contacts = response.getJSONArray("contacts");
                        for (int i = 0; i < contactLayoutIds.length; i++) {
                            if (i < contacts.length()) {
                                String contactName = contacts.getJSONObject(i).getString("name");
                                String contactPhone = contacts.getJSONObject(i).getString("phone");

                                TextView tv = findViewById(contactNameIds[i]);
                                if (tv != null) tv.setText(contactName);

                                LinearLayout contactLayout = findViewById(contactLayoutIds[i]);
                                final String phone = contactPhone;
                                if (contactLayout != null) {
                                    contactLayout.setOnClickListener(v -> {
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                        callIntent.setData(android.net.Uri.parse("tel:" + phone));
                                        startActivity(callIntent);
                                    });
                                }
                            }
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(this, "Could not load contacts", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    private void updateBluetoothBatteryUI(int level, boolean isConnected) {
        battery_progress_bar.setProgress(level);
        tv_battery_percentage.setText(level + "%");
        try {
            RotateDrawable rotateDrawable = (RotateDrawable) battery_progress_bar.getProgressDrawable();
            GradientDrawable gradient = (GradientDrawable) rotateDrawable.getDrawable();
            if (isConnected) {
                // Purple gradient when hardware connected
                gradient.setColors(new int[]{0xFFCA62BF, 0xFF97498F, 0xFF64305F});
            } else {
                // Grey gradient when hardware not connected
                gradient.setColors(new int[]{0xFFCCCCCC, 0xFF999999, 0xFF666666});
            }
            gradient.setGradientType(GradientDrawable.SWEEP_GRADIENT);
        } catch (Exception e) { e.printStackTrace(); }
    }
}