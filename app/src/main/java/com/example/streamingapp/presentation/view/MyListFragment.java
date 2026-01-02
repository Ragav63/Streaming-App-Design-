package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.ContentType;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.HistoryUiItem;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.FragmentMyListBinding;
import com.example.streamingapp.presentation.adapter.HistoryRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyListFragment extends Fragment {

   private FragmentMyListBinding binding;
    private HistoryRecItemAdapter historyRecItemAdapter;
    private List<HistoryItems> historyCache;
    private List<MovieItems> movieCache;
    private List<SeriesItems> seriesCache;
    private StreamingViewModel vm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        setupUI();
        setupObservers();
        loadData();
    }

    private void setupUI() {
        // Set up RecyclerView
        binding.recVHistory.setLayoutManager(
                new GridLayoutManager(requireContext(), 2)
        );

        // Initialize adapter WITH DEBUGGING
        historyRecItemAdapter = new HistoryRecItemAdapter(
                requireContext(),
                src -> {
                    Bundle b = new Bundle();
                    b.putString("imageResource", src);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.fullScreenImageActivity, b);
                }
        );


        binding.recVHistory.setAdapter(historyRecItemAdapter);

        // Show loading initially
        showLoading(true);
        binding.emptyStateText.setVisibility(View.GONE);

        binding.llFavMoviesSeries.setOnClickListener(v->{
            Navigation.findNavController(requireView())
                    .navigate(R.id.favouriteFragment);
        });
    }

    private void setupObservers() {
        // Observe history data
        vm.getHistoryLiveData().observe(getViewLifecycleOwner(), history -> {

            if (history != null) {
                historyCache = history;
                for (HistoryItems item : history) {
                    Log.d("FavouriteFragment", "History item: " + item.getTitle() + " at " + item.getViewedAt());
                }
            } else {
                historyCache = new ArrayList<>();
                Log.d("FavouriteFragment", "History data is null");
            }

            tryBuildUiList();
        });

        // Observe movie data
        vm.getMovieLiveData().observe(getViewLifecycleOwner(), movies -> {
            Log.d("FavouriteFragment", "Movie data received: " + (movies != null ? movies.size() : 0) + " items");

            if (movies != null) {
                movieCache = movies;
                for (MovieItems movie : movies) {
                    Log.d("FavouriteFragment", "Movie item: " + movie.getTitle() + " - " + movie.getPoster());
                }
            } else {
                movieCache = new ArrayList<>();
                Log.d("FavouriteFragment", "Movie data is null");
            }

            tryBuildUiList();
        });

        // Observe series data
        vm.getSeriesLiveData().observe(getViewLifecycleOwner(), series -> {
            Log.d("FavouriteFragment", "Series data received: " + (series != null ? series.size() : 0) + " items");

            if (series != null) {
                seriesCache = series;
                for (SeriesItems serie : series) {
                    Log.d("FavouriteFragment", "Series item: " + serie.getTitle() + " - " + serie.getPoster());
                }
            } else {
                seriesCache = new ArrayList<>();
                Log.d("FavouriteFragment", "Series data is null");
            }

            tryBuildUiList();
        });
    }

    private void loadData() {
        Log.d("FavouriteFragment", "Loading data...");
        vm.loadHistory();
        vm.loadMovies();
        vm.loadSeries();
    }

    private void tryBuildUiList() {
        // Check if all data is loaded
        if (historyCache == null || movieCache == null || seriesCache == null) {
            Log.d("FavouriteFragment", "Waiting for data. History: " +
                    (historyCache != null ? "loaded" : "null") +
                    ", Movies: " + (movieCache != null ? "loaded" : "null") +
                    ", Series: " + (seriesCache != null ? "loaded" : "null"));
            return;
        }

        Log.d("FavouriteFragment", "Building UI list. History: " + historyCache.size() +
                ", Movies: " + movieCache.size() + ", Series: " + seriesCache.size());

        // Create combined map for all content
        Map<String, Object> contentMap = new HashMap<>();

        // Add all movies to map
        for (MovieItems movie : movieCache) {
            if (movie != null && movie.getTitle() != null) {
                contentMap.put(movie.getTitle().trim().toLowerCase(), movie);
            }
        }

        // Add all series to map
        for (SeriesItems series : seriesCache) {
            if (series != null && series.getTitle() != null) {
                contentMap.put(series.getTitle().trim().toLowerCase(), series);
            }
        }

        List<HistoryUiItem> uiList = new ArrayList<>();

        for (HistoryItems history : historyCache) {
            if (history != null && history.getTitle() != null) {
                String titleKey = history.getTitle().trim().toLowerCase();
                Object content = contentMap.get(titleKey);

                if (content instanceof MovieItems) {
                    MovieItems movie = (MovieItems) content;
                    uiList.add(new HistoryUiItem(
                            history.getId(),
                            history.getTitle(),
                            history.getViewedAt(),
                            movie.getImdb_rating(),
                            movie.getPoster(),
                            ContentType.MOVIE
                    ));
                    Log.d("FavouriteFragment", "✓ Matched movie: " + history.getTitle() +
                            " | Poster: " + movie.getPoster() +
                            " | Rating: " + movie.getImdb_rating());

                } else if (content instanceof SeriesItems) {
                    SeriesItems series = (SeriesItems) content;
                    uiList.add(new HistoryUiItem(
                            history.getId(),
                            history.getTitle(),
                            history.getViewedAt(),
                            series.getImdb_rating(),
                            series.getPoster(),
                            ContentType.SERIES
                    ));
                    Log.d("FavouriteFragment", "✓ Matched series: " + history.getTitle());

                } else {
                    Log.d("FavouriteFragment", "✗ No match for: " + history.getTitle());
                }
            }
        }

        Log.d("FavouriteFragment", "Final UI list size: " + uiList.size());

        // Debug: Print the UI list
        for (HistoryUiItem item : uiList) {
            Log.d("FavouriteFragment", "UI List Item: " +
                    "Title: " + item.getTitle() +
                    " | Rating: " + item.getRating() +
                    " | Image: " + item.getPosterUrl());
        }

        // Update UI
        requireActivity().runOnUiThread(() -> {
            Log.d("FavouriteFragment", "Submitting list to adapter. Size: " + uiList.size());

            // Try both lists to debug
            historyRecItemAdapter.submitList(uiList);
            // historyRecItemAdapter.submitList(testList); // Uncomment to test

            // Check adapter state
            Log.d("FavouriteFragment", "Adapter item count after submit: " + historyRecItemAdapter.getItemCount());

            if (uiList.isEmpty()) {
                showEmptyState("No history items found");
            } else {
                showLoading(false);
                binding.emptyStateText.setVisibility(View.GONE);
                binding.recVHistory.setVisibility(View.VISIBLE);
            }

        });
    }

    private void showLoading(boolean show) {
        if (binding != null) {
            if (show) {
                binding.recVHistory.setVisibility(View.GONE);
            } else {
                binding.recVHistory.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showEmptyState(String message) {
        if (binding != null) {
            binding.recVHistory.setVisibility(View.GONE);
            binding.emptyStateText.setVisibility(View.VISIBLE);
            binding.emptyStateText.setText(message);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FavouriteFragment", "onResume called");
        // Refresh data when fragment resumes
        if (historyRecItemAdapter != null &&
                (historyCache == null || movieCache == null || seriesCache == null ||
                        historyRecItemAdapter.getItemCount() == 0)) {
            loadData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}