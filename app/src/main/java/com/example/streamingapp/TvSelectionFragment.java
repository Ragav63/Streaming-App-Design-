package com.example.streamingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class TvSelectionFragment extends Fragment {
    private RecyclerView recVTvSelection;
    private List<TvNameItems> tvNameItemsList;
    private TvSelectionRecItemAdapter tvSelectionRecItemAdapter;

    private List<TvNameItems> generateTvNameItemList() {
        List itemsList = new ArrayList<>();
        itemsList.add(new TvNameItems("espn","ESPN", "NBA Playoff: Lakers vs Denver, Game 2"));
        itemsList.add(new TvNameItems("abc","ABC", "Euphoria - Season 1, Episode 1"));
        itemsList.add(new TvNameItems("fox","FOX", "Shogun - Season 1, Episode 3"));
        itemsList.add(new TvNameItems("abc","abc", "High School Musical"));
        itemsList.add(new TvNameItems("SS","ss", "CSK vs MI"));
        itemsList.add(new TvNameItems("SS","ss", "KKR vs Delhi"));

        return itemsList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tv_selection, container, false);

        recVTvSelection = view.findViewById(R.id.recVTvNames);

        recVTvSelection.setLayoutManager(new LinearLayoutManager(getActivity()));
        tvNameItemsList = generateTvNameItemList();
        tvSelectionRecItemAdapter = new TvSelectionRecItemAdapter(this, tvNameItemsList);
        recVTvSelection.setAdapter(tvSelectionRecItemAdapter);
        recVTvSelection.setHasFixedSize(true);



        return view;
    }
}