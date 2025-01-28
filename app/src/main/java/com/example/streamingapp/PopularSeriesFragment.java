package com.example.streamingapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;


public class PopularSeriesFragment extends Fragment {
    ImageView backIv;
    private RecyclerView recVPopularSeries;
    private GridLayoutManager popularSeriesLayoutManager;
    private PopularSeriesRecItemAdapter popularSeriesRecItemAdapter;
    private List<PopularSeriesItems> popularSeriesItemsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            popularSeriesItemsList = getArguments().getParcelableArrayList("popularSeriesItems");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popular_series, container, false);

        backIv = view.findViewById(R.id.backIv);
        recVPopularSeries = view.findViewById(R.id.recVPopularSeries);

        backIv.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        if (popularSeriesItemsList != null) {
            popularSeriesLayoutManager=new GridLayoutManager(getContext(), 2);
            recVPopularSeries.setLayoutManager(popularSeriesLayoutManager);
            popularSeriesRecItemAdapter = new PopularSeriesRecItemAdapter(getContext(), popularSeriesItemsList);
            recVPopularSeries.setAdapter(popularSeriesRecItemAdapter);
            recVPopularSeries.setHasFixedSize(true);
        }

        return view;
    }

    public void updatePopularSeries(List<PopularSeriesItems> items) {
        popularSeriesItemsList.clear();
        popularSeriesItemsList.addAll(items);
        popularSeriesRecItemAdapter.notifyDataSetChanged();
    }
}