package com.example.hershield;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class EmergencyContactPageActivity extends AppCompatActivity {

    private RequestQueue queue;
    private int userId;
    private LinearLayout contactsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_page);

        queue = Volley.newRequestQueue(this);

        SharedPreferences prefs = getSharedPreferences("HerShield", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        contactsContainer = findViewById(R.id.contactsContainer);

        AppCompatButton btnAddContacts = findViewById(R.id.btn_add_contacts);
        btnAddContacts.setOnClickListener(v ->
                startActivity(new Intent(this, AddNewContact.class)));

        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, HomePage.class));
            finish();
        });

        findViewById(R.id.nav_location).setOnClickListener(v ->
                startActivity(new Intent(this, LocationActivity.class)));

        findViewById(R.id.nav_call).setOnClickListener(v ->
                Toast.makeText(this, "You are already on the Contacts screen", Toast.LENGTH_SHORT).show());

        findViewById(R.id.nav_profile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        // Load contacts from server
        loadContacts();
        setupNavigationIcons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts(); // Refresh when returning from AddNewContact
    }

    private void loadContacts() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiClient.BASE_URL + "/contacts/" + userId,
                null,
                response -> {
                    try {
                        JSONArray contacts = response.getJSONArray("contacts");
                        if (contactsContainer != null) {
                            contactsContainer.removeAllViews();
                            for (int i = 0; i < contacts.length(); i++) {
                                String name = contacts.getJSONObject(i).getString("name");
                                String phone = contacts.getJSONObject(i).getString("phone");

                                TextView tv = new TextView(this);
                                tv.setText((i + 1) + ". " + name + " — " + phone);
                                tv.setTextSize(16);
                                tv.setPadding(16, 12, 16, 12);
                                contactsContainer.addView(tv);
                            }
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(this, "Failed to load contacts", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    private void setupNavigationIcons() {
        findViewById(R.id.settings_icon).setOnClickListener(v ->
                startActivity(new Intent(this, HomePage.class)));
    }
}