package com.example.hershield;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class EmergencyContactPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_page);

        // 1. Initialize the "Add Contacts" button
        AppCompatButton btnAddContacts = findViewById(R.id.btn_add_contacts);

        // 2. Set Click Listener to navigate to the Add Contact page
        btnAddContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace 'AddContactActivity.class' with the actual name of your Add Contact class
                Intent intent = new Intent(EmergencyContactPageActivity.this, AddNewContact.class);
                startActivity(intent);
            }
        });

        // 2. Setup Settings Icon Click
        findViewById(R.id.settings_icon).setOnClickListener(v -> {
            showToast("Settings Clicked");
        });

        // 3. Setup Bottom Navigation Clicks
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Home Navigation
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
            // Finish this activity so it doesn't stay in the back stack
            finish();
        });

        // Location Navigation
        findViewById(R.id.nav_location).setOnClickListener(v -> {
            showToast("Navigating to Location Screen");
        });

        // Call Navigation (Current Screen - Optional to just refresh or do nothing)
        findViewById(R.id.nav_call).setOnClickListener(v -> {
            showToast("You are already on the Contacts screen");
        });

        // Profile Navigation
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            showToast("Navigating to Profile Screen");
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}