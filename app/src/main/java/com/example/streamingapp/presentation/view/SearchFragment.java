package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.FilterState;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.data.model.FilterState.Mode;
import com.example.streamingapp.data.model.TvChannel;
import com.example.streamingapp.databinding.FiltersBottomSheetBinding;
import com.example.streamingapp.databinding.FragmentSearchBinding;
import com.example.streamingapp.presentation.adapter.CastRecItemAdapter;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.presentation.adapter.GenreFilterAdapter;
import com.example.streamingapp.presentation.adapter.NowOnTvItemAdapter;
import com.example.streamingapp.presentation.adapter.PopularMovieRecItemAdapter;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.presentation.adapter.PopularSeriesRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.adapter.SelectedFilterAdapter;
import com.example.streamingapp.presentation.viewmodel.FiltersViewModel;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;

    private StreamingViewModel vm;
    private FiltersViewModel filtersViewModel;

    private PopularMovieRecItemAdapter movieAdapter;
    private PopularSeriesRecItemAdapter seriesAdapter;
    private CastRecItemAdapter castAdapter;
    private NowOnTvItemAdapter tvItemAdapter;
    private GenreFilterAdapter genreFilterAdapter;
    private SelectedFilterAdapter selectedFilterAdapter;

    private List<MovieItems> movieItemsList = new ArrayList<>();
    private List<SeriesItems> seriesItemsList = new ArrayList<>();
    private List<CastItems> castItemsList = new ArrayList<>();
    private List<TvChannel> tvItemList = new ArrayList<>();
    private List<PickItem> genreItemList = new ArrayList<>();
    private List<String> selectedGenres = new ArrayList<>();

    private BottomSheetBehavior<View> bottomSheetBehavior;

    private LinearLayoutManager horizontalMovies, horizontalSeries, horizontalCast, horizontalTv,horizontalGenres, horizontalFilters;

    private String currentSearchQuery = "";
    private FilterState.Mode selectedMode = null;
    private String selectedYear = null;
    private String selectedCountry = null;
    private String selectedSort = null;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);
        filtersViewModel = new ViewModelProvider(this)
                .get(FiltersViewModel.class);


        binding.tvCountFilter.setVisibility(View.GONE);
        binding.tvCountFilter.setText("0");


        setupBottomSheet();   // 👈 ADD THIS
        setupAdapters();   // 👈 FIRST
        setupData();
        setupSearch();
        setupButtons();
    }

    // -----------------------
    // BOTTOM SHEET & FILTERS
    // -----------------------
    private void setupBottomSheet() {
        View bottomSheet = binding.filtersBottomSheet.getRoot();
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        FiltersBottomSheetBinding b = FiltersBottomSheetBinding.bind(binding.filtersBottomSheet.bottomSheetRoot);

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        bottomSheetBehavior.setPeekHeight((int) (screenHeight * 0.6f), true);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        setupModeListeners(b);

        b.llReleaseYear.setOnClickListener(v -> showReleaseYearDialog());
        b.llCountry.setOnClickListener(v -> showCountryDialog());
        b.llSortBy.setOnClickListener(v -> showSortByDialog());

        b.btnApply.setOnClickListener(v -> applyFilters());
        b.btnReset.setOnClickListener(v -> {
            filtersViewModel.resetFilters();
            genreFilterAdapter.clearSelection();
            selectedFilterAdapter.submitList(new ArrayList<>());
            binding.tvCountFilter.setVisibility(View.GONE);
        });
    }

    private void setupModeListeners(FiltersBottomSheetBinding b) {
        b.btnModeRow1.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            b.btnModeRow2.clearChecked();
            setModeFromButton(group.findViewById(checkedId));
        });

        b.btnModeRow2.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            b.btnModeRow1.clearChecked();
            setModeFromButton(group.findViewById(checkedId));
        });
    }

    private void setModeFromButton(MaterialButton btn) {
        if (btn == null) return;
        switch (btn.getText().toString()) {
            case "Movies": selectedMode = Mode.MOVIES; break;
            case "Series": selectedMode = Mode.SERIES; break;
            case "TV":     selectedMode = Mode.TV; break;
            case "Anime":  selectedMode = Mode.ANIME; break;
        }
        applyFilters();
    }

    private FilterState.Mode mapMode(FilterState.Mode mode) {
        if (mode == null) return null;
        switch (mode) {
            case MOVIES: return FilterState.Mode.MOVIES;
            case SERIES: return FilterState.Mode.SERIES;
            case TV:     return FilterState.Mode.TV;
            case ANIME:  return FilterState.Mode.ANIME;
        }
        return null;
    }

    private void applyFilters() {
        FilterState current = filtersViewModel.getFilterState().getValue();
        if (current == null) current = FilterState.empty();

        FilterState newState = current.copy(
                mapMode(selectedMode),
                selectedYear,
                selectedCountry,
                selectedSort,
                new ArrayList<>(selectedGenres),
                currentSearchQuery
        );

        filtersViewModel.updateFilter(newState);

        selectedFilterAdapter.submitList(newState.asChipList());
        int count = newState.count();
        binding.tvCountFilter.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        binding.tvCountFilter.setText(String.valueOf(count));

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    // -----------------------
    // DATA OBSERVERS
    // -----------------------
    private void setupData() {
        vm.getMovieLiveData().observe(getViewLifecycleOwner(), movies ->
                filtersViewModel.setMovies(movies != null ? movies : new ArrayList<>()));

        vm.getSeriesLiveData().observe(getViewLifecycleOwner(), series ->
                filtersViewModel.setSeries(series != null ? series : new ArrayList<>()));

        vm.getCastLiveData().observe(getViewLifecycleOwner(), cast ->
                filtersViewModel.setCast(cast != null ? cast : new ArrayList<>()));

        vm.getTvLiveData().observe(getViewLifecycleOwner(), tv -> {
                filtersViewModel.setTv(tv != null ? tv : new ArrayList<>());
        });

        vm.getGenresLiveData().observe(getViewLifecycleOwner(), items -> {
            genreItemList = items;
            genreFilterAdapter.submitList(items);
        });

        filtersViewModel.getFilteredMovies().observe(getViewLifecycleOwner(), movies -> {
            movieAdapter.submitList(movies);
            updateSectionVisibility(
                    movies,
                    filtersViewModel.getFilteredSeries().getValue(),
                    filtersViewModel.getFilteredCast().getValue(),
                    filtersViewModel.getFilteredTv().getValue()
            );
        });

        filtersViewModel.getFilteredSeries().observe(getViewLifecycleOwner(), series -> {
            seriesAdapter.submitList(series);
            updateSectionVisibility(
                    filtersViewModel.getFilteredMovies().getValue(),
                    series,
                    filtersViewModel.getFilteredCast().getValue(),
                    filtersViewModel.getFilteredTv().getValue()
            );
        });

        filtersViewModel.getFilteredCast().observe(getViewLifecycleOwner(), cast -> {
            castAdapter.submitList(cast);
            updateSectionVisibility(
                    filtersViewModel.getFilteredMovies().getValue(),
                    filtersViewModel.getFilteredSeries().getValue(),
                    cast,
                    filtersViewModel.getFilteredTv().getValue()
            );
        });


        filtersViewModel.getFilteredTv().observe(getViewLifecycleOwner(), tvChannels -> {
            tvItemAdapter.submitList(tvChannels);
            updateSectionVisibility(
                    filtersViewModel.getFilteredMovies().getValue(),
                    filtersViewModel.getFilteredSeries().getValue(),
                    filtersViewModel.getFilteredCast().getValue(),
                    tvChannels
            );
        });

        filtersViewModel.hasAnyResults().observe(getViewLifecycleOwner(), hasAny -> {
            if (hasAny) hideNoItemAnimation();
            else showNoItemAnimation();
        });




        vm.loadMovies();
        vm.loadSeries();
        vm.loadCast();
        vm.loadGenres();
        vm.loadTvItems();
    }

    private void updateSectionVisibility(
            List<?> movies,
            List<?> series,
            List<?> cast,
            List<?> tv
    ) {
        setSectionVisibility(
                !movies.isEmpty() && shouldShowMovies(),
                binding.recVPopularMovies,
                binding.Recommend
        );

        setSectionVisibility(
                !series.isEmpty() && shouldShowSeries(),
                binding.recVPopularSeries,
                binding.Series
        );

        setSectionVisibility(
                !tv.isEmpty() && shouldShowTv(),
                binding.recVTvChannel,
                binding.TvChannel
        );

        setSectionVisibility(
                !cast.isEmpty(),
                binding.recVCast,
                binding.Actors
        );
    }



    private void setSectionVisibility(boolean show, View list, View title) {
        int visibility = show ? View.VISIBLE : View.GONE;
        list.setVisibility(visibility);
        title.setVisibility(visibility);
    }

    private boolean shouldShowMovies() {
        if (!currentSearchQuery.isEmpty()) return true;
        return selectedMode == null || selectedMode == Mode.MOVIES || selectedMode == Mode.ANIME;
    }

    private boolean shouldShowSeries() {
        if (!currentSearchQuery.isEmpty()) return true;
        return selectedMode == null || selectedMode == Mode.SERIES;
    }

    private boolean shouldShowTv() {
        if (!currentSearchQuery.isEmpty()) return true;
        return selectedMode == null || selectedMode == Mode.TV;
    }


    // -----------------------
    // ADAPTERS & LAYOUTS
    // -----------------------
    private void setupAdapters() {
        // Layout managers

        horizontalMovies = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalSeries = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalTv = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalCast = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalGenres = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalFilters = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        // Adapters
        movieAdapter = new PopularMovieRecItemAdapter(requireContext(), movieItemsList,
                (movie, pos) -> {
                    Bundle b = new Bundle();
                    b.putParcelable("movieItem", movie);
                    Navigation.findNavController(requireView()).navigate(R.id.movieScreenActivity, b);
                });

        seriesAdapter = new PopularSeriesRecItemAdapter(requireContext(), seriesItemsList,
                (series, pos) -> {
                    Bundle b = new Bundle();
                    b.putParcelable("seriesItem", series);
                    Navigation.findNavController(requireView()).navigate(R.id.seriesScreenActivity, b);
                });

        tvItemAdapter = new NowOnTvItemAdapter(getContext(), new ArrayList<>(),
                (item, pos) -> {

                });

        castAdapter = new CastRecItemAdapter(requireContext(),
                cast -> {
            Bundle b = new Bundle();
            b.putBoolean("isMovie",true);
            b.putParcelable("castItem", cast);
            Navigation.findNavController(requireView()).navigate(R.id.actorScreenActivity, b);
        });

        genreFilterAdapter = new GenreFilterAdapter(requireContext(), new ArrayList<>(),
                selectedPositions -> {
                    selectedGenres.clear();
                    for (int pos : selectedPositions) {
                        selectedGenres.add(genreItemList.get(pos).getItemTitle());
                    }
                });

        selectedFilterAdapter = new SelectedFilterAdapter(requireContext());
        selectedFilterAdapter.setOnFilterRemovedListener(removedFilter -> {
            removeFromFilterLists(removedFilter);
            applyFilters();
        });

        // Attach adapters
        binding.recVPopularMovies.setAdapter(movieAdapter);
        binding.recVPopularSeries.setAdapter(seriesAdapter);
        binding.recVTvChannel.setAdapter(tvItemAdapter);
        binding.recVCast.setAdapter(castAdapter);
        binding.filtersBottomSheet.recVGenres.setAdapter(genreFilterAdapter);
        binding.recVSelectedFilters.setAdapter(selectedFilterAdapter);

        // Set layout managers
        binding.recVPopularMovies.setLayoutManager(horizontalMovies);
        binding.recVPopularSeries.setLayoutManager(horizontalSeries);
        binding.recVTvChannel.setLayoutManager(horizontalTv);
        binding.recVCast.setLayoutManager(horizontalCast);
        binding.filtersBottomSheet.recVGenres.setLayoutManager(horizontalGenres);
        binding.recVSelectedFilters.setLayoutManager(horizontalFilters);
    }

    private void removeFromFilterLists(String removedFilter) {
        if (selectedGenres.contains(removedFilter)) selectedGenres.remove(removedFilter);
        if (selectedMode != null && selectedMode.toString().equals(removedFilter)) selectedMode = null;
        if (selectedYear != null && selectedYear.equals(removedFilter)) {
            selectedYear = null;
            binding.filtersBottomSheet.tvSelectedYear.setText("All");
        }
        if (selectedCountry != null && selectedCountry.equals(removedFilter)) {
            selectedCountry = null;
            binding.filtersBottomSheet.tvSelectedCountry.setText("All");
        }
        if (selectedSort != null && selectedSort.equals(removedFilter)) {
            selectedSort = null;
            binding.filtersBottomSheet.tvSelectedSort.setText("Default");
        }
    }

    // -----------------------
    // SEARCH
    // -----------------------
    private void setupSearch() {
        SearchView searchView = binding.searchView;
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.parseColor("#80FFFFFF"));

        // Search icon
        ImageView searchIcon =
                searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setColorFilter(Color.WHITE);

        // Close (X) icon
        ImageView closeIcon =
                searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeIcon.setColorFilter(Color.WHITE);

        // ✅ ATTACH LISTENER TO EDITTEXT — NOT SEARCHVIEW
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                FilterState current = filtersViewModel.getFilterState().getValue();
                if (current == null) return;
                filtersViewModel.updateFilter(current.copyWithQuery(currentSearchQuery));
            }
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void afterTextChanged(Editable s){}
        });
    }

    // -----------------------
    // NO ITEM ANIMATION
    // -----------------------
    private void showNoItemAnimation() {
        binding.rlNoItemsFound.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(requireContext(), R.anim.no_item_pulse);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        binding.ivNoItem.startAnimation(anim);
    }

    private void hideNoItemAnimation() {
        binding.ivNoItem.clearAnimation();
        binding.rlNoItemsFound.setVisibility(View.GONE);
    }

    // -----------------------
    // BUTTONS & UI
    // -----------------------
    private void setupButtons() {
        binding.ivFilter.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            else bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    @SuppressLint("ResourceAsColor")
    private void showReleaseYearDialog() {

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_date_picker);
        dialog.setCancelable(true);

        NumberPicker yearPicker = dialog.findViewById(R.id.np_year);
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        // NumberPicker range
        yearPicker.setMinValue(1950);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setValue(currentYear); // default selection
        yearPicker.setWrapSelectorWheel(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            yearPicker.setTextColor(Color.WHITE);
        }


        btnOk.setOnClickListener(v -> {
            selectedYear = String.valueOf(yearPicker.getValue());

            binding.filtersBottomSheet.tvSelectedYear.setText(selectedYear);

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    @SuppressLint("ResourceAsColor")
    private void showSortByDialog() {

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sort_picker);
        dialog.setCancelable(true);

        NumberPicker sortPicker = dialog.findViewById(R.id.np_sort);
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        String[] sortOptions = {
                "Popular",
                "Latest",
                "Rating",
                "A → Z",
                "Z → A"
        };

        sortPicker.setMinValue(0);
        sortPicker.setMaxValue(sortOptions.length - 1);
        sortPicker.setDisplayedValues(sortOptions);
        sortPicker.setWrapSelectorWheel(false);

// default selection (optional)
        sortPicker.setValue(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sortPicker.setTextColor(Color.WHITE);
        }


        btnOk.setOnClickListener(v -> {
            selectedSort = sortOptions[sortPicker.getValue()];
            binding.filtersBottomSheet.tvSelectedSort.setText(selectedSort);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }


    @SuppressLint("ResourceAsColor")
    private void showCountryDialog() {

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_country_picker);
        dialog.setCancelable(true);

        CountryCodePicker countryPicker = dialog.findViewById(R.id.ccp);
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        // Force UI behavior programmatically (backup to XML)
        countryPicker.showNameCode(false);
        countryPicker.showFullName(true);
        countryPicker.showFlag(true);
        countryPicker.setContentColor(Color.WHITE);


        btnOk.setOnClickListener(v -> {
            selectedCountry = countryPicker.getSelectedCountryName();
            binding.filtersBottomSheet.tvSelectedCountry.setText(selectedCountry);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
