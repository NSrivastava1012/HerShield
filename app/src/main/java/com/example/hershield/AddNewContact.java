package com.example.hershield;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AddNewContact extends AppCompatActivity {

    private EditText etName, etPhone;
    private AppCompatButton btnAdd;
    private RequestQueue queue;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        queue = Volley.newRequestQueue(this);

        // Get user_id from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("HerShield", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        btnAdd = findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(v -> saveContact());
        setupNavigationIcons();
    }

    private void saveContact() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { etName.setError("Please enter a name"); return; }
        if (TextUtils.isEmpty(phone)) { etPhone.setError("Please enter a phone number"); return; }

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
                    Toast.makeText(this, "Contact added: " + name, Toast.LENGTH_LONG).show();
                    etName.setText("");
                    etPhone.setText("");
                },
                error -> Toast.makeText(this, "Failed to add contact", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }
    private void setupNavigationIcons() {
        findViewById(R.id.settings_icon).setOnClickListener(v ->
                startActivity(new Intent(this, HomePage.class)));
    }
}