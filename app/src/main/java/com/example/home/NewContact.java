package com.example.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewContact extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newcontact);

        sharedPreferences = getSharedPreferences("NewContacts", MODE_PRIVATE);

        Button saveButton = findViewById(R.id.newcontact);
        EditText name = findViewById(R.id.name3);
        EditText contact = findViewById(R.id.contact3);

        saveButton.setOnClickListener(v -> {
            if (isValidContact(contact)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String nameValue = name.getText().toString();
                String contactValue = contact.getText().toString();

                int newContactIndex = getNextContactIndex();
                if (newContactIndex != -1) {
                    editor.putString("name" + newContactIndex, nameValue);
                    editor.putString("contact" + newContactIndex, contactValue);
                    editor.apply();

                    Toast.makeText(NewContact.this, "Contact Saved", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(NewContact.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(NewContact.this, "Maximum contacts reached", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(NewContact.this, "Contact number must be exactly 10 digits", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidContact(EditText contact) {
        String contactStr = contact.getText().toString().trim();
        return !TextUtils.isEmpty(contactStr) && contactStr.length() == 10;
    }

    private int getNextContactIndex() {
        for (int i = 1; i <= 10; i++) {
            if (sharedPreferences.getString("name" + i, "").isEmpty() &&
                    sharedPreferences.getString("contact" + i, "").isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}
