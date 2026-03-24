package com.example.hershield;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

public class ProfileActivity extends AppCompatActivity {

    private TextView etUserName, etPhoneNumber;
    private AppCompatButton btnLogout;
    private RequestQueue queue;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        queue = Volley.newRequestQueue(this);

        SharedPreferences prefs = getSharedPreferences("HerShield", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        // Change EditText to TextView in code — disable editing
        etUserName = findViewById(R.id.et_user_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnLogout = findViewById(R.id.btn_logout);

        // Disable editing
        etUserName.setFocusable(false);
        etUserName.setClickable(false);
        etUserName.setCursorVisible(false);

        etPhoneNumber.setFocusable(false);
        etPhoneNumber.setClickable(false);
        etPhoneNumber.setCursorVisible(false);


        // Load profile from server
        loadProfile();
        setupNavigationIcons();

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadProfile() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ApiClient.BASE_URL + "/profile/" + userId,
                null,
                response -> {
                    try {
                        etUserName.setText(response.getString("name"));
                        etPhoneNumber.setText(response.getString("phone"));
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    private void setupNavigationIcons() {
        findViewById(R.id.settings_icon).setOnClickListener(v ->
                startActivity(new Intent(this, SettingActivity.class)));
    }
}