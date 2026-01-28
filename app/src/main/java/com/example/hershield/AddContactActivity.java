package com.example.hershield;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {

    // EditTexts
    private EditText etName1, etPhone1;
    private EditText etName2, etPhone2;
    private EditText etName3, etPhone3;

    // Button
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Initialize views
        etName1 = findViewById(R.id.etName1);
        etPhone1 = findViewById(R.id.etPhone1);

        etName2 = findViewById(R.id.etName2);
        etPhone2 = findViewById(R.id.etPhone2);

        etName3 = findViewById(R.id.etName3);
        etPhone3 = findViewById(R.id.etPhone3);

        Button btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            Intent intent = new Intent(AddContactActivity.this, LoginActivity.class);

            // Clear all previous activities
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }

    private void handleSubmit() {
        // Get text from EditTexts
        String name1 = etName1.getText().toString().trim();
        String phone1 = etPhone1.getText().toString().trim();

        String name2 = etName2.getText().toString().trim();
        String phone2 = etPhone2.getText().toString().trim();

        String name3 = etName3.getText().toString().trim();
        String phone3 = etPhone3.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name1) || TextUtils.isEmpty(phone1) ||
                TextUtils.isEmpty(name2) || TextUtils.isEmpty(phone2) ||
                TextUtils.isEmpty(name3) || TextUtils.isEmpty(phone3)) {

            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Optionally, validate phone numbers
        if (!phone1.matches("\\d{10}") || !phone2.matches("\\d{10}") || !phone3.matches("\\d{10}")) {
            Toast.makeText(this, "Enter valid 10-digit phone numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        // Here you can save data to database or send to another activity
        // For now, just show a success message
        Toast.makeText(this, "Contacts added successfully!", Toast.LENGTH_SHORT).show();

        // Optional: Clear fields after submission
        clearFields();
    }

    private void clearFields() {
        etName1.setText("");
        etPhone1.setText("");
        etName2.setText("");
        etPhone2.setText("");
        etName3.setText("");
        etPhone3.setText("");
    }
}
