package com.example.streamingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class CountryFragment extends Fragment {
    ImageView backIv;
    TextView resetTv, backTv;
    private RecyclerView recVCountries;
    private RecyclerView.LayoutManager countryLayoutManager;
    private List<CountryItems> countryItemsList;
    private CountryRecItemAdapter countryRecItemAdapter;

    private List<CountryItems> generateCountries() {
        List<CountryItems> itemsList = new ArrayList<>();
        itemsList.add(new CountryItems("All"));
        itemsList.add(new CountryItems("India"));
        itemsList.add(new CountryItems("USA"));
        itemsList.add(new CountryItems("Korea"));
        itemsList.add(new CountryItems("China"));

        return itemsList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_country, container, false);

        backIv = view.findViewById(R.id.backIv);
        resetTv = view.findViewById(R.id.resetTv);
        backTv = view.findViewById(R.id.backTv);
        recVCountries = view.findViewById(R.id.recVCountries);

        backIv.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        backTv.setOnClickListener(v -> {
            List<String> selectedCountries = countryRecItemAdapter.getSelectedItems();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("selectedCountries", new ArrayList<>(selectedCountries));

            // Set the result to FiltersFragment
            FiltersFragment filtersFragment = new FiltersFragment();
            filtersFragment.setArguments(bundle);

            // Replace current fragment with FiltersFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, filtersFragment)
                    .addToBackStack(null)
                    .commit();
        });

        countryItemsList = generateCountries();
        countryLayoutManager = new LinearLayoutManager(getContext());
        recVCountries.setLayoutManager(countryLayoutManager);
        countryRecItemAdapter = new CountryRecItemAdapter(getContext(), countryItemsList);
        recVCountries.setAdapter(countryRecItemAdapter);


        return view;
    }
}