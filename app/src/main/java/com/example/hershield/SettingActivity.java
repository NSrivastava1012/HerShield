package com.example.hershield;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private boolean isNotifOn = true; // Tracks the state of the custom switch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 1. Initialize the Custom Toggle Switch
        FrameLayout switchNotif = findViewById(R.id.switch_notifications);
        View switchThumb = findViewById(R.id.switch_thumb);

        switchNotif.setOnClickListener(v -> {
            // Toggle the state
            isNotifOn = !isNotifOn;

            // Get current layout parameters of the thumb
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) switchThumb.getLayoutParams();

            if (isNotifOn) {
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
                showToast("Notifications Enabled");
            } else {
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
                params.setMarginStart(2); // Match the layout look
                showToast("Notifications Disabled");
            }

            switchThumb.setLayoutParams(params);
        });

        // 2. Initialize Menu Items
        RelativeLayout itemShare = findViewById(R.id.item_share);
        RelativeLayout itemAbout = findViewById(R.id.item_about);
        RelativeLayout itemFeedback = findViewById(R.id.item_feedback);
        RelativeLayout itemLogout = findViewById(R.id.item_logout);

        // Share App Logic
        itemShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Download HerShield for your safety!");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        // Logout Logic
        itemLogout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
            // Clear activity stack so user cannot go back
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            showToast("Logged out successfully");
        });

        // Placeholder logic for others
        itemAbout.setOnClickListener(v -> showToast("HerShield Version 1.0"));
        itemFeedback.setOnClickListener(v -> showToast("Feedback form opening..."));

        // Settings/Home Icon at top
        findViewById(R.id.settings_icon_top).setOnClickListener(v -> finish());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}