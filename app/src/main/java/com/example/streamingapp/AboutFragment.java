package com.example.streamingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class AboutFragment extends Fragment {

    private RecyclerView recVAbout, recVPhotos;
    private CastRecItemAdapter castRecItemAdapter;
    private RecyclerView.LayoutManager castLayoutManager, aboutPhotosLayoutManager;
    private List<CastItems> castItemsList;
    private AboutPhotosRecItemAdapter aboutPhotosRecItemAdapter;
    private List<AboutPhotosItems> aboutPhotosItemsList;
    private List<PopularMovieItems> popularMovieItemsList;
    private List<PopularSeriesItems> popularSeriesItemsList;
    private boolean isMovieList;
    private List<CastItems> generateCastList() {
        List<CastItems> itemList = new ArrayList<>();
        itemList.add(new CastItems("Sam Worthington", "Actor",R.drawable.samworthington));
        itemList.add(new CastItems("Zoe Saldana", "Actor",R.drawable.zoesaldana));
        itemList.add(new CastItems("Michele Rodriguez", "Actor",R.drawable.michelerodriguez));
        itemList.add(new CastItems("Sigourney Weaver", "Actor",R.drawable.sigourneyweaver));
        itemList.add(new CastItems("Stephen Lang", "Actor",R.drawable.stephenlang));

        return itemList;
    }

    private List<AboutPhotosItems> generatePhotosList() {
        List<AboutPhotosItems> itemsList = new ArrayList<>();
        itemsList.add(new AboutPhotosItems(R.drawable.avatarhz));
        itemsList.add(new AboutPhotosItems(R.drawable.avatarhz1));
        itemsList.add(new AboutPhotosItems(R.drawable.avatarhz2));
        itemsList.add(new AboutPhotosItems(R.drawable.avatarhz3));
        itemsList.add(new AboutPhotosItems(R.drawable.avatarhz4));

        return itemsList;
    }

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstanceWithMovies(List<PopularMovieItems> popularMovieItemsList) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularMovieItemsList", new ArrayList<>(popularMovieItemsList != null ? popularMovieItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", true);
        fragment.setArguments(args);
        return fragment;
    }

    public static AboutFragment newInstanceWithSeries(List<PopularSeriesItems> popularSeriesItemsList) {
        AboutFragment fragment = new AboutFragment();
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
                Log.d("AboutFragment", "Received movie items: " + popularMovieItemsList);
            } else {
                popularSeriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");
                Log.d("AboutFragment", "Received series items: " + popularSeriesItemsList);
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

        castItemsList = generateCastList();

        if (isMovieList) {
            if (popularMovieItemsList != null && !popularMovieItemsList.isEmpty()) {
                castRecItemAdapter = new CastRecItemAdapter<>(getContext(), castItemsList, popularMovieItemsList);
            } else {
                Log.d("AboutFragment", "Movie list is empty or null");
            }
        } else {
            if (popularSeriesItemsList != null && !popularSeriesItemsList.isEmpty()) {
                castRecItemAdapter = new CastRecItemAdapter<>(getContext(), castItemsList, popularSeriesItemsList);
                }  else {
                Log.d("AboutFragment", "Series list is empty or null");
                }
            }

        recVAbout.setAdapter(castRecItemAdapter);
        recVAbout.setHasFixedSize(true);

        aboutPhotosLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVPhotos.setLayoutManager(aboutPhotosLayoutManager);
        aboutPhotosItemsList = generatePhotosList();
        aboutPhotosRecItemAdapter = new AboutPhotosRecItemAdapter(getContext(), aboutPhotosItemsList);
        recVPhotos.setAdapter(aboutPhotosRecItemAdapter);
        recVPhotos.setHasFixedSize(true);

        return view;
    }
}