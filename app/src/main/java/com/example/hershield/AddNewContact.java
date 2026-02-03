package com.example.hershield;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class AddNewContact extends AppCompatActivity {

    // Declare UI elements
    private EditText etName;
    private EditText etPhone;
    private AppCompatButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        // 1. Initialize views using the IDs from your XML
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        btnAdd = findViewById(R.id.btn_add);

        // 2. Set click listener for the "Add" button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });
    }

    private void saveContact() {
        // Get data from input fields
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // 3. Simple Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Please enter a name");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Please enter a phone number");
            return;
        }

        // 4. Logic to save the contact
        // For now, we display a Toast message
        String message = "Contact Added: " + name + " (" + phone + ")";
        Toast.makeText(AddNewContact.this, message, Toast.LENGTH_LONG).show();

        // Optional: Clear fields after adding
        etName.setText("");
        etPhone.setText("");
    }
}