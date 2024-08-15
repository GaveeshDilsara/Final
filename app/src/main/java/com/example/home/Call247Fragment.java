package com.example.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Call247Fragment extends Fragment {

    private RecyclerView contactRecyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call24, container, false);

        contactRecyclerView = view.findViewById(R.id.contact_list);
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the contact list with images
        contactList = new ArrayList<>();
        contactList.add(new Contact("Police", "119", R.drawable.ic_police));
        contactList.add(new Contact("Ambulance", "1990", R.drawable.ic_ambulance));
        contactList.add(new Contact("Fire Department", "110", R.drawable.ic_fire));
        contactList.add(new Contact("Report Crimes", "011-2691500", R.drawable.ic_crime));
        contactList.add(new Contact("Bomb Disposal", "011-2433335", R.drawable.ic_bomb));

        // Set up the adapter
        contactAdapter = new ContactAdapter(getActivity(), contactList);
        contactRecyclerView.setAdapter(contactAdapter);

        return view;
    }
}
