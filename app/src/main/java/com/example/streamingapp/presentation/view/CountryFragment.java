package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.streamingapp.data.model.CountryItems;
import com.example.streamingapp.databinding.FragmentCountryBinding;
import com.example.streamingapp.presentation.adapter.CountryRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class CountryFragment extends Fragment {
    private FragmentCountryBinding binding;
    private StreamingViewModel vm;

    private CountryRecItemAdapter countryRecItemAdapter;
    private List<CountryItems> countryItemsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCountryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        setupUi();
        setupRecycler();

        return view;
    }

    private void setupUi() {

        binding.backIv.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigateUp();
        });

        binding.backTv.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigateUp();
        });
    }

    private void setupRecycler() {


        binding.recVCountries.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        countryRecItemAdapter = new CountryRecItemAdapter(requireContext(), selectedCountries -> {
            // Handle selected country changes here
            Log.d("CountrySelection", "Selected countries: " + selectedCountries);
        });

        binding.recVCountries.setAdapter(countryRecItemAdapter);
        vm.loadCountries();
        vm.getCountryLiveData().observe(getViewLifecycleOwner(), items -> {
            countryItemsList = items;
            countryRecItemAdapter.submitList(items);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;   // prevent memory leak
    }
}