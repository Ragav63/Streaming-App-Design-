package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TrailerItems;
import com.example.streamingapp.databinding.FragmentTrailersBinding;
import com.example.streamingapp.presentation.adapter.TrailerRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class TrailersFragment extends Fragment {
    private FragmentTrailersBinding binding;
    private TrailerRecItemAdapter trailerRecItemAdapter;
    private List<TrailerItems> trailerItemsList;

    private StreamingViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentTrailersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(
                requireActivity(),
                new StreamingViewModelFactory()
        ).get(StreamingViewModel.class);

        setupRecycler();
    }

    private void setupRecycler() {
        binding.recVTrailers.setLayoutManager(new LinearLayoutManager(requireContext()));

        trailerItemsList = vm.getTrailerItems();
        trailerRecItemAdapter = new TrailerRecItemAdapter(item -> {
            // handle click

        });
        binding.recVTrailers.setAdapter(trailerRecItemAdapter);
        trailerRecItemAdapter.submitList(trailerItemsList);
        binding.recVTrailers.setHasFixedSize(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leak
    }
}