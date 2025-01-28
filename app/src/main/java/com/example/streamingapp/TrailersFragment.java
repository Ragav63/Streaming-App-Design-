package com.example.streamingapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class TrailersFragment extends Fragment {
    private RecyclerView recVTrailers;
    private TrailerRecItemAdapter trailerRecItemAdapter;
    private List<TrailerItems> trailerItemsList;

    private List<TrailerItems> generateTrailerList() {
        List<TrailerItems> itemList = new ArrayList<>();
        itemList.add(new TrailerItems("Avatar: The Way of Water |Official Teaser Trailer", "2 min 14 sec",R.drawable.avatarthewayofwater));
        itemList.add(new TrailerItems("Avatar: The Way of Water |New Trailer", "2 min 14 sec",R.drawable.avatarthewayofwater));
        itemList.add(new TrailerItems("Avatar: The Way of Water |Trailer 2024", "2 min 14 sec",R.drawable.avatarthewayofwater));
        return itemList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trailers, container, false);

        recVTrailers = view.findViewById(R.id.recVTrailers);

        recVTrailers.setLayoutManager(new LinearLayoutManager(getContext()));
        trailerItemsList = generateTrailerList();
        trailerRecItemAdapter = new TrailerRecItemAdapter(getContext(), trailerItemsList);
        recVTrailers.setAdapter(trailerRecItemAdapter);
        recVTrailers.setHasFixedSize(true);

        return view;
    }
}