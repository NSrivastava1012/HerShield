package com.example.hershield;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.card.MaterialCardView;

public class HomePage extends AppCompatActivity {

    private ProgressBar battery_progress_bar;
    private TextView tv_battery_percentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // 1. Initialize UI Components
        battery_progress_bar = findViewById(R.id.battery_progress_bar);
        tv_battery_percentage = findViewById(R.id.tv_battery_percentage);

        // Initial battery UI state
        updateBluetoothBatteryUI(75, true);

        // 2. Setup Widget Navigation
        setupWidgetNavigation();

        // 3. Setup Bottom Navigation & Icons
        setupNavigationIcons();
    }

    private void setupWidgetNavigation() {
        // SOS / Emergency Widget
        MaterialCardView widgetEmergency = findViewById(R.id.widget_1);
        widgetEmergency.setOnClickListener(v -> {
            // Navigates to Emergency Contact Page
            startActivity(new Intent(HomePage.this, HelplineNumberActivity.class));
        });

        // Phone/Dialer Widget
        MaterialCardView widgetPhone = findViewById(R.id.widget_2);
        widgetPhone.setOnClickListener(v -> {
            startActivity(new Intent(HomePage.this, EmergencyContactPageActivity.class));
        });

        // Location Widget
        MaterialCardView widgetLocation = findViewById(R.id.widget_3);
        widgetLocation.setOnClickListener(v -> {
            startActivity(new Intent(HomePage.this, ProfileActivity.class));
        });

        // Profile Widget
        MaterialCardView widgetProfile = findViewById(R.id.widget_4);
        widgetProfile.setOnClickListener(v -> {
            startActivity(new Intent(HomePage.this, ProfileActivity.class));
        });

        // Contacts
        findViewById(R.id.contact_1).setOnClickListener(v -> showToast("Calling Primary Contact..."));
    }

    private void setupNavigationIcons() {
        // Settings Icon (Top Right)
        findViewById(R.id.settings_icon).setOnClickListener(v -> {
            startActivity(new Intent(HomePage.this, SettingActivity.class));
        });
    }

    private void updateBluetoothBatteryUI(int level, boolean isCharging) {
        battery_progress_bar.setProgress(level);
        tv_battery_percentage.setText(level + "%");
        try {
            RotateDrawable rotateDrawable = (RotateDrawable) battery_progress_bar.getProgressDrawable();
            GradientDrawable gradient = (GradientDrawable) rotateDrawable.getDrawable();
            if (isCharging) {
                gradient.setColors(new int[]{0xFFCA62BF, 0xFF97498F, 0xFF64305F});
            } else {
                gradient.setColors(new int[]{0xFFEDCCF3, 0xFFC59EE7, 0xFF97498F});
            }
            gradient.setGradientType(GradientDrawable.SWEEP_GRADIENT);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}