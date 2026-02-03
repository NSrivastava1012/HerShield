package com.example.hershield;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ProfileActivity extends AppCompatActivity {

    private EditText etUserName, etPhoneNumber;
    private AppCompatButton btnSave, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure this matches the XML filename you provided
        setContentView(R.layout.activity_profile);

        // 1. Initialize UI Elements
        etUserName = findViewById(R.id.et_user_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnSave = findViewById(R.id.btn_save);
        btnLogout = findViewById(R.id.btn_logout);

        // 2. Set Click Listener for Save Button
        btnSave.setOnClickListener(v -> {
            String name = etUserName.getText().toString().trim();
            String phone = etPhoneNumber.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Here you would typically save to SharedPreferences or a Database
                Toast.makeText(ProfileActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. Set Click Listener for Logout Button
        btnLogout.setOnClickListener(v -> {
            // Navigate back to LoginActivity
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

            // Clear the activity stack so the user can't press 'Back' to return to the profile
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish(); // Close the current Profile activity

            Toast.makeText(ProfileActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
        });
    }
}