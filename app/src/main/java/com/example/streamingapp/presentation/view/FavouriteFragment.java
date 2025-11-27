package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.databinding.FragmentFavouriteBinding;
import com.example.streamingapp.presentation.adapter.HistoryRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends Fragment {

    private FragmentFavouriteBinding binding;

    private HistoryRecItemAdapter historyRecItemAdapter;
    private List<HistoryItems> historyItemsList;

    private StreamingViewModel vm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        setupUI();

        return view;
    }

    private void setupUI() {

        // *** DOWNLOAD CLICK ***
        binding.downloadTv.setOnClickListener(v -> {
            Navigation.findNavController(requireView())
                    .navigate(R.id.downloadFragment);
        });

        // *** HISTORY RECYCLER ***
        binding.recVHistory.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        historyItemsList = vm.getHistoryItems();

        historyRecItemAdapter = new HistoryRecItemAdapter(src -> {
            Bundle b = new Bundle();
            b.putString("imageSource", src);

            Navigation.findNavController(requireView())
                    .navigate(R.id.fullScreenImageActivity, b);
        });

        binding.recVHistory.setAdapter(historyRecItemAdapter);
        historyRecItemAdapter.differ.submitList(historyItemsList);
        binding.recVHistory.setHasFixedSize(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}