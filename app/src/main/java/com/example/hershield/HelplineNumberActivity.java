package com.example.hershield;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HelplineNumberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline_numbers);

        // Initialize and setup buttons
        setupCallingButtons();
    }

    private void setupCallingButtons() {
        // Police - 100
        findViewById(R.id.call_police).setOnClickListener(v -> makeCall("100"));

        // Pregnancy Medic - Example: 102
        findViewById(R.id.call_medic).setOnClickListener(v -> makeCall("102"));

        // Ambulance - 108
        findViewById(R.id.call_ambulance).setOnClickListener(v -> makeCall("108"));

        // Fire Service - 101
        findViewById(R.id.call_fire_service).setOnClickListener(v -> makeCall("101"));

        // Women Helpline - 1091
        findViewById(R.id.call_women_helpline).setOnClickListener(v -> makeCall("1091"));

        // Child Helpline - 1098
        findViewById(R.id.call_child_helpline).setOnClickListener(v -> makeCall("1098"));
    }

    /**
     * Opens the dialer with the specified number
     */
    private void makeCall(String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open dialer", Toast.LENGTH_SHORT).show();
        }
    }
}