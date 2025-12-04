package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.data.model.TrailerItems;
import com.example.streamingapp.databinding.FragmentTrailersBinding;
import com.example.streamingapp.presentation.adapter.TrailerRecItemAdapter;
import com.example.streamingapp.presentation.utils.YouTubeDurationFetcher;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class TrailersFragment extends Fragment {
    private FragmentTrailersBinding binding;
    private TrailerRecItemAdapter trailerRecItemAdapter;
    private StreamingViewModel vm;

    private boolean isMovieList;
    private MovieItems movieItem;      // <-- SINGLE ITEM
    private SeriesItems seriesItem;
    private final Map<String, String> durationCache = new HashMap<>();
    private final List<TrailerItems> trailerItemsList = new ArrayList<>();

    public TrailersFragment() { }

    // ─────────────────────────────────────────────
    // FACTORY METHODS (BUNDLE — NO SAFE ARGS)
    // ─────────────────────────────────────────────

    public static TrailersFragment newInstanceWithMovies(MovieItems movieItem) {
        TrailersFragment fragment = new TrailersFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie_item", movieItem);
        bundle.putBoolean("isMovieList", true);
        fragment.setArguments(bundle);
        return fragment;
    }


    public static TrailersFragment newInstanceWithSeries(SeriesItems seriesItem) {
        TrailersFragment fragment = new TrailersFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("series_item", seriesItem);
        bundle.putBoolean("isMovieList", false);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentTrailersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            isMovieList = getArguments().getBoolean("isMovieList");

            if (isMovieList) {
                movieItem = getArguments().getParcelable("movie_item");
            } else {
                seriesItem = getArguments().getParcelable("series_item");
            }
        }


        vm = new ViewModelProvider(
                requireActivity(),
                new StreamingViewModelFactory()
        ).get(StreamingViewModel.class);

        setupRecycler();
    }

    private void setupRecycler() {
        binding.recVTrailers.setLayoutManager(new LinearLayoutManager(requireContext()));

        trailerRecItemAdapter = new TrailerRecItemAdapter(item -> {
            // handle click
        });
        binding.recVTrailers.setAdapter(trailerRecItemAdapter);
        binding.recVTrailers.setHasFixedSize(true);

        List<String> trailerUrls = new ArrayList<>();
        String title = "";

        if (isMovieList && movieItem != null) {
            trailerUrls = movieItem.getTrailers();
            title = movieItem.getTitle();
        } else if (!isMovieList && seriesItem != null) {
            trailerUrls = seriesItem.getTrailers();
            title = seriesItem.getTitle();
        }

        if (trailerUrls == null || trailerUrls.isEmpty()) {
            showNoTrailers();
            return;
        }

        trailerItemsList.clear();
        durationCache.clear();

        for (int i = 0; i < trailerUrls.size(); i++) {
            String trailerName = title + " • Trailer " + (i + 1);
            trailerItemsList.add(new TrailerItems(trailerName, "Loading...", trailerUrls.get(i)));
        }

        // Now submit the initial list
        trailerRecItemAdapter.submitList(new ArrayList<>(trailerItemsList));

        fetchAllDurations(trailerUrls);
    }


    private void fetchAllDurations(List<String> trailerUrls) {
        AtomicInteger completedCount = new AtomicInteger(0);
        int totalUrls = trailerUrls.size();

        for (int i = 0; i < trailerUrls.size(); i++) {
            final int index = i;
            String url = trailerUrls.get(i);

            // Check cache first
            if (durationCache.containsKey(url)) {
                updateTrailerDuration(index, durationCache.get(url));
                completedCount.incrementAndGet();
                continue;
            }

            YouTubeDurationFetcher.fetchDuration(url, new YouTubeDurationFetcher.DurationCallback() {
                @Override
                public void onDurationReady(String duration) {
                    durationCache.put(url, duration);
                    updateTrailerDuration(index, duration);
                    completedCount.incrementAndGet();

                    // Update progress
                    int progress = (completedCount.get() * 100) / totalUrls;
                    Log.d("TrailersFragment", "Progress: " + progress + "%");
                }

                @Override
                public void onError(String message) {
                    Log.e("TrailersFragment", "Failed to get duration for trailer " + index + ": " + message);
                    durationCache.put(url, "Unknown");
                    updateTrailerDuration(index, "Unknown");
                    completedCount.incrementAndGet();
                }
            });
        }
    }

    private void updateTrailerDuration(int index, String duration) {
        if (!isAdded()) return; // early exit if fragment detached

        if (index >= 0 && index < trailerItemsList.size()) {
            List<TrailerItems> newList = new ArrayList<>(trailerItemsList);

            TrailerItems oldItem = newList.get(index);
            TrailerItems newItem = new TrailerItems(oldItem.getTrailerTitle(), duration, oldItem.getTrailerUrl());
            newList.set(index, newItem);
            trailerItemsList.set(index, newItem);

            // Safe UI update
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> trailerRecItemAdapter.submitList(new ArrayList<>(newList)));
            }
        }
    }


    private void showNoTrailers() {
        binding.recVTrailers.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leak
    }
}