package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.presentation.adapter.AboutPhotosRecItemAdapter;
import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.presentation.adapter.CastRecItemAdapter;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class AboutFragment extends Fragment {

    private RecyclerView recVAbout, recVPhotos;
    private CastRecItemAdapter castRecItemAdapter;
    private RecyclerView.LayoutManager castLayoutManager, aboutPhotosLayoutManager;
    private List<CastItems> castItemsList;
    private AboutPhotosRecItemAdapter aboutPhotosRecItemAdapter;
    private List<AboutPhotosItems> aboutPhotosItemsList;
    private List<MovieItems> movieItemsList;
    private List<SeriesItems> seriesItemsList;
    private boolean isMovieList;

    private StreamingViewModel vm;


    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstanceWithMovies(List<MovieItems> movieItemsList) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularMovieItemsList", new ArrayList<>(movieItemsList != null ? movieItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", true);
        fragment.setArguments(args);
        return fragment;
    }

    public static AboutFragment newInstanceWithSeries(List<SeriesItems> seriesItemsList) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularSeriesItemsList", new ArrayList<>(seriesItemsList != null ? seriesItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        if (getArguments() != null) {
            isMovieList = getArguments().getBoolean("isMovieList");
            if (isMovieList) {
                movieItemsList = getArguments().getParcelableArrayList("popularMovieItemsList");
                Log.d("AboutFragment", "Received movie items: " + movieItemsList);
            } else {
                seriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");
                Log.d("AboutFragment", "Received series items: " + seriesItemsList);
            }
        } else {
            Log.d("AboutFragment", "getArguments() is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        recVAbout = view.findViewById(R.id.recVCast);
        recVPhotos = view.findViewById(R.id.recVPhotos);

        castLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVAbout.setLayoutManager(castLayoutManager);

        castItemsList = vm.getCast();

        if (isMovieList) {
            if (movieItemsList != null && !movieItemsList.isEmpty()) {
                castRecItemAdapter = new CastRecItemAdapter<>(getContext(), castItemsList, movieItemsList);
            } else {
                Log.d("AboutFragment", "Movie list is empty or null");
            }
        } else {
            if (seriesItemsList != null && !seriesItemsList.isEmpty()) {
                castRecItemAdapter = new CastRecItemAdapter<>(getContext(), castItemsList, seriesItemsList);
                }  else {
                Log.d("AboutFragment", "Series list is empty or null");
                }
            }

        recVAbout.setAdapter(castRecItemAdapter);
        recVAbout.setHasFixedSize(true);

        aboutPhotosLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVPhotos.setLayoutManager(aboutPhotosLayoutManager);
        aboutPhotosItemsList = vm.getPhotos();
        aboutPhotosRecItemAdapter = new AboutPhotosRecItemAdapter(getContext(), aboutPhotosItemsList);
        recVPhotos.setAdapter(aboutPhotosRecItemAdapter);
        recVPhotos.setHasFixedSize(true);

        return view;
    }
}