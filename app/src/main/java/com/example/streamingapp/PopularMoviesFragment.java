package com.example.streamingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;


public class PopularMoviesFragment extends Fragment {
    ImageView backIv;
    private RecyclerView recVPopularMovies;
    private GridLayoutManager popularMoviesLayoutManager;
    private PopularMovieRecItemAdapter popularMovieRecItemAdapter;
    private List<PopularMovieItems> popularMovieItemsList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            popularMovieItemsList = getArguments().getParcelableArrayList("popularMovieItems");
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

        if (popularMovieItemsList != null) {
            popularMoviesLayoutManager=new GridLayoutManager(getContext(), 2);
            recVPopularMovies.setLayoutManager(popularMoviesLayoutManager);
            popularMovieRecItemAdapter = new PopularMovieRecItemAdapter(getContext(), popularMovieItemsList);
            recVPopularMovies.setAdapter(popularMovieRecItemAdapter);
            recVPopularMovies.setHasFixedSize(true);
        }

        return view;
    }

    public void updatePopularMovies(List<PopularMovieItems> items) {
        popularMovieItemsList.clear();
        popularMovieItemsList.addAll(items);
        popularMovieRecItemAdapter.notifyDataSetChanged();
    }
}