package com.example.home;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class AddFriendsFragment extends Fragment implements FragmentUpdateListener {

    private static final int REQUEST_CALL_PHONE = 1;
    private ContactAdapterFriends adapter;
    private List<String> contactsList;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addfriends, container, false);

        ListView contactsListView = view.findViewById(R.id.contactsListView);
        Button addContactButton = view.findViewById(R.id.addContactButton);
        contactsList = new ArrayList<>();
        sharedPreferences = requireActivity().getSharedPreferences("EmergencyContacts", getContext().MODE_PRIVATE);

        // Check if the CALL_PHONE permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if not granted
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        } else {
            // If permission is already granted, load contacts
            loadContacts();
        }

        adapter = new ContactAdapterFriends(requireContext(), contactsList);
        contactsListView.setAdapter(adapter);

        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NewContact.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadContacts() {
        contactsList.clear();

        for (int i = 1; i <= 10; i++) {
            String name = sharedPreferences.getString("name" + i, "");
            String contact = sharedPreferences.getString("contact" + i, "");
            if (!name.isEmpty() && !contact.isEmpty()) {
                contactsList.add("Emergency: " + name + ": " + contact);
            }
        }

        if (!contactsList.isEmpty()) {
            contactsList.add("------- New Contacts -------");
        }

        SharedPreferences newContactsPrefs = requireActivity().getSharedPreferences("NewContacts", getContext().MODE_PRIVATE);
        for (int i = 1; i <= 10; i++) {
            String name = newContactsPrefs.getString("name" + i, "");
            String contact = newContactsPrefs.getString("contact" + i, "");
            if (!name.isEmpty() && !contact.isEmpty()) {
                contactsList.add("New: " + name + ": " + contact);
            }
        }

        if (contactsList.isEmpty()) {
            contactsList.add("No contacts saved.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadContacts(); // Reload contacts when the fragment is resumed
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Call permission granted", Toast.LENGTH_SHORT).show();
                // Permission granted, proceed with loading contacts
                loadContacts();
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(requireContext(), "Call permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onContactDeleted() {
        // Reload contacts when a contact is deleted
        loadContacts();
        adapter.notifyDataSetChanged();
    }
}
