package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.data.model.CrewMember;
import com.example.streamingapp.databinding.FragmentFilmographyBinding;
import com.example.streamingapp.presentation.adapter.FilmographyRecItemAdapter;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class FilmographyFragment extends Fragment {

    private FragmentFilmographyBinding binding;

    private FilmographyRecItemAdapter<Parcelable> filmographyRecItemAdapter;
    private List<MovieItems> movieList = new ArrayList<>();
    private List<SeriesItems> seriesList = new ArrayList<>();

    private boolean isMovieList;
    private String actorName;
    private StreamingViewModel vm;

    public FilmographyFragment() {}

    public static Fragment newInstance(String actorName, Boolean isMovie) {
        FilmographyFragment fragment = new FilmographyFragment();
        Bundle args = new Bundle();
        args.putBoolean("isMovie", isMovie);
        args.putString("actorName", actorName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFilmographyBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        actorName = requireArguments().getString("actorName");
        isMovieList = requireArguments().getBoolean("isMovie");        // Cast safely


        setupRecycler();

        if (isMovieList) {
            vm.getMovieLiveData().observe(getViewLifecycleOwner(), movies -> {
                if (movies == null) return;

                movieList = filterMoviesByActor(actorName, movies);
                filmographyRecItemAdapter.submitList(dedupeMovies(movieList));
            });
            vm.loadMovies();
        } else {
            vm.getSeriesLiveData().observe(getViewLifecycleOwner(), series -> {
                if (series == null) return;

                seriesList = filterSeriesByActor(actorName, series);
                filmographyRecItemAdapter.submitList(dedupeSeries(seriesList));
            });
            vm.loadSeries();
        }

        return binding.getRoot();
    }


    private List<SeriesItems> filterSeriesByActor(
            String actorName,
            List<SeriesItems> items
    ) {
        List<SeriesItems> filtered = new ArrayList<>();

        for (SeriesItems series : items) {
            if (series.getCrew() == null) continue;

            for (CrewMember crew : series.getCrew()) {
                if (actorName.equalsIgnoreCase(crew.getName())) {
                    filtered.add(series);
                    break;
                }
            }
        }
        return filtered;
    }

    private List<SeriesItems> dedupeSeries(List<SeriesItems> list) {
        Map<Integer, SeriesItems> map = new LinkedHashMap<>();
        for (SeriesItems s : list) {
            map.put(s.getId(), s);
        }
        return new ArrayList<>(map.values());
    }

    private List<MovieItems> filterMoviesByActor(
            String actorName,
            List<MovieItems> items
    ) {
        List<MovieItems> filtered = new ArrayList<>();

        for (MovieItems movie : items) {
            if (movie.getCrew() == null) continue;

            for (CrewMember crew : movie.getCrew()) {
                if (actorName.equalsIgnoreCase(crew.getName())) {
                    filtered.add(movie);
                    break;
                }
            }
        }
        return filtered;
    }

    private List<MovieItems> dedupeMovies(List<MovieItems> list) {
        Map<Integer, MovieItems> map = new LinkedHashMap<>();
        for (MovieItems m : list) {
            map.put(m.getId(), m);
        }
        return new ArrayList<>(map.values());
    }



    private void setupRecycler() {
        binding.recVFilmography.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.recVFilmography.setHasFixedSize(true);

        filmographyRecItemAdapter =
                new FilmographyRecItemAdapter<>(requireContext(), item -> {

                    Bundle bundle = new Bundle();

                    if (item instanceof MovieItems) {
                        MovieItems movie = (MovieItems) item;
                        bundle.putParcelable("movieItem", movie);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.movieScreenActivity, bundle);

                    } else if (item instanceof SeriesItems) {
                        SeriesItems series = (SeriesItems) item;
                        bundle.putParcelable("seriesItem", series);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.seriesScreenActivity, bundle);
                    }
                });

        binding.recVFilmography.setAdapter(filmographyRecItemAdapter);

    }


    private List<Parcelable> dedupe(List<Parcelable> list) {
        Map<String, Parcelable> map = new LinkedHashMap<>();

        for (Parcelable item : list) {
            if (item instanceof MovieItems) {
                MovieItems m = (MovieItems) item;
                map.put(String.valueOf(m.getId()), m);
            } else if (item instanceof SeriesItems) {
                SeriesItems s = (SeriesItems) item;
                map.put(String.valueOf(s.getId()), s);
            }
        }
        Log.d("Filmography",map.values().toString());
        return new ArrayList<>(map.values());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}