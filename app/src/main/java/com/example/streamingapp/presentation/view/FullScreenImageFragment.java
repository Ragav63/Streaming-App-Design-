package com.example.streamingapp.presentation.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentFullScreenImageBinding;

public class FullScreenImageFragment extends Fragment {

    private FragmentFullScreenImageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentFullScreenImageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.backIv.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(requireView());
            navController.navigateUp();
        });

        String src = getArguments().getString("imageResource");

        if (src == null) return;

        Glide.with(requireContext()).load(src).into(binding.fullScreenImageView);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}