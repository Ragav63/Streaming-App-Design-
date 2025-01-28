package com.example.streamingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class FilmographyFragment extends Fragment {

    private RecyclerView recVFilmography;
    private GridLayoutManager filmographyLayoutManager;
    private List<FilmographyListItems> filmographyListItems;
    private FilmographyRecItemAdapter<Object> filmographyRecItemAdapter;
    private List<PopularMovieItems> popularMovieItemsList;
    private List<PopularSeriesItems> popularSeriesItemsList;
    private boolean isMovieList;
    private List<FilmographyListItems> generateFilmographyItemList() {
        List<FilmographyListItems> itemList = new ArrayList<>();
        itemList.add(new FilmographyListItems("7.2", "Venom 3",R.drawable.venom3vertical));
        itemList.add(new FilmographyListItems("6.8", "Kalki",R.drawable.kalkivertical));
        itemList.add(new FilmographyListItems("8.0", "Avengers Endgame",R.drawable.avengersvertical));
        itemList.add(new FilmographyListItems("7.0", "Avatar The Last Airbender",R.drawable.avatarthelastairbendervertical));
        itemList.add(new FilmographyListItems("7.3", "Captain America Brave New World",R.drawable.captainamericavertical));
        itemList.add(new FilmographyListItems("6.5", "Avatar The Way of Water",R.drawable.avatarthewayofwatervertical));

        return itemList;
    }

    public FilmographyFragment() {
        // Required empty public constructor
    }

    public static FilmographyFragment newInstanceWithMovies(List<PopularMovieItems> popularMovieItemsList) {
        FilmographyFragment fragment = new FilmographyFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularMovieItemsList", new ArrayList<>(popularMovieItemsList != null ? popularMovieItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", true);
        fragment.setArguments(args);
        return fragment;
    }

    public static FilmographyFragment newInstanceWithSeries(List<PopularSeriesItems> popularSeriesItemsList) {
        FilmographyFragment fragment = new FilmographyFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularSeriesItemsList", new ArrayList<>(popularSeriesItemsList != null ? popularSeriesItemsList : new ArrayList<>()));
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
                popularMovieItemsList = getArguments().getParcelableArrayList("popularMovieItemsList");
                Log.d("FilmographyFragment", "Received movie items: " + popularMovieItemsList);
            } else {
                popularSeriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");
                Log.d("FilmographyFragment", "Received series items: " + popularSeriesItemsList);
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
            if (popularMovieItemsList != null && !popularMovieItemsList.isEmpty()) {
                filmographyRecItemAdapter = new FilmographyRecItemAdapter<>(getContext(), new ArrayList<>(popularMovieItemsList));
                recVFilmography.setAdapter(filmographyRecItemAdapter);
                recVFilmography.setHasFixedSize(true);
            } else {
                Log.d("FilmographyFragment", "Movie list is empty or null");
            }
        } else {
            if (popularSeriesItemsList != null && !popularSeriesItemsList.isEmpty()) {
                filmographyRecItemAdapter = new FilmographyRecItemAdapter<>(getContext(), new ArrayList<>(popularSeriesItemsList));
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