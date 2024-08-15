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

public class OpeningAddNumbers extends AppCompatActivity {

    private int position = -1;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "MyAppPreferences";
    private static final String CONTACTS_SAVED_KEY = "ContactsSaved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the intent has edit data
        Intent intent = getIntent();
        boolean isEditMode = intent != null && intent.hasExtra("name") && intent.hasExtra("contact");

        if (!isEditMode) {
            SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            boolean contactsSaved = preferences.getBoolean(CONTACTS_SAVED_KEY, false);

            if (contactsSaved) {
                // If contacts are already saved, go directly to MainActivity
                Intent mainActivityIntent = new Intent(OpeningAddNumbers.this, MainActivity.class);
                startActivity(mainActivityIntent);
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_opening_add_numbers);

        sharedPreferences = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);

        Button saveButton = findViewById(R.id.contactsave);
        EditText name1 = findViewById(R.id.name1);
        EditText contact1 = findViewById(R.id.contact1);
        EditText name2 = findViewById(R.id.name2);
        EditText contact2 = findViewById(R.id.contact2);
        EditText name3 = findViewById(R.id.name3);
        EditText contact3 = findViewById(R.id.contact3);

        if (isEditMode) {
            name1.setText(intent.getStringExtra("name"));
            contact1.setText(intent.getStringExtra("contact"));
            position = intent.getIntExtra("position", -1);

            // Hide other fields during CRUD operations
            name2.setVisibility(View.GONE);
            contact2.setVisibility(View.GONE);
            name3.setVisibility(View.GONE);
            contact3.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> {
            if (isValidContact(contact1) && (position != -1 || (isValidContact(contact2) && isValidContact(contact3)))) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (position == -1) {
                    // Adding three contacts initially
                    editor.putString("name1", name1.getText().toString());
                    editor.putString("contact1", contact1.getText().toString());
                    editor.putString("name2", name2.getText().toString());
                    editor.putString("contact2", contact2.getText().toString());
                    editor.putString("name3", name3.getText().toString());
                    editor.putString("contact3", contact3.getText().toString());
                } else {
                    // Updating an existing contact
                    editor.putString("name" + (position + 1), name1.getText().toString());
                    editor.putString("contact" + (position + 1), contact1.getText().toString());
                }

                editor.apply();

                Toast.makeText(OpeningAddNumbers.this, "Contact Saved", Toast.LENGTH_SHORT).show();

                // Mark that the contacts have been saved
                SharedPreferences.Editor prefEditor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                prefEditor.putBoolean(CONTACTS_SAVED_KEY, true);
                prefEditor.apply();

                // Now redirect to MainActivity
                Intent mainActivityIntent = new Intent(OpeningAddNumbers.this, MainActivity.class);
                startActivity(mainActivityIntent);
                finish();
            } else {
                Toast.makeText(OpeningAddNumbers.this, "Each contact number must be exactly 10 digits", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (position == -1) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(OpeningAddNumbers.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean isValidContact(EditText contact) {
        String contactStr = contact.getText().toString().trim();
        return !TextUtils.isEmpty(contactStr) && contactStr.length() == 10;
    }
}
