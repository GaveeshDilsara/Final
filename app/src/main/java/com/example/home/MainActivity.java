package com.example.home;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_SMS_PERMISSION = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 3;
    private static final int REQUEST_CHECK_SETTINGS = 4;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Setup Drawer Layout and Navigation View
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Setup Navigation Drawer Listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Setup Bottom Navigation View
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Default Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new FragmentHome()).commit();
        }

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Location Callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    sendEmergencyMessage("Emergency! Please help me immediately. Location unavailable.");
                    return;
                }
                Location location = locationResult.getLastLocation();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String locationMessage = "Emergency! Please help me immediately. My location is: " +
                        "https://maps.google.com/?q=" + latitude + "," + longitude;
                sendEmergencyMessage(locationMessage);

                // Stop location updates after getting a location
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        };
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.home:
                            selectedFragment = new FragmentHome();
                            break;

                        case R.id.recording:
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                            } else {
                                Intent intent = new Intent(MainActivity.this, VideoRecordingActivity.class);
                                startActivity(intent);
                            }
                            return true;

                        case R.id.sos:
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                requestSmsPermission();
                            } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                requestLocationPermission();
                            } else {
                                sendEmergencyMessageWithLocation();
                            }

                            // Load SOSFragment instead of starting it as an activity
                            selectedFragment = new SOSFragment();
                            break;

                        case R.id.call24:
                            selectedFragment = new Call247Fragment();
                            break;

                        case R.id.add_friends:
                            selectedFragment = new AddFriendsFragment();
                            break;
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, selectedFragment)
                                .addToBackStack(null)
                                .commit();
                    }

                    return true;
                }
            };

    private void handleNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new FragmentHome())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.history:
                Toast.makeText(MainActivity.this, "History Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.contactlist:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new AddFriendsFragment())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.about:
                Intent intent = new Intent(MainActivity.this, AboutUs.class);
                startActivity(intent);
                break;

            case R.id.login:
                Toast.makeText(MainActivity.this, "Log In Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.share:
                Toast.makeText(MainActivity.this, "Share Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.rate_us:
                Toast.makeText(MainActivity.this, "Rate Us Selected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.self_defence:
                Toast.makeText(MainActivity.this, "Self Defence Selected", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MainActivity.this, VideoRecordingActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermission();
                } else {
                    sendEmergencyMessageWithLocation();
                }
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendEmergencyMessageWithLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendEmergencyMessageWithLocation() {
        // Create location request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);  // Update location every 10 seconds
        locationRequest.setFastestInterval(5000);  // Fastest interval to get location

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // Location settings are satisfied, request location updates
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof com.google.android.gms.common.api.ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed by showing the user a dialog
                            try {
                                com.google.android.gms.common.api.ResolvableApiException resolvable = (com.google.android.gms.common.api.ResolvableApiException) e;
                                resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                sendEmergencyMessage("Emergency! Please help me immediately. Location unavailable.");
                            }
                        } else {
                            sendEmergencyMessage("Emergency! Please help me immediately. Location unavailable.");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // User agreed to make required location settings changes
                sendEmergencyMessageWithLocation();
            } else {
                // User did not agree to make required location settings changes
                sendEmergencyMessage("Emergency! Please help me immediately. Location unavailable.");
            }
        }
    }

    private void sendEmergencyMessage(String message) {
        SharedPreferences sharedPreferences = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);

        String contact1 = sharedPreferences.getString("contact1", "");
        String contact2 = sharedPreferences.getString("contact2", "");
        String contact3 = sharedPreferences.getString("contact3", "");

        if (!contact1.isEmpty()) {
            sendSms(contact1, message);
        }
        if (!contact2.isEmpty()) {
            sendSms(contact2, message);
        }
        if (!contact3.isEmpty()) {
            sendSms(contact3, message);
        }

        Toast.makeText(this, "Emergency message sent to contacts", Toast.LENGTH_SHORT).show();
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
