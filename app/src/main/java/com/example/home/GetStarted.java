package com.example.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GetStarted extends AppCompatActivity {

    private static final String PREF_NAME = "MyAppPreferences";
    private static final String FIRST_TIME_KEY = "FirstTimeLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the app is being launched for the first time
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(FIRST_TIME_KEY, true);

        if (!isFirstTime) {
            // If it's not the first time, go directly to MainActivity
            Intent intent = new Intent(GetStarted.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_get_started);

        Button getStartedButton = findViewById(R.id.getstarted);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mark that the app has been launched for the first time
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(FIRST_TIME_KEY, false);
                editor.apply();

                // Go to OpeningAddNumbers activity
                Intent intent = new Intent(GetStarted.this, OpeningAddNumbers.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
