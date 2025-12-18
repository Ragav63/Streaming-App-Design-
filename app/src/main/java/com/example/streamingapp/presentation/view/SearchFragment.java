package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.databinding.FragmentSearchBinding;
import com.example.streamingapp.presentation.adapter.CastRecItemAdapter;
import com.example.streamingapp.presentation.adapter.NowOnTvItemAdapter;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.presentation.adapter.PopularMovieRecItemAdapter;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.presentation.adapter.PopularSeriesRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;

    private StreamingViewModel vm;

    private PopularMovieRecItemAdapter movieAdapter;
    private PopularSeriesRecItemAdapter seriesAdapter;
    private CastRecItemAdapter castAdapter;

    private List<MovieItems> movieItemsList;
    private List<SeriesItems> seriesItemsList;
    private List<CastItems> castItemsList;

    private GridLayoutManager gridMovies, gridSeries, gridCast;
    private LinearLayoutManager horizontalMovies, horizontalSeries, horizontalCast;

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

        setupData();
        setupSearch();
        setupButtons();
        resetSearchUI();

    }

    // ---------------------------------------------
    // LOAD DATA
    // ---------------------------------------------
    private void setupData() {
        vm.getCastLiveData().observe(getViewLifecycleOwner(), items -> {
            castItemsList = items != null ? items : new ArrayList<>();
            if (castAdapter != null) {
                castAdapter.submitList(castItemsList);
            }
        });

        vm.getMovieLiveData().observe(getViewLifecycleOwner(), items -> {
            movieItemsList = items != null ? items : new ArrayList<>();
            if (movieAdapter != null) {
                movieAdapter.differ.submitList(movieItemsList);
            }
        });

        vm.getSeriesLiveData().observe(getViewLifecycleOwner(), items -> {
            seriesItemsList = items != null ? items : new ArrayList<>();
            if (seriesAdapter != null) {
                seriesAdapter.differ.submitList(seriesItemsList);
            }
        });

        vm.loadMovies();
        vm.loadSeries();
        vm.loadCast();

        setupAdapters();
    }


    // ---------------------------------------------
    // SETUP ADAPTERS + LAYOUTS
    // ---------------------------------------------
    private void setupAdapters() {

        // Grid layouts
        gridMovies = new GridLayoutManager(requireContext(), 2);
        gridSeries = new GridLayoutManager(requireContext(), 2);
        gridCast = new GridLayoutManager(requireContext(), 2);

        // Horizontal layouts
        horizontalMovies = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalSeries = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalCast = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        // Create adapters
        movieAdapter = new PopularMovieRecItemAdapter(requireContext(), movieItemsList,
                (movie, pos) -> {
                    Bundle b = new Bundle();
                    b.putParcelable("movieItem", movie);
                    b.putParcelableArrayList("popularMovieItemsList", (ArrayList<? extends Parcelable>) movieItemsList);
                    Navigation.findNavController(requireView()).navigate(R.id.movieScreenActivity, b);
                });

        seriesAdapter = new PopularSeriesRecItemAdapter(requireContext(), seriesItemsList,
                (s, pos) -> {
                    Bundle b = new Bundle();
                    b.putParcelable("seriesItem", s);
                    b.putParcelableArrayList("popularSeriesItemsList", (ArrayList<? extends Parcelable>) seriesItemsList);
                    Navigation.findNavController(requireView()).navigate(R.id.seriesScreenActivity, b);
                });



        castAdapter = new CastRecItemAdapter(requireContext(),cast -> {
            Bundle b = new Bundle();
            b.putParcelable("castItem", cast);
            Navigation.findNavController(requireView()).navigate(R.id.actorScreenActivity, b);
        });

        // Attach adapters
        binding.recVPopularMovies.setAdapter(movieAdapter);
        binding.recVPopularSeries.setAdapter(seriesAdapter);
        binding.recVCast.setAdapter(castAdapter);

        // Initial layout managers
        binding.recVPopularMovies.setLayoutManager(gridMovies);
        binding.recVPopularSeries.setLayoutManager(gridSeries);
        binding.recVCast.setLayoutManager(gridCast);

        // Submit lists (VERY IMPORTANT)
        movieAdapter.differ.submitList(movieItemsList);
        seriesAdapter.differ.submitList(seriesItemsList);
        castAdapter.submitList(castItemsList);
    }

    // ---------------------------------------------
    // SEARCH SYSTEM
    // ---------------------------------------------
    private void setupSearch() {

        binding.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean active = s.length() > 2;

                adjustSearchUI(active);

                String query = s.toString();

                if (active) {
                    movieAdapter.filter(query);
                    seriesAdapter.filter(query);
                    castAdapter.getFilter().filter(query);
                } else {
                    movieAdapter.filter("");
                    seriesAdapter.filter("");
                    castAdapter.getFilter().filter("");
                }

            }

            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void adjustSearchUI(boolean active) {
        if (active) {
            binding.cancelTv.setVisibility(View.VISIBLE);

            binding.recVPopularMovies.setLayoutManager(horizontalMovies);
            binding.recVPopularSeries.setLayoutManager(horizontalSeries);
            binding.recVCast.setLayoutManager(horizontalCast);



        } else {
            binding.cancelTv.setVisibility(View.GONE);

            binding.recVPopularMovies.setLayoutManager(gridMovies);
            binding.recVPopularSeries.setLayoutManager(gridSeries);
            binding.recVCast.setLayoutManager(gridCast);


        }
    }



    // ---------------------------------------------
    // BUTTONS
    // ---------------------------------------------
    private void setupButtons() {

        binding.cancelTv.setOnClickListener(v -> binding.searchEdt.setText(""));

        binding.filterTv.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.filtersFragment));



    }

    private void resetSearchUI() {
        if (binding == null) return;

        binding.searchEdt.setText("");

        binding.cancelTv.setVisibility(View.GONE);

        binding.recVPopularMovies.setLayoutManager(gridMovies);
        binding.recVPopularSeries.setLayoutManager(gridSeries);
        binding.recVCast.setLayoutManager(gridCast);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
