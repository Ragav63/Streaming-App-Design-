package com.example.streamingapp.presentation.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.data.model.PickGenreTypeRecItem;
import com.example.streamingapp.data.model.PickVideoTypeRecItem;
import com.example.streamingapp.databinding.FragmentFiltersBinding;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FiltersFragment extends Fragment {
    private FragmentFiltersBinding binding;

    private List<String> selectedCategories = new ArrayList<>();
    private List<String> selectedGenres = new ArrayList<>();
    private List<String> selectedCountries = new ArrayList<>();

    private boolean isCategoriesExpanded = false;
    private boolean isGenresExpanded = false;
    private boolean isCountriesExpanded = false;
    private boolean isYearExpanded = false;

    private int fromYear = 1990;
    private int toYear = 2100;

    private TextView[] sortOptions;

    private LocalManager localManager;

    private StreamingViewModel vm;

    // Dynamic lists from ViewModel
    private List<String> genreNames = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    private List<PickGenreTypeRecItem> pickGenreTypeItemList;
    private List<PickVideoTypeRecItem> pickVideoTypeItemList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFiltersBinding.inflate(inflater, container, false);
        localManager = new LocalManager(requireContext());
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        setupObservers();
        vm.loadGenres();
        vm.loadVideoTypeItems();

        loadFilters();
        setupSortOptions();
        setupClickListeners();
        updateAllTextViews();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFilters(); // Reload in case other fragments changed data
        updateAllTextViews();
    }

    // ---------------------------------------------
    // SETUP OBSERVERS
    // ---------------------------------------------
    private void setupObservers() {
        // Observe genres
        vm.getGenresLiveData().observe(getViewLifecycleOwner(), genreItems -> {
            if (genreItems != null) {
                genreNames.clear();
                for (PickGenreTypeRecItem item : genreItems) {
                    genreNames.add(item.getItemTitle());
                }
                Log.d("FiltersFragment", "Loaded genres: " + genreNames);

                // Reload filters to update genre names based on new genre list
                reloadGenreFilters();
            }
        });

        // Observe video types
        vm.getVideoTypeLiveData().observe(getViewLifecycleOwner(), videoTypeItems -> {
            if (videoTypeItems != null) {
                categoryNames.clear();
                for (PickVideoTypeRecItem item : videoTypeItems) {
                    categoryNames.add(item.getItemTitle());
                }
                Log.d("FiltersFragment", "Loaded categories: " + categoryNames);

                // Reload filters to update category names based on new category list
                reloadCategoryFilters();
            }
        });
    }

    // ---------------------------------------------
    // RELOAD FILTERS WITH UPDATED LISTS
    // ---------------------------------------------
    private void reloadGenreFilters() {
        // Reload genre positions and convert to names using updated genre list
        Set<Integer> genrePositions = localManager.loadGenreSelection();
        selectedGenres = convertGenrePositionsToNames(genrePositions);
        updateAllTextViews();
    }

    private void reloadCategoryFilters() {
        // Reload category positions and convert to names using updated category list
        Set<Integer> categoryPositions = localManager.loadCategoryPositions();
        selectedCategories = convertCategoryPositionsToNames(categoryPositions);
        updateAllTextViews();
    }

    // ---------------------------------------------
    // SAVE FILTERS
    // ---------------------------------------------
    private void saveFilters() {
        Log.d("FiltersFragment", "Saving filters - Categories: " + selectedCategories.size() +
                ", Genres: " + selectedGenres.size() +
                ", Countries: " + selectedCountries.size() +
                ", Year: " + fromYear + "-" + toYear);

        // Convert names back to positions for saving
        Set<Integer> categoryPositions = convertCategoryNamesToPositions(selectedCategories);
        Set<Integer> genrePositions = convertGenreNamesToPositions(selectedGenres);

        // Use dedicated methods for each filter type
        localManager.saveCategoryPositions(categoryPositions); // Save positions, not names
        localManager.saveGenreSelection(genrePositions); // Save positions, not names
        localManager.saveCountrySelection(new HashSet<>(selectedCountries));
        localManager.saveYearRange(fromYear, toYear);

        localManager.saveBoolean("catExp", isCategoriesExpanded);
        localManager.saveBoolean("genExp", isGenresExpanded);
        localManager.saveBoolean("cntExp", isCountriesExpanded);
        localManager.saveBoolean("yearExp", isYearExpanded);
    }

    // ---------------------------------------------
    // LOAD FILTERS
    // ---------------------------------------------
    private void loadFilters() {
        // Load positions and convert to names
        Set<Integer> categoryPositions = localManager.loadCategoryPositions();
        selectedCategories = convertCategoryPositionsToNames(categoryPositions);

        Set<Integer> genrePositions = localManager.loadGenreSelection();
        selectedGenres = convertGenrePositionsToNames(genrePositions);

        selectedCountries = new ArrayList<>(localManager.loadCountrySelection());

        // Use consistent year loading method
        fromYear = localManager.loadFromYear(1990);
        toYear = localManager.loadToYear(2100);

        isCategoriesExpanded = localManager.loadBoolean("catExp", false);
        isGenresExpanded = localManager.loadBoolean("genExp", false);
        isCountriesExpanded = localManager.loadBoolean("cntExp", false);
        isYearExpanded = localManager.loadBoolean("yearExp", false);

        Log.d("FiltersFragment", "Loaded filters - Categories: " + selectedCategories.size() +
                ", Genres: " + selectedGenres.size() +
                ", Countries: " + selectedCountries.size() +
                ", Year: " + fromYear + "-" + toYear);
    }

    // ---------------------------------------------
    // CONVERSION METHODS FOR CATEGORIES
    // ---------------------------------------------
    private List<String> convertCategoryPositionsToNames(Set<Integer> positions) {
        List<String> categoryNamesList = new ArrayList<>();
        for (Integer position : positions) {
            if (position >= 0 && position < categoryNames.size()) {
                categoryNamesList.add(categoryNames.get(position));
            }
        }
        return categoryNamesList;
    }

    private Set<Integer> convertCategoryNamesToPositions(List<String> categoryNamesList) {
        Set<Integer> positions = new HashSet<>();
        for (String categoryName : categoryNamesList) {
            for (int i = 0; i < categoryNames.size(); i++) {
                if (categoryNames.get(i).equals(categoryName)) {
                    positions.add(i);
                    break;
                }
            }
        }
        return positions;
    }

    // ---------------------------------------------
    // CONVERSION METHODS FOR GENRES
    // ---------------------------------------------
    private List<String> convertGenrePositionsToNames(Set<Integer> positions) {
        List<String> genreNamesList = new ArrayList<>();
        for (Integer position : positions) {
            if (position >= 0 && position < genreNames.size()) {
                genreNamesList.add(genreNames.get(position));
            }
        }
        return genreNamesList;
    }

    private Set<Integer> convertGenreNamesToPositions(List<String> genreNamesList) {
        Set<Integer> positions = new HashSet<>();
        for (String genreName : genreNamesList) {
            for (int i = 0; i < genreNames.size(); i++) {
                if (genreNames.get(i).equals(genreName)) {
                    positions.add(i);
                    break;
                }
            }
        }
        return positions;
    }

    // ---------------------------------------------
    // UPDATE UI TEXT
    // ---------------------------------------------
    private void updateAllTextViews() {
        Log.d("FiltersFragment", "Updating UI - Categories: " + selectedCategories.size() +
                ", Genres: " + selectedGenres.size() +
                ", Countries: " + selectedCountries.size() +
                ", Year: " + fromYear + "-" + toYear);

        updateTextView(binding.categoryTv, selectedCategories, isCategoriesExpanded);
        updateTextView(binding.genreTv, selectedGenres, isGenresExpanded);
        updateTextView(binding.countryTv, selectedCountries, isCountriesExpanded);
        updateYearText();
    }

    private void updateTextView(TextView tv, List<String> items, boolean expanded) {
        Log.d("FiltersFragment", "updateTextView: " + tv.getContentDescription() +
                ", items: " + items + ", expanded: " + expanded);

        if (items == null || items.isEmpty()) {
            tv.setText("All");
            return;
        }

        try {
            if (expanded) {
                tv.setText(TextUtils.join(", ", items));
            } else {
                String first = items.get(0);
                int extra = items.size() - 1;
                tv.setText(extra > 0 ? first + " +" + extra + " more" : first);
            }
        } catch (Exception e) {
            Log.e("FiltersFragment", "Error updating text view", e);
            tv.setText("All");
        }
    }

    private void updateYearText() {
        String fromText = fromYear == 1990 ? "90" : String.valueOf(fromYear);
        String toText = toYear == 2100 ? "now" : String.valueOf(toYear);
        binding.yearTv.setText(fromText + " - " + toText);

        Log.d("FiltersFragment", "Year text updated: " + fromText + " - " + toText);
    }

    // ---------------------------------------------
    // SORT OPTION
    // ---------------------------------------------
    private void setupSortOptions() {
        sortOptions = new TextView[]{
                binding.popularTv,
                binding.newTv,
                binding.imdbRatingTv
        };

        binding.popularTv.setTag("popularTv");
        binding.newTv.setTag("newTv");
        binding.imdbRatingTv.setTag("ratingImdbTv");

        String selected = localManager.loadSortOption();
        for (TextView tv : sortOptions) {
            if (tv.getTag().equals(selected)) {
                setSelectedSort(tv);
            }
        }

        for (TextView tv : sortOptions) {
            tv.setOnClickListener(v -> {
                setSelectedSort(tv);
                localManager.saveSortOption(tv.getTag().toString());
            });
        }
    }

    private void setSelectedSort(TextView selected) {
        for (TextView tv : sortOptions) {
            tv.setBackgroundResource(
                    tv == selected
                            ? R.drawable.lgtransparentbluestroke_bg
                            : R.drawable.lgblackcircle_bg
            );
        }
    }

    // ---------------------------------------------
    // RESET
    // ---------------------------------------------
    private void resetFilters() {
        selectedCategories.clear();
        selectedGenres.clear();
        selectedCountries.clear();

        fromYear = 1990;
        toYear = 2100;

        isCategoriesExpanded = false;
        isGenresExpanded = false;
        isCountriesExpanded = false;
        isYearExpanded = false;

        // Save the reset state using dedicated methods
        localManager.saveCategoryPositions(new HashSet<>()); // Save empty positions
        localManager.saveGenreSelection(new HashSet<>()); // Save empty positions
        localManager.saveCountrySelection(new HashSet<>());
        localManager.saveYearRange(fromYear, toYear);

        updateAllTextViews();

        Log.d("FiltersFragment", "Filters reset to default");
    }

    // ------------------------------------------------------------
    // CLICK LISTENERS
    // ------------------------------------------------------------
    private void setupClickListeners() {

        binding.backIv.setOnClickListener(v -> {
            saveFilters();
            NavHostFragment.findNavController(this).popBackStack();
        });

        binding.categoryTv.setOnClickListener(v -> openCategoriesScreen());
        binding.genreTv.setOnClickListener(v -> openGenresScreen());
        binding.countryTv.setOnClickListener(v -> openCountryScreen());
        binding.yearTv.setOnClickListener(v -> openYearScreen());

        binding.resetTv.setOnClickListener(v -> resetFilters());
        binding.accptFiltersTv.setOnClickListener(v -> {
            saveFilters();
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    // ------------------------------------------------------------
    // NAVIGATION (NO SAFE ARGS)
    // ------------------------------------------------------------
    private void openCategoriesScreen() {
        saveFilters();
        Bundle bundle = new Bundle();
        bundle.putString("filters", "filters");

        NavHostFragment.findNavController(this)
                .navigate(R.id.pickVideoTypeActivity, bundle);
    }

    private void openGenresScreen() {
        saveFilters();
        Bundle bundle = new Bundle();
        bundle.putString("filters", "filters");

        NavHostFragment.findNavController(this)
                .navigate(R.id.pickGenresActivity, bundle);
    }

    private void openCountryScreen() {
        saveFilters();
        NavHostFragment.findNavController(this)
                .navigate(R.id.countryFragment);
    }

    private void openYearScreen() {
        saveFilters();
        NavHostFragment.findNavController(this)
                .navigate(R.id.yearFragment);
    }
}
