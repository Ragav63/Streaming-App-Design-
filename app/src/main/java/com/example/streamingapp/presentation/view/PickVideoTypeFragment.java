package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.databinding.FragmentPickVideoTypeBinding;
import com.example.streamingapp.presentation.adapter.PickVideoRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.AvRecomPagerViewModel;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class PickVideoTypeFragment extends Fragment {

    private FragmentPickVideoTypeBinding binding;
    private PickVideoRecItemAdapter pickVideoRecItemAdapter;
    private List<PickItem> videoTypeRecItemList;
    private StreamingViewModel vm;
    private NavController navController;
    private AvRecomPagerViewModel pagerVM;


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

        navController = NavHostFragment.findNavController(this);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        pagerVM = new ViewModelProvider(requireActivity())
                .get(AvRecomPagerViewModel.class);

        setupRecycler();

    }

    private void setupRecycler() {


        pickVideoRecItemAdapter = new PickVideoRecItemAdapter(
                requireContext(),
                new ArrayList<>(),
                selectedPositions ->{
                    pagerVM.setStepValid(1, !selectedPositions.isEmpty());
                }
        );

        binding.recVPickVideoTypes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recVPickVideoTypes.setHasFixedSize(true);
        binding.recVPickVideoTypes.setAdapter(pickVideoRecItemAdapter);
        // Load video items from ViewModel
        vm.loadVideoTypeItems();
        vm.getVideoTypeLiveData().observe(getViewLifecycleOwner(), videoTypeItems -> {
            videoTypeRecItemList = videoTypeItems;
            pickVideoRecItemAdapter.submitList(videoTypeItems);

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
            pagerVM.moveToPage(2); // 0 → Avatar, 1 → Genres

        } else if ("login".equals(login)) {
            pagerVM.moveToPage(2); // 0 → Avatar, 1 → Genres

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




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}