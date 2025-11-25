package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.streamingapp.data.model.CountryItems;
import com.example.streamingapp.presentation.adapter.CountryRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class CountryFragment extends Fragment {
    ImageView backIv;
    TextView resetTv, backTv;
    private RecyclerView recVCountries;
    private RecyclerView.LayoutManager countryLayoutManager;
    private List<CountryItems> countryItemsList;
    private CountryRecItemAdapter countryRecItemAdapter;

    private StreamingViewModel vm;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_country, container, false);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

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
                    .replace(R.id.nav_host_fragment, filtersFragment)
                    .addToBackStack(null)
                    .commit();
        });

        countryItemsList = vm.getCountries();
        countryLayoutManager = new LinearLayoutManager(getContext());
        recVCountries.setLayoutManager(countryLayoutManager);
        countryRecItemAdapter = new CountryRecItemAdapter(getContext(), countryItemsList);
        recVCountries.setAdapter(countryRecItemAdapter);


        return view;
    }
}