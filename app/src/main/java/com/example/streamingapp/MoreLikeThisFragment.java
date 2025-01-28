package com.example.streamingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class MoreLikeThisFragment extends Fragment {
    private RecyclerView recVMorelikeThis;
    private RecyclerView.LayoutManager popularMoviesLayoutManager;
    private List<PopularMovieItems> popularMovieItemsList;
    private List<PopularSeriesItems> popularSeriesItemsList;
    private PopularMovieRecItemAdapter movieAdapter;
    private PopularSeriesRecItemAdapter seriesAdapter;
    private boolean isMovieList;

    public MoreLikeThisFragment() {
        // Required empty public constructor
    }

    public static MoreLikeThisFragment newInstanceWithMovies(List<PopularMovieItems> popularMovieItemsList) {
        MoreLikeThisFragment fragment = new MoreLikeThisFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularMovieItemsList", new ArrayList<>(popularMovieItemsList != null ? popularMovieItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", true);
        fragment.setArguments(args);
        return fragment;
    }

    public static MoreLikeThisFragment newInstanceWithSeries(List<PopularSeriesItems> popularSeriesItemsList) {
        MoreLikeThisFragment fragment = new MoreLikeThisFragment();
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
                Log.d("MoreLikeThisFragment", "Received movie items: " + popularMovieItemsList);
            } else {
                popularSeriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");
                Log.d("MoreLikeThisFragment", "Received series items: " + popularSeriesItemsList);
            }
        } else {
            Log.d("MoreLikeThisFragment", "getArguments() is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more_like_this, container, false);

        recVMorelikeThis = view.findViewById(R.id.recVMorelikeThis);

        popularMoviesLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recVMorelikeThis.setLayoutManager(popularMoviesLayoutManager);

        if (isMovieList) {
            if (popularMovieItemsList != null && !popularMovieItemsList.isEmpty()) {
                movieAdapter = new PopularMovieRecItemAdapter(getContext(), popularMovieItemsList);
                recVMorelikeThis.setAdapter(movieAdapter);
                recVMorelikeThis.setHasFixedSize(true);
            } else {
                Log.d("MoreLikeThisFragment", "Movie list is empty or null");
            }
        } else {
            if (popularSeriesItemsList != null && !popularSeriesItemsList.isEmpty()) {
                seriesAdapter = new PopularSeriesRecItemAdapter(getContext(), popularSeriesItemsList);
                recVMorelikeThis.setAdapter(seriesAdapter);
                recVMorelikeThis.setHasFixedSize(true);
            } else {
                Log.d("MoreLikeThisFragment", "Series list is empty or null");
            }
        }


        return view;
    }

    public void updateMovieList(List<PopularMovieItems> items) {
        popularMovieItemsList.clear();
        popularMovieItemsList.addAll(items);
        movieAdapter.notifyDataSetChanged();
    }

    public void updateSeriesList(List<PopularSeriesItems> items) {
        popularSeriesItemsList.clear();
        popularSeriesItemsList.addAll(items);
        seriesAdapter.notifyDataSetChanged();
    }
}