package com.example.streamingapp.presentation.view;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.databinding.FragmentYearBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class YearFragment extends Fragment {
    private FragmentYearBinding binding;

    private List<String> years;
    private int startYear = 1980;
    private int endYear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentYearBinding.inflate(inflater, container, false);

        setupUI();
        initSpinners();

        return binding.getRoot();
    }

    private void setupUI() {

        binding.backIv.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        endYear = Calendar.getInstance().get(Calendar.YEAR);

        years = new ArrayList<>();
        for (int y = startYear; y <= endYear; y++) years.add(String.valueOf(y));

        binding.startYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                updateToYearSpinner(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.resetTv.setOnClickListener(v -> resetSpinners());

        binding.backTv.setOnClickListener(v -> {

            int fromYear = Integer.parseInt(binding.startYear.getSelectedItem().toString());
            int toYear = Integer.parseInt(binding.endYear.getSelectedItem().toString());

            LocalManager.saveYearRange(fromYear, toYear);

            Bundle result = new Bundle();
            result.putInt("fromYear", fromYear);
            result.putInt("toYear", toYear);

            getParentFragmentManager().setFragmentResult("yearRequestKey", result);
            getParentFragmentManager().popBackStack();
        });
    }

    private void initSpinners() {

        int savedFromYear = LocalManager.loadFromYear(startYear);
        int fromIndex = years.indexOf(String.valueOf(savedFromYear));

        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                years
        );
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.startYear.setAdapter(fromAdapter);
        binding.startYear.setSelection(fromIndex);

        updateToYearSpinner(fromIndex);
    }

    private void updateToYearSpinner(int fromIndex) {

        List<String> toYearList = new ArrayList<>(years.subList(fromIndex, years.size()));

        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                toYearList
        );
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.endYear.setAdapter(toAdapter);

        int savedToYear = LocalManager.loadToYear(endYear);
        int toIndex = toYearList.indexOf(String.valueOf(savedToYear));

        if (toIndex != -1) binding.endYear.setSelection(toIndex);
    }

    private void resetSpinners() {

        int startIndex = years.indexOf(String.valueOf(startYear));
        int endIndex = years.indexOf(String.valueOf(endYear));

        binding.startYear.setSelection(startIndex);
        updateToYearSpinner(startIndex);

        binding.endYear.setSelection(endIndex - startIndex);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}