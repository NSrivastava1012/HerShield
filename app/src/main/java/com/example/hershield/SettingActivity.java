package com.example.hershield;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private boolean isNotifOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        FrameLayout switchNotif = findViewById(R.id.switch_notifications);
        View switchThumb = findViewById(R.id.switch_thumb);

        switchNotif.setOnClickListener(v -> {
            isNotifOn = !isNotifOn;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) switchThumb.getLayoutParams();
            if (isNotifOn) {
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
                Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
                params.setMarginStart(2);
                Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
            switchThumb.setLayoutParams(params);
        });

        RelativeLayout itemShare = findViewById(R.id.item_share);
        RelativeLayout itemAbout = findViewById(R.id.item_about);
        RelativeLayout itemFeedback = findViewById(R.id.item_feedback);
        RelativeLayout itemLogout = findViewById(R.id.item_logout);

        itemShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Download HerShield for your safety!");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        itemAbout.setOnClickListener(v ->
                Toast.makeText(this, "HerShield Version 1.0", Toast.LENGTH_SHORT).show());

        itemFeedback.setOnClickListener(v ->
                Toast.makeText(this, "Feedback form opening...", Toast.LENGTH_SHORT).show());

        // Logout — clear session and go to Login
        itemLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("HerShield", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.settings_icon_top).setOnClickListener(v -> finish());
    }
}