package com.example.streamingapp;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class YearFragment extends Fragment {
    ImageView backIv;
    TextView resetTv, backTv;
    Spinner fromYearSpinner, toYearSpinner;
    private List<String> years;
    private int startYear = 1980;
    private int endYear;
    private static final String PREFS_NAME = "year_prefs";
    private static final String FROM_YEAR_KEY = "from_year";
    private static final String TO_YEAR_KEY = "to_year";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_year, container, false);

        backIv = view.findViewById(R.id.backIv);
        resetTv = view.findViewById(R.id.resetTv);
        backTv = view.findViewById(R.id.backTv);
        fromYearSpinner = view.findViewById(R.id.startYear);
        toYearSpinner = view.findViewById(R.id.endYear);

        backIv.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        // Initialize endYear with the current year
        endYear = Calendar.getInstance().get(Calendar.YEAR);

        years = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            years.add(String.valueOf(year));
        }

        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, years);
        fromAdapter.setDropDownViewResource(R.layout.spinner_item);
        fromYearSpinner.setAdapter(fromAdapter);

        fromYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateToYearSpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        resetTv.setOnClickListener(v -> resetSpinners());

        backTv.setOnClickListener(v -> {
            int fromYear = Integer.parseInt((String) fromYearSpinner.getSelectedItem());
            int toYear = Integer.parseInt((String) toYearSpinner.getSelectedItem());

            // Save the selected years
            SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(FROM_YEAR_KEY, fromYear);
            editor.putInt(TO_YEAR_KEY, toYear);
            editor.apply();

            // Use setFragmentResult to pass data back to the FiltersFragment
            Bundle result = new Bundle();
            result.putInt("fromYear", fromYear);
            result.putInt("toYear", toYear);
            getParentFragmentManager().setFragmentResult("yearRequestKey", result);

            getParentFragmentManager().popBackStack();
        });

        // Initialize the spinners with saved values
        initializeSpinners();

        // Initialize the toYearSpinner with the default fromYearSpinner position
//        updateToYearSpinner(fromYearSpinner.getSelectedItemPosition());

        return view;
    }

    private void updateToYearSpinner(int fromYearPosition) {
        List<String> toYearList = new ArrayList<>(years.subList(fromYearPosition, years.size()));
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, toYearList);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toYearSpinner.setAdapter(toAdapter);

        // Restore the saved toYear selection if available
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedToYear = prefs.getInt(TO_YEAR_KEY, endYear);
        int toYearIndex = toYearList.indexOf(String.valueOf(savedToYear));
        if (toYearIndex != -1) {
            toYearSpinner.setSelection(toYearIndex);
        }
    }

    private void resetSpinners() {
        int startYearPosition = years.indexOf(String.valueOf(startYear));
        int endYearPosition = years.indexOf(String.valueOf(endYear));
        fromYearSpinner.setSelection(startYearPosition);
        updateToYearSpinner(startYearPosition);
        toYearSpinner.setSelection(endYearPosition - startYearPosition); // Adjust for the new list size
    }

    private void initializeSpinners() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedFromYear = prefs.getInt(FROM_YEAR_KEY, startYear);
        int fromYearIndex = years.indexOf(String.valueOf(savedFromYear));

        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, years);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromYearSpinner.setAdapter(fromAdapter);
        fromYearSpinner.setSelection(fromYearIndex);

        updateToYearSpinner(fromYearIndex);
    }
}