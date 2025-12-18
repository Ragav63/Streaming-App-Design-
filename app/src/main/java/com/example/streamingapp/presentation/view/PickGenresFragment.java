package com.example.streamingapp.presentation.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.databinding.FragmentPickGenresBinding;
import com.example.streamingapp.presentation.adapter.PickGenreRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.AvRecomPagerViewModel;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class PickGenresFragment extends Fragment {

    private FragmentPickGenresBinding binding;

    private PickGenreRecItemAdapter pickGenreRecItemAdapter;
    private List<PickItem> PickItemList;

    private StreamingViewModel vm;
    private NavController navController;
    private AvRecomPagerViewModel pagerVM;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPickGenresBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        pagerVM = new ViewModelProvider(requireActivity())
                .get(AvRecomPagerViewModel.class);

        setupRecycler();

        handleIncomingArguments();
    }


    private void setupRecycler() {


        binding.recVGenre.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        pickGenreRecItemAdapter = new PickGenreRecItemAdapter(
                requireContext(),
                new ArrayList<>(),
                selectedPositions -> {
                    pagerVM.setStepValid(2,!selectedPositions.isEmpty());

                }
        );


        binding.recVGenre.setAdapter(pickGenreRecItemAdapter);
        vm.loadGenres();
        vm.getGenresLiveData().observe(getViewLifecycleOwner(), items -> {
            PickItemList = items;
            pickGenreRecItemAdapter.submitList(items); // AsyncListDiffer will handle it

        });
    }





    private List<String> getSelectedGenres() {
        List<String> selected = new ArrayList<>();
        for (int pos : pickGenreRecItemAdapter.getSelectedPositions()) {
            selected.add(PickItemList.get(pos).getItemTitle());
        }
        return selected;
    }






    private void handleIncomingArguments() {
        if (getArguments() == null) return;

        String origin = getArguments().getString("origin");
        String filters = getArguments().getString("filters");

        if (origin != null) {
            Toast.makeText(requireContext(), "Origin: " + origin, Toast.LENGTH_SHORT).show();
        }

        if (filters != null) {
            Toast.makeText(requireContext(), "Filters: " + filters, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}