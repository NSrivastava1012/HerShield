package com.example.hershield;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnContinue;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        queue = Volley.newRequestQueue(this);

        etEmail = findViewById(R.id.etEmail);
        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter a valid email address");
                return;
            }

            // Check if email exists in database
            JSONObject body = new JSONObject();
            try {
                body.put("email", email);
            } catch (JSONException e) { e.printStackTrace(); }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiClient.BASE_URL + "/forgot-password",
                    body,
                    response -> {
                        try {
                            if (response.getString("status").equals("success")) {
                                Toast.makeText(this, "Email verified", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, ResetPasswordActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            } else {
                                etEmail.setError("Email not registered");
                            }
                        } catch (JSONException e) { e.printStackTrace(); }
                    },
                    error -> Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            );
            queue.add(request);
        });
    }
}