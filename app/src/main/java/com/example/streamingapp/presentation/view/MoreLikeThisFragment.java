package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentMoreLikeThisBinding;
import com.example.streamingapp.presentation.adapter.PopularMovieRecItemAdapter;
import com.example.streamingapp.presentation.adapter.PopularSeriesRecItemAdapter;

import java.util.ArrayList;
import java.util.List;


public class MoreLikeThisFragment extends Fragment {

    private FragmentMoreLikeThisBinding binding;

    private List<MovieItems> movieItemsList;
    private List<SeriesItems> seriesItemsList;
    private PopularMovieRecItemAdapter movieAdapter;
    private PopularSeriesRecItemAdapter seriesAdapter;

    private boolean isMovieList;

    public MoreLikeThisFragment() { }

    // ─────────────────────────────────────────────
    // FACTORY METHODS (BUNDLE — NO SAFE ARGS)
    // ─────────────────────────────────────────────

    public static MoreLikeThisFragment newInstanceWithMovies(List<MovieItems> movieItems) {
        MoreLikeThisFragment fragment = new MoreLikeThisFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("movies", new ArrayList<>(movieItems));
        bundle.putBoolean("isMovieList", true);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static MoreLikeThisFragment newInstanceWithSeries(List<SeriesItems> seriesItems) {
        MoreLikeThisFragment fragment = new MoreLikeThisFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("series", new ArrayList<>(seriesItems));
        bundle.putBoolean("isMovieList", false);
        fragment.setArguments(bundle);
        return fragment;
    }

    // ─────────────────────────────────────────────

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isMovieList = getArguments().getBoolean("isMovieList");

            if (isMovieList) {
                movieItemsList = getArguments().getParcelableArrayList("movies");
                Log.d("MoreLikeThis", "Movie list size: " + (movieItemsList != null ? movieItemsList.size() : 0));
            } else {
                seriesItemsList = getArguments().getParcelableArrayList("series");
                Log.d("MoreLikeThis", "Series list size: " + (seriesItemsList != null ? seriesItemsList.size() : 0));
            }
        }
    }

    // ─────────────────────────────────────────────

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMoreLikeThisBinding.inflate(inflater, container, false);

        binding.recVMorelikeThis.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        if (isMovieList) {
            if (movieItemsList != null && !movieItemsList.isEmpty()) {
                movieAdapter = new PopularMovieRecItemAdapter(requireContext(), movieItemsList, (movie, pos) -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("movieItem",movie);
                    bundle.putParcelableArrayList(
                            "popularMovieItemsList",
                            new ArrayList<>(movieAdapter.getCurrentList())
                    );
                    // Navigate using NavController
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.movieScreenActivity, bundle);
                });
                binding.recVMorelikeThis.setAdapter(movieAdapter);
            }
        } else {
            if (seriesItemsList != null && !seriesItemsList.isEmpty()) {
                seriesAdapter =  new PopularSeriesRecItemAdapter(
                        requireContext(),
                        seriesItemsList,
                        (item, pos) -> {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("seriesItem",item);
                            bundle.putParcelableArrayList(
                                    "popularSeriesItemsList",
                                    new ArrayList<>(seriesAdapter.getCurrentList())
                            );
                            // Navigate using NavController
                            NavController navController = Navigation.findNavController(requireView());
                            navController.navigate(R.id.seriesScreenActivity, bundle);

                        }
                );
                binding.recVMorelikeThis.setAdapter(seriesAdapter);
            }
        }

        return binding.getRoot();
    }

    // ─────────────────────────────────────────────
    // OPTIONAL — LIST UPDATERS
    // ─────────────────────────────────────────────

    public void updateMovieList(List<MovieItems> items) {
        if (movieItemsList == null) movieItemsList = new ArrayList<>();
        movieItemsList.clear();
        movieItemsList.addAll(items);
        if (movieAdapter != null) movieAdapter.notifyDataSetChanged();
    }

    public void updateSeriesList(List<SeriesItems> items) {
        if (seriesItemsList == null) seriesItemsList = new ArrayList<>();
        seriesItemsList.clear();
        seriesItemsList.addAll(items);
        if (seriesAdapter != null) seriesAdapter.notifyDataSetChanged();
    }
}