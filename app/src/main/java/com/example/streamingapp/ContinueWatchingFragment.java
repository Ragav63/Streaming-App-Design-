package com.example.streamingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;


public class ContinueWatchingFragment extends Fragment {
    ImageView backIv;
    private RecyclerView recVContinueWatching;
    private ContinueWatchingFragmentItemAdapter continueWatchingFragmentItemAdapter;
    private List<ContinueWatchingItems> continueWatchingItemsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Retrieve the data from the Bundle
        if (getArguments() != null) {
            continueWatchingItemsList = getArguments().getParcelableArrayList("continueWatchingItems");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_continue_watching, container, false);

        recVContinueWatching = view.findViewById(R.id.recVContinueWatching);
        backIv = view.findViewById(R.id.backIv);

        backIv.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        continueWatchingFragmentItemAdapter = new ContinueWatchingFragmentItemAdapter(getContext(), continueWatchingItemsList);
        recVContinueWatching.setAdapter(continueWatchingFragmentItemAdapter);

        return view;
    }
}