package com.example.hershield;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etPhone, etEmail, etPassword;
    Button btnRegister;
    TextView tvLogin;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        queue = Volley.newRequestQueue(this);

        etName = findViewById(R.id.etRegisterName);
        etPhone = findViewById(R.id.etRegisterPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvRegisterLogin);

        btnRegister.setOnClickListener(view -> registerUser());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { etName.setError("Name required"); return; }
        if (TextUtils.isEmpty(phone) || phone.length() < 10) { etPhone.setError("Valid phone required"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("Valid email required"); return; }
        if (password.length() < 8) { etPassword.setError("Min 8 characters"); return; }

        JSONObject body = new JSONObject();
        try {
            body.put("name", name);
            body.put("phone", phone);
            body.put("email", email);
            body.put("password", password);
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiClient.BASE_URL + "/register",
                body,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            // 1. Extract the user_id that Flask just sent back
                            int userId = response.getInt("user_id");

                            Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show();

                            // 2. Create the Intent for the next screen
                            Intent intent = new Intent(this, AddContactActivity.class);

                            // 3. "Put" the ID into the intent so the next activity can see it
                            intent.putExtra("USER_ID", userId);

                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }
}