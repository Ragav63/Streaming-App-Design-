package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.presentation.adapter.FilmographyRecItemAdapter;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;

import java.util.ArrayList;
import java.util.List;


public class FilmographyFragment extends Fragment {

    private RecyclerView recVFilmography;
    private GridLayoutManager filmographyLayoutManager;
    private FilmographyRecItemAdapter<Object> filmographyRecItemAdapter;
    private List<MovieItems> movieItemsList;
    private List<SeriesItems> seriesItemsList;
    private boolean isMovieList;


    public FilmographyFragment() {
        // Required empty public constructor
    }

    public static FilmographyFragment newInstanceWithMovies(List<MovieItems> movieItemsList) {
        FilmographyFragment fragment = new FilmographyFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularMovieItemsList", new ArrayList<>(movieItemsList != null ? movieItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", true);
        fragment.setArguments(args);
        return fragment;
    }

    public static FilmographyFragment newInstanceWithSeries(List<SeriesItems> seriesItemsList) {
        FilmographyFragment fragment = new FilmographyFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularSeriesItemsList", new ArrayList<>(seriesItemsList != null ? seriesItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isMovieList = getArguments().getBoolean("isMovieList");
            if (isMovieList) {
                movieItemsList = getArguments().getParcelableArrayList("popularMovieItemsList");
                Log.d("FilmographyFragment", "Received movie items: " + movieItemsList);
            } else {
                seriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");
                Log.d("FilmographyFragment", "Received series items: " + seriesItemsList);
            }
        } else {
            Log.d("FilmographyFragment", "getArguments() is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filmography, container, false);

        recVFilmography = view.findViewById(R.id.recVFilmography);

        filmographyLayoutManager = new GridLayoutManager(getContext(), 3);
        recVFilmography.setLayoutManager(filmographyLayoutManager);

        if (isMovieList) {
            if (movieItemsList != null && !movieItemsList.isEmpty()) {
                filmographyRecItemAdapter = new FilmographyRecItemAdapter<>(getContext(), new ArrayList<>(movieItemsList));
                recVFilmography.setAdapter(filmographyRecItemAdapter);
                recVFilmography.setHasFixedSize(true);
            } else {
                Log.d("FilmographyFragment", "Movie list is empty or null");
            }
        } else {
            if (seriesItemsList != null && !seriesItemsList.isEmpty()) {
                filmographyRecItemAdapter = new FilmographyRecItemAdapter<>(getContext(), new ArrayList<>(seriesItemsList));
                recVFilmography.setAdapter(filmographyRecItemAdapter);
                recVFilmography.setHasFixedSize(true);
            } else {
                Log.d("FilmographyFragment", "Series list is empty or null");
            }
//            else {
//
//            filmographyListItems = generateFilmographyItemList();
//            filmographyRecItemAdapter = new FilmographyRecItemAdapter<>(getContext(), new ArrayList<>(filmographyListItems));
//            recVFilmography.setAdapter(filmographyRecItemAdapter);
//            recVFilmography.setHasFixedSize(true);
        }

        return view;
    }
}