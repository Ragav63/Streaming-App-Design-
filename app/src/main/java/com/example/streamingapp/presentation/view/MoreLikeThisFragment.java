package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.adapter.PopularMovieRecItemAdapter;
import com.example.streamingapp.presentation.adapter.PopularSeriesRecItemAdapter;

import java.util.ArrayList;
import java.util.List;


public class MoreLikeThisFragment extends Fragment {
    private RecyclerView recVMorelikeThis;
    private RecyclerView.LayoutManager popularMoviesLayoutManager;
    private List<MovieItems> movieItemsList;
    private List<SeriesItems> seriesItemsList;
    private PopularMovieRecItemAdapter movieAdapter;
    private PopularSeriesRecItemAdapter seriesAdapter;
    private boolean isMovieList;

    public MoreLikeThisFragment() {
        // Required empty public constructor
    }

    public static MoreLikeThisFragment newInstanceWithMovies(List<MovieItems> movieItemsList) {
        MoreLikeThisFragment fragment = new MoreLikeThisFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularMovieItemsList", new ArrayList<>(movieItemsList != null ? movieItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", true);
        fragment.setArguments(args);
        return fragment;
    }

    public static MoreLikeThisFragment newInstanceWithSeries(List<SeriesItems> seriesItemsList) {
        MoreLikeThisFragment fragment = new MoreLikeThisFragment();
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
                Log.d("MoreLikeThisFragment", "Received movie items: " + movieItemsList);
            } else {
                seriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");
                Log.d("MoreLikeThisFragment", "Received series items: " + seriesItemsList);
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
            if (movieItemsList != null && !movieItemsList.isEmpty()) {
                movieAdapter = new PopularMovieRecItemAdapter(getContext(), movieItemsList);
                recVMorelikeThis.setAdapter(movieAdapter);
                recVMorelikeThis.setHasFixedSize(true);
            } else {
                Log.d("MoreLikeThisFragment", "Movie list is empty or null");
            }
        } else {
            if (seriesItemsList != null && !seriesItemsList.isEmpty()) {
                seriesAdapter = new PopularSeriesRecItemAdapter(getContext(), seriesItemsList);
                recVMorelikeThis.setAdapter(seriesAdapter);
                recVMorelikeThis.setHasFixedSize(true);
            } else {
                Log.d("MoreLikeThisFragment", "Series list is empty or null");
            }
        }


        return view;
    }

    public void updateMovieList(List<MovieItems> items) {
        movieItemsList.clear();
        movieItemsList.addAll(items);
        movieAdapter.notifyDataSetChanged();
    }

    public void updateSeriesList(List<SeriesItems> items) {
        seriesItemsList.clear();
        seriesItemsList.addAll(items);
        seriesAdapter.notifyDataSetChanged();
    }
}