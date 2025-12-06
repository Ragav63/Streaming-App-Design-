package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.streamingapp.data.model.DownloadItems;
import com.example.streamingapp.databinding.FragmentDownloadBinding;
import com.example.streamingapp.presentation.adapter.DownloadRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class DownloadFragment extends Fragment {
    private FragmentDownloadBinding binding;

    private StreamingViewModel vm;
    private DownloadRecItemAdapter downloadRecItemAdapter;
    private List<DownloadItems> downloadItemsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDownloadBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        setupUi();
        setupRecycler();

        return binding.getRoot();
    }

    private void setupUi() {
        binding.backIv.setOnClickListener(v ->{
                    NavHostFragment.findNavController(this).popBackStack();

        });
    }

    private void setupRecycler() {


        binding.recVDownload.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        downloadRecItemAdapter = new DownloadRecItemAdapter((item, actionType, position) -> {
            switch (actionType) {
                case PLAY:
                    // Handle play click
                    break;
                case REMOVE:
                    // Remove item
                    List<DownloadItems> currentList = new ArrayList<>(downloadRecItemAdapter.differ.getCurrentList());
                    currentList.remove(position);
                    downloadRecItemAdapter.submitList(currentList);
                    break;
            }
        });

        binding.recVDownload.setAdapter(downloadRecItemAdapter);
        vm.loadDownloads();
        vm.getDownloadLiveData().observe(getViewLifecycleOwner(), items -> {
            downloadItemsList = items;
            downloadRecItemAdapter.submitList(items);
        });
        binding.recVDownload.setHasFixedSize(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // mandatory to avoid memory leak
    }
}