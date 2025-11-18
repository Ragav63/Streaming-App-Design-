package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TrailerItems;
import com.example.streamingapp.presentation.adapter.TrailerRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class TrailersFragment extends Fragment {
    private RecyclerView recVTrailers;
    private TrailerRecItemAdapter trailerRecItemAdapter;
    private List<TrailerItems> trailerItemsList;

    private StreamingViewModel vm;

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
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);
        recVTrailers = view.findViewById(R.id.recVTrailers);

        recVTrailers.setLayoutManager(new LinearLayoutManager(getContext()));
        trailerItemsList = vm.getTrailerItems();
        trailerRecItemAdapter = new TrailerRecItemAdapter(getContext(), trailerItemsList);
        recVTrailers.setAdapter(trailerRecItemAdapter);
        recVTrailers.setHasFixedSize(true);

        return view;
    }
}