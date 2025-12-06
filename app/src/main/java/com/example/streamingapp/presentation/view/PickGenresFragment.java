package com.example.streamingapp.presentation.view;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.streamingapp.databinding.FragmentPickGenresBinding;
import com.example.streamingapp.presentation.adapter.PickGenreRecItemAdapter;
import com.example.streamingapp.data.model.PickGenreTypeRecItem;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class PickGenresFragment extends Fragment {

    private FragmentPickGenresBinding binding;

    private PickGenreRecItemAdapter pickGenreRecItemAdapter;
    private List<PickGenreTypeRecItem> pickGenreTypeRecItemList;

    private StreamingViewModel vm;
    private NavController navController;


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

        navController = Navigation.findNavController(view);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        setupRecycler();
        setupClicks();
        updateNextButtonAppearance();

        handleIncomingArguments();
    }


    private void setupRecycler() {


        binding.recVGenre.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        pickGenreRecItemAdapter = new PickGenreRecItemAdapter(
                requireContext(),
                new ArrayList<>(),
                selectedPositions -> {
                    // Called whenever selection changes
                    updateNextButtonAppearance();  // Update button UI
                    // Optionally do something with the positions
                    // e.g., Log.d("PickGenres", "Selected: " + selectedPositions);
                }
        );


        binding.recVGenre.setAdapter(pickGenreRecItemAdapter);
        vm.loadGenres();
        vm.getGenresLiveData().observe(getViewLifecycleOwner(), items -> {
            pickGenreTypeRecItemList = items;
            pickGenreRecItemAdapter.submitList(items); // AsyncListDiffer will handle it

        });
    }


    private void setupClicks() {

        binding.backIv.setOnClickListener(v -> navController.popBackStack());

        binding.nextTv.setOnClickListener(v -> {
            if (pickGenreRecItemAdapter.getSelectedPositions().isEmpty()) {
                Toast.makeText(requireContext(), "Select at least one.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> selectedGenres = getSelectedGenres();

            String origin = getArguments() != null ? getArguments().getString("origin") : null;
            String filters = getArguments() != null ? getArguments().getString("filters") : null;

            if ("settings".equals(origin)) {
                navController.navigate(R.id.settingsActivity);

            } else if ("filters".equals(filters)) {

                Bundle result = new Bundle();
                result.putStringArrayList("selectedGenres", new ArrayList<>(selectedGenres));

                getParentFragmentManager().setFragmentResult("genres_result", result);
                navController.popBackStack();

            } else {
                navController.navigate(R.id.homeFragment);
            }
        });
    }


    private List<String> getSelectedGenres() {
        List<String> selected = new ArrayList<>();
        for (int pos : pickGenreRecItemAdapter.getSelectedPositions()) {
            selected.add(pickGenreTypeRecItemList.get(pos).getItemTitle());
        }
        return selected;
    }


    private void updateNextButtonAppearance() {
        boolean hasAny = !pickGenreRecItemAdapter.getSelectedPositions().isEmpty();

        if (hasAny) {
            binding.nextTv.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.bluemain)
            ));
            binding.nextTv.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
            binding.nextTv.setText("Next");
        } else {
            binding.nextTv.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.white)
            ));
            binding.nextTv.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
            binding.nextTv.setText("Select at Least 1");
        }
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