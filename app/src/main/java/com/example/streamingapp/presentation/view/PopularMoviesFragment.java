package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.presentation.adapter.PopularMovieRecItemAdapter;
import com.example.streamingapp.R;

import java.util.List;


public class PopularMoviesFragment extends Fragment {
    ImageView backIv;
    private RecyclerView recVPopularMovies;
    private GridLayoutManager popularMoviesLayoutManager;
    private PopularMovieRecItemAdapter popularMovieRecItemAdapter;
    private List<MovieItems> movieItemsList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieItemsList = getArguments().getParcelableArrayList("popularMovieItems");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        backIv = view.findViewById(R.id.backIv);
        recVPopularMovies = view.findViewById(R.id.recVPopularMovies);

        backIv.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        if (movieItemsList != null) {
            popularMoviesLayoutManager=new GridLayoutManager(getContext(), 2);
            recVPopularMovies.setLayoutManager(popularMoviesLayoutManager);
            popularMovieRecItemAdapter = new PopularMovieRecItemAdapter(getContext(), movieItemsList);
            recVPopularMovies.setAdapter(popularMovieRecItemAdapter);
            recVPopularMovies.setHasFixedSize(true);
        }

        return view;
    }

    public void updatePopularMovies(List<MovieItems> items) {
        movieItemsList.clear();
        movieItemsList.addAll(items);
        popularMovieRecItemAdapter.notifyDataSetChanged();
    }
}