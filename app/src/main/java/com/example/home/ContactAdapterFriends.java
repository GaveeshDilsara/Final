package com.example.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.List;

public class ContactAdapterFriends extends ArrayAdapter<String> {

    private SharedPreferences emergencyContactsPrefs;
    private SharedPreferences newContactsPrefs;

    public ContactAdapterFriends(Context context, List<String> contacts) {
        super(context, 0, contacts);
        emergencyContactsPrefs = context.getSharedPreferences("EmergencyContacts", Context.MODE_PRIVATE);
        newContactsPrefs = context.getSharedPreferences("NewContacts", Context.MODE_PRIVATE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String contact = getItem(position);

        if (contact.contains("-------")) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_seperator, parent, false);
            TextView separatorTextView = convertView.findViewById(R.id.separatorTextView);
            separatorTextView.setText(contact);
            return convertView;
        }

        if (convertView == null || convertView.findViewById(R.id.contactTextView) == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_contact, parent, false);
        }

        String[] contactDetails = contact.split(": ");
        String contactType = contactDetails[0];
        String name = contactDetails[1];
        String contactNumber = contactDetails[2];

        TextView contactTextView = convertView.findViewById(R.id.contactTextView);
        Button editButton = convertView.findViewById(R.id.editButton);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        Button callButton = convertView.findViewById(R.id.callButton);

        contactTextView.setText(name + ": " + contactNumber);

        // Edit button functionality
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OpeningAddNumbers.class);
            intent.putExtra("name", name);
            intent.putExtra("contact", contactNumber);
            intent.putExtra("position", position);
            getContext().startActivity(intent);
        });

        // Call button functionality
        callButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contactNumber));
                getContext().startActivity(callIntent);
            } else {
                Toast.makeText(getContext(), "Call permission not granted", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete button functionality - only for new contacts
        if (contactType.equals("New")) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> {
                deleteContact(newContactsPrefs, position);
                remove(contact);
                notifyDataSetChanged();

                // Notify the fragment to update the contacts list
                if (getContext() instanceof FragmentUpdateListener) {
                    ((FragmentUpdateListener) getContext()).onContactDeleted();
                }
            });
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void deleteContact(SharedPreferences prefs, int position) {
        SharedPreferences.Editor editor = prefs.edit();
        // We have to adjust the position as SharedPreferences are 1-indexed in your case
        String nameKey = "name" + (position + 1);
        String contactKey = "contact" + (position + 1);

        editor.remove(nameKey);
        editor.remove(contactKey);
        editor.apply();

        // To keep the remaining contacts in proper order, we should shift any subsequent entries up.
        for (int i = position + 2; i <= 10; i++) {
            String nextName = prefs.getString("name" + i, "");
            String nextContact = prefs.getString("contact" + i, "");

            editor.putString("name" + (i - 1), nextName);
            editor.putString("contact" + (i - 1), nextContact);
            editor.remove("name" + i);
            editor.remove("contact" + i);
            editor.apply();
        }
    }
}
