package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.databinding.FragmentAvatorBinding;
import com.example.streamingapp.presentation.adapter.PickAvatorAdapter;
import com.example.streamingapp.presentation.viewmodel.AvRecomPagerViewModel;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class AvatorFragment extends Fragment {

    private FragmentAvatorBinding binding;

    private PickAvatorAdapter pickAvatorAdapter;
    private StreamingViewModel vm;
    private NavController navController;
    private AvRecomPagerViewModel pagerVM;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAvatorBinding.inflate(inflater, container, false);
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



        binding.tvUserName.setText(LocalManager.loadUserName());

        setupRecycler();

        handleIncomingArguments();
    }

    private void setupRecycler() {


        binding.recVSelectAvator.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false));

        pickAvatorAdapter = new PickAvatorAdapter(
                requireContext(),
                new ArrayList<>(),
                item -> {
                    pagerVM.setStepValid(0, true);
                    setAvatar(item);
                }
        );


        binding.recVSelectAvator.setAdapter(pickAvatorAdapter);
        vm.loadAvators();
        vm.getAvatorLiveData().observe(getViewLifecycleOwner(), items -> {
            pickAvatorAdapter.submitList(items);

            // Now data exists
            PickItem selected = pickAvatorAdapter.getSelectedItem();
            setAvatar(selected);
        });


    }

    private void setAvatar(@Nullable PickItem item) {
        if (item == null) {
            // Optional: clear image or show placeholder
            binding.ivImage.setImageDrawable(null);
            return;
        }

        Glide.with(requireContext())
                .load(item.getItemImg())
                .into(binding.ivImage);
        binding.ivImage.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.bluemain)
        );
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