package com.example.hershield;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.widget.ImageButton;
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
        // Ensure this matches your XML file name
        setContentView(R.layout.activity_home_page);

        // 1. Initialize Battery Components
        battery_progress_bar = findViewById(R.id.battery_progress_bar);
        tv_battery_percentage = findViewById(R.id.tv_battery_percentage);

        // Set initial battery state (75% as seen in your XML)
        updateBluetoothBatteryUI(75, true);

        // 2. Initialize Views & Buttons
        CardView btnBatteryLife = findViewById(R.id.btn_battery_life);
        MaterialCardView widgetEmergency = findViewById(R.id.widget_1);
        MaterialCardView widgetPhone = findViewById(R.id.widget_2);
        MaterialCardView widgetLocation = findViewById(R.id.widget_3);
        MaterialCardView widgetProfile = findViewById(R.id.widget_4);

        ImageButton contact1 = findViewById(R.id.contact_1);
        ImageButton contact2 = findViewById(R.id.contact_2);
        ImageButton contact3 = findViewById(R.id.contact_3);

        // 3. Set Click Listeners for Widgets/Buttons
        btnBatteryLife.setOnClickListener(v -> showToast("Checking device battery status..."));

        widgetEmergency.setOnClickListener(v -> {
            showToast("SOS: Sending emergency alerts!");
            // Add SOS logic here
        });

        widgetPhone.setOnClickListener(v -> showToast("Opening Dialer..."));

        widgetLocation.setOnClickListener(v -> showToast("Fetching current location..."));

        contact1.setOnClickListener(v -> showToast("Calling Primary Contact"));

        findViewById(R.id.settings_icon).setOnClickListener(v -> {
            // Optional: Start Settings Activity
            showToast("Settings opened");
        });

        // 4. Initialize Bottom Navigation Click Listeners
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Since you are using a custom layout for Nav, we find them by their structure or IDs
        // For example, if you add IDs to the ImageViews in the bottom nav:

        // Example for Profile (The last ImageView in your Bottom Nav)
        // findViewById(R.id.nav_profile).setOnClickListener(v -> {
        //    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        // });
    }

    /**
     * Updates the battery ring percentage and colors dynamically
     */
    private void updateBluetoothBatteryUI(int level, boolean isCharging) {
        battery_progress_bar.setProgress(level);
        tv_battery_percentage.setText(level + "%");

        try {
            // Access the circular_progress_bar drawable to change colors programmatically
            RotateDrawable rotateDrawable = (RotateDrawable) battery_progress_bar.getProgressDrawable();
            GradientDrawable gradient = (GradientDrawable) rotateDrawable.getDrawable();

            if (isCharging) {
                // Pink/Purple palette for charging
                gradient.setColors(new int[]{0xFFCA62BF, 0xFF97498F, 0xFF64305F});
            } else if (level < 20) {
                // Red palette for low battery
                gradient.setColors(new int[]{0xFFFF5252, 0xFFD50000, 0xFF880E4F});
            } else {
                // Default Purple palette
                gradient.setColors(new int[]{0xFFEDCCF3, 0xFFC59EE7, 0xFF97498F});
            }
            gradient.setGradientType(GradientDrawable.SWEEP_GRADIENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}