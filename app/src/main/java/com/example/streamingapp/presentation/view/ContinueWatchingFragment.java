package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.streamingapp.databinding.FragmentContinueWatchingBinding;
import com.example.streamingapp.domain.repository.ActionType;
import com.example.streamingapp.domain.repository.ContinueWatchingItemClick;
import com.example.streamingapp.presentation.adapter.ContinueWatchingFragmentItemAdapter;
import com.example.streamingapp.data.model.ContinueWatchingItems;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.List;


public class ContinueWatchingFragment extends Fragment {
    private FragmentContinueWatchingBinding binding;
    private ContinueWatchingFragmentItemAdapter adapter;
    private StreamingViewModel vm;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentContinueWatchingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(
                requireActivity(),
                new StreamingViewModelFactory()
        ).get(StreamingViewModel.class);

        setupBackButton();
        setupRecycler();
    }


    private void setupBackButton() {
        binding.backIv.setOnClickListener(v ->
                Navigation.findNavController(v).popBackStack()
        );
    }


    private void setupRecycler() {
        List<ContinueWatchingItems> items = vm.getContinueWatchingItems();
        adapter = new ContinueWatchingFragmentItemAdapter((item, action) -> {
            if (action == ActionType.PLAY) {
                // handle play
            } else if (action == ActionType.REMOVE) {
                // handle remove
            }
        });

        binding.recVContinueWatching.setAdapter(adapter);
        adapter.submitList(items);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}