package com.example.streamingapp.presentation.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.streamingapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FiltersFragment extends Fragment {
    private static final String PREF_IS_CATEGORIES_EXPANDED = "isCategoriesExpanded";
    private static final String PREF_IS_GENRES_EXPANDED = "isGenresExpanded";
    private static final String PREF_IS_COUNTRIES_EXPANDED = "isCountriesExpanded";
    private static final String PREF_IS_YEAR_EXPANDED = "isYearExpanded";
    private static final int PICK_GENRES_REQUEST_CODE = 100;
    private static final int PICK_VIDEO_TYPE_REQUEST_CODE = 101;
    private static final int PICK_COUNTRY_REQUEST_CODE = 102;
    private static final String PREF_SELECTED_SORT_OPTION = "selectedSortOption";
    private static final String PREFS_NAME = "FiltersPrefs";
    ImageView backIv;
    TextView resetTv, popularTv, newTv, ratingImdbTv, categoriesTv, genreTv, countryTv, yearTv, acceptFiltersTv;
    TextView[] sortOptions;
    private List<String> selectedCategories = new ArrayList<>();
    private List<String> selectedGenres = new ArrayList<>();
    private List<String> selectedCountries = new ArrayList<>();
    private boolean isCategoriesExpanded = false;
    private boolean isGenresExpanded = false;
    private boolean isCountriesExpanded = false;
    private boolean isYearExpanded = false;
    private int fromYear = 1990;
    private int toYear = 2100;
    private String selectedSortOption = "popularTv";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFiltersFromSharedPreferences();
        if (getArguments() != null) {
            ArrayList<String> updatedCountries = getArguments().getStringArrayList("selectedCountries");
            if (updatedCountries != null) {
                selectedCountries = updatedCountries;
            }
        }
        // Set up a listener to receive data from YearFragment
        getParentFragmentManager().setFragmentResultListener("yearRequestKey", this, (requestKey, result) -> {
            fromYear = result.getInt("fromYear");
            toYear = result.getInt("toYear");
            updateYearText(fromYear, toYear);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filters, container, false);

        // Initialize views
        backIv = view.findViewById(R.id.backIv);
        resetTv = view.findViewById(R.id.resetTv);
        popularTv = view.findViewById(R.id.popularTv);
        newTv = view.findViewById(R.id.newTv);
        ratingImdbTv = view.findViewById(R.id.imdbRatingTv);
        categoriesTv = view.findViewById(R.id.categoryTv);
        genreTv = view.findViewById(R.id.genreTv);
        countryTv = view.findViewById(R.id.countryTv);
        yearTv = view.findViewById(R.id.yearTv);
        acceptFiltersTv = view.findViewById(R.id.accptFiltersTv);

        // Set tags for sort options
        popularTv.setTag("popularTv");
        newTv.setTag("newTv");
        ratingImdbTv.setTag("ratingImdbTv");

        // Load saved data
        updateTextViews();

        // Set default filter selection
        setDefaultFilterSelection();

        // Set click listeners
        backIv.setOnClickListener(v -> navigateBack());
        categoriesTv.setOnClickListener(v -> handleCategoryClick());
        genreTv.setOnClickListener(v -> handleGenreClick());
        countryTv.setOnClickListener(v -> navigateToCountryFragment());
        yearTv.setOnClickListener(v -> handleYearClick());

        return view;
    }

    private void setDefaultFilterSelection() {
        sortOptions = new TextView[]{popularTv, newTv, ratingImdbTv};
        loadSelectedSortOption();

        for (TextView filterOption : sortOptions) {
            filterOption.setOnClickListener(v -> {
                setSelectedFilter((TextView) v);
                saveSelectedSortOption((TextView) v);
            });
        }
    }

    private void navigateBack() {
        saveFiltersToSharedPreferences();
        getParentFragmentManager().popBackStack();
    }

    private void handleCategoryClick() {
        saveFiltersToSharedPreferences();
        Intent intent = new Intent(getActivity(), PickVideoTypeActivity.class);
        intent.putStringArrayListExtra("selectedCategories", new ArrayList<>(selectedCategories));
        startActivityForResult(intent, PICK_VIDEO_TYPE_REQUEST_CODE);
    }

    private void handleGenreClick() {
        saveFiltersToSharedPreferences();
        Intent intent = new Intent(getActivity(), PickGenresActivity.class);
        intent.putStringArrayListExtra("selectedGenres", new ArrayList<>(selectedGenres));
        intent.putExtra("filters", "filters");
        startActivityForResult(intent, PICK_GENRES_REQUEST_CODE);
    }

    private void navigateToCountryFragment() {
        saveFiltersToSharedPreferences();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, new CountryFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void handleYearClick() {
        if (isYearExpanded) {
            navigateToYearFragment();
        } else {
            isYearExpanded = true;
            yearTv.setText(fromYear + "-" + toYear);
        }
    }

    private void navigateToYearFragment() {
        saveFiltersToSharedPreferences();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, new YearFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setSelectedFilter(TextView selectedFilter) {
        for (TextView filterOption : sortOptions) {
            filterOption.setBackgroundResource(filterOption == selectedFilter ? R.drawable.lgtransparentbluestroke_bg : R.drawable.lgblackcircle_bg);
        }
    }

    private void updateTextViews() {
        updateTextView(categoriesTv, selectedCategories, isCategoriesExpanded);
        updateTextView(genreTv, selectedGenres, isGenresExpanded);
        updateTextView(countryTv, selectedCountries, isCountriesExpanded);
        updateYearText(fromYear, toYear);
    }

    private void updateTextView(TextView textView, List<String> items, boolean isExpanded) {
        if (items != null && !items.isEmpty()) {
            if (isExpanded) {
                textView.setText(TextUtils.join(", ", items));
            } else {
                String displayText = items.get(0) + (items.size() > 1 ? " +" + (items.size() - 1) + " more" : "");
                textView.setText(displayText);
            }
        } else {
            textView.setText("All");
        }
    }

    private void updateYearText(int fromYear, int toYear) {
        String fromYearStr = String.valueOf(fromYear);
        String toYearStr = String.valueOf(toYear);

        Log.d("FiltersFragment", "Updating year text with fromYear: " + fromYearStr + " and toYear: " + toYearStr);

        String shortFromYear = fromYearStr.length() >= 2 ? fromYearStr.substring(fromYearStr.length() - 2) : fromYearStr;
        String shortToYear = toYearStr.length() >= 2 ? toYearStr.substring(toYearStr.length() - 2) : toYearStr;

        yearTv.setText(shortFromYear + "-" + shortToYear);
    }

    private void saveFiltersToSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FiltersPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putStringSet("selectedCategories", new HashSet<>(selectedCategories));
        editor.putStringSet("selectedGenres", new HashSet<>(selectedGenres));
        editor.putStringSet("selectedCountries", new HashSet<>(selectedCountries));
        editor.putInt("fromYear", fromYear);
        editor.putInt("toYear", toYear);
        editor.putBoolean(PREF_IS_CATEGORIES_EXPANDED, isCategoriesExpanded);
        editor.putBoolean(PREF_IS_GENRES_EXPANDED, isGenresExpanded);
        editor.putBoolean(PREF_IS_COUNTRIES_EXPANDED, isCountriesExpanded);
        editor.putBoolean(PREF_IS_YEAR_EXPANDED, isYearExpanded);

        editor.apply();
    }

    private void loadFiltersFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FiltersPrefs", Context.MODE_PRIVATE);

        selectedCategories = new ArrayList<>(sharedPreferences.getStringSet("selectedCategories", new HashSet<>()));
        selectedGenres = new ArrayList<>(sharedPreferences.getStringSet("selectedGenres", new HashSet<>()));
        selectedCountries = new ArrayList<>(sharedPreferences.getStringSet("selectedCountries", new HashSet<>()));
        fromYear = sharedPreferences.getInt("fromYear", 1990);
        toYear = sharedPreferences.getInt("toYear", 2100);
        isCategoriesExpanded = sharedPreferences.getBoolean(PREF_IS_CATEGORIES_EXPANDED, false);
        isGenresExpanded = sharedPreferences.getBoolean(PREF_IS_GENRES_EXPANDED, false);
        isCountriesExpanded = sharedPreferences.getBoolean(PREF_IS_COUNTRIES_EXPANDED, false);
        isYearExpanded = sharedPreferences.getBoolean(PREF_IS_YEAR_EXPANDED, false);
    }

    private void saveSelectedSortOption(TextView selectedSortOption) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Object tag = selectedSortOption.getTag();
        if (tag != null) {
            editor.putString(PREF_SELECTED_SORT_OPTION, tag.toString());
            editor.apply();
        } else {
            Log.e("FiltersFragment", "Error: selectedSortOption tag is null");
        }
    }

    private void loadSelectedSortOption() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        selectedSortOption = sharedPreferences.getString(PREF_SELECTED_SORT_OPTION, "popularTv");

        for (TextView sortOption : sortOptions) {
            if (selectedSortOption.equals(sortOption.getTag())) {
                setSelectedFilter(sortOption);
                break;
            }
        }
    }

    private TextView getSortOptionTextView(String sortOptionTag) {
        switch (sortOptionTag) {
            case "newTv":
                return newTv;
            case "ratingImdbTv":
                return ratingImdbTv;
            case "popularTv":
            default:
                return popularTv;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_GENRES_REQUEST_CODE) {
               if (data != null) {
                    selectedGenres = data.getStringArrayListExtra("selectedGenres");
                    isGenresExpanded = true;
                    updateTextView(genreTv, selectedGenres, isGenresExpanded);
                }
            }
        }
        if (requestCode == PICK_VIDEO_TYPE_REQUEST_CODE) {
            if (data != null) {
                selectedCategories = data.getStringArrayListExtra("selectedCategories");
                isCategoriesExpanded = true;
                updateTextView(categoriesTv, selectedCategories, isCategoriesExpanded);
            }
        }
        if (requestCode == PICK_COUNTRY_REQUEST_CODE) {
            if (data != null) {
                selectedCountries = data.getStringArrayListExtra("selectedCountries");
                isCountriesExpanded = true;
                updateTextView(countryTv, selectedCountries, isCountriesExpanded);
            }

        }
    }
}
