package com.example.hershield;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AddContactActivity extends AppCompatActivity {

    private EditText etName1, etPhone1, etName2, etPhone2, etName3, etPhone3;
    private Button btnSubmit;
    private RequestQueue queue;
    private int userId = -1; // class field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        queue = Volley.newRequestQueue(this);

        // Get user_id from Intent or SharedPreferences — assigned to CLASS field, not local variable
        SharedPreferences prefs = getSharedPreferences("HerShield", MODE_PRIVATE);
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            userId = prefs.getInt("user_id", -1);
        } else {
            prefs.edit().putInt("user_id", userId).apply();
        }

        // Show toast to confirm userId is correct
        Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_LONG).show();

        if (userId == -1) {
            Toast.makeText(this, "User session error!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName1 = findViewById(R.id.etName1);
        etPhone1 = findViewById(R.id.etPhone1);
        etName2 = findViewById(R.id.etName2);
        etPhone2 = findViewById(R.id.etPhone2);
        etName3 = findViewById(R.id.etName3);
        etPhone3 = findViewById(R.id.etPhone3);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        String name1 = etName1.getText().toString().trim();
        String phone1 = etPhone1.getText().toString().trim();
        String name2 = etName2.getText().toString().trim();
        String phone2 = etPhone2.getText().toString().trim();
        String name3 = etName3.getText().toString().trim();
        String phone3 = etPhone3.getText().toString().trim();

        if (TextUtils.isEmpty(name1) || TextUtils.isEmpty(phone1) ||
                TextUtils.isEmpty(name2) || TextUtils.isEmpty(phone2) ||
                TextUtils.isEmpty(name3) || TextUtils.isEmpty(phone3)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone1.matches("\\d{10}") || !phone2.matches("\\d{10}") || !phone3.matches("\\d{10}")) {
            Toast.makeText(this, "Enter valid 10-digit phone numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        saveContact(name1, phone1, false);
        saveContact(name2, phone2, false);
        saveContact(name3, phone3, true);
    }

    private void saveContact(String name, String phone, boolean isLast) {
        JSONObject body = new JSONObject();
        try {
            body.put("user_id", userId);
            body.put("name", name);
            body.put("phone", phone);
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiClient.BASE_URL + "/contacts/add",
                body,
                response -> {
                    if (isLast) {
                        Toast.makeText(this, "Contacts saved successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                },
                error -> Toast.makeText(this, "Failed to save contact: " + name, Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }
}