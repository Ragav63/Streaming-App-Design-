package com.example.streamingapp.presentation.view;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamingapp.databinding.FragmentPickVideoTypeBinding;
import com.example.streamingapp.presentation.adapter.PickVideoRecItemAdapter;
import com.example.streamingapp.data.model.PickVideoTypeRecItem;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class PickVideoTypeFragment extends Fragment {

    private FragmentPickVideoTypeBinding binding;
    private PickVideoRecItemAdapter pickVideoRecItemAdapter;
    private List<PickVideoTypeRecItem> videoTypeRecItemList;
    private StreamingViewModel vm;
    private NavController navController;

    @Nullable
    @Override
    public android.view.View onCreateView(
            @NonNull android.view.LayoutInflater inflater,
            @Nullable android.view.ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentPickVideoTypeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        setupRecycler();
        setupClicks();
        updateNextButtonAppearance();
    }

    private void setupRecycler() {


        pickVideoRecItemAdapter = new PickVideoRecItemAdapter(
                requireContext(),
                new ArrayList<>(),
                selectedPositions -> updateNextButtonAppearance() // Lambda selection listener
        );

        binding.recVPickVideoTypes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recVPickVideoTypes.setHasFixedSize(true);
        binding.recVPickVideoTypes.setAdapter(pickVideoRecItemAdapter);
        // Load video items from ViewModel
        vm.loadVideoTypeItems();
        vm.getVideoTypeLiveData().observe(getViewLifecycleOwner(), videoTypeItems -> {
            videoTypeRecItemList = videoTypeItems;
            pickVideoRecItemAdapter.submitList(videoTypeItems);

        });
    }

    private void setupClicks() {
        binding.backIv.setOnClickListener(v -> navController.popBackStack());

        binding.nextTv.setOnClickListener(v -> {
            if (pickVideoRecItemAdapter.getSelectedPositions().isEmpty()) {
                Toast.makeText(requireContext(), "Select at least 1.", Toast.LENGTH_SHORT).show();
                return;
            }

            handleNext();
        });
    }

    private void handleNext() {
        List<String> selectedTitles = getSelectedVideoTitles();

        Bundle args = getArguments();
        String origin = args != null ? args.getString("origin") : null;
        String login = args != null ? args.getString("login") : null;

        if ("origin".equals(origin)) {
            Bundle bundle = new Bundle();
            bundle.putString("origin", "origin");
            navController.navigate(R.id.pickGenresActivity, bundle);

        } else if ("login".equals(login)) {
            navController.navigate(R.id.pickGenresActivity);

        } else {
            // Send selected categories back to previous fragment
            Bundle result = new Bundle();
            result.putStringArrayList("selectedCategories", new ArrayList<>(selectedTitles));
            getParentFragmentManager().setFragmentResult("video_types_result", result);
            navController.popBackStack();
        }
    }

    private List<String> getSelectedVideoTitles() {
        List<String> selectedTitles = new ArrayList<>();
        for (int pos : pickVideoRecItemAdapter.getSelectedPositions()) {
            selectedTitles.add(videoTypeRecItemList.get(pos).getItemTitle());
        }
        return selectedTitles;
    }

    private void updateNextButtonAppearance() {
        boolean hasSelection = !pickVideoRecItemAdapter.getSelectedPositions().isEmpty();

        if (hasSelection) {
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}