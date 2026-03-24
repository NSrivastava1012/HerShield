package com.example.hershield;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;
    TextView tvForgetPassword;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        queue = Volley.newRequestQueue(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) { etEmail.setError("Email required"); return; }
            if (TextUtils.isEmpty(password)) { etPassword.setError("Password required"); return; }

            JSONObject body = new JSONObject();
            try {
                body.put("email", email);
                body.put("password", password);
            } catch (JSONException e) { e.printStackTrace(); }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiClient.BASE_URL + "/login",
                    body,
                    response -> {
                        try {
                            if (response.getString("status").equals("success")) {
                                // Save user info
                                SharedPreferences prefs = getSharedPreferences("HerShield", MODE_PRIVATE);
                                prefs.edit()
                                        .putInt("user_id", response.getInt("user_id"))
                                        .putString("name", response.getString("name"))
                                        .apply();

                                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, HomePage.class));
                                finish();
                            }
                        } catch (JSONException e) { e.printStackTrace(); }
                    },
                    error -> Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            );
            queue.add(request);
        });

        btnRegister.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class)));

        tvForgetPassword.setOnClickListener(view ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }
}