package com.example.streamingapp.presentation.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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

        String src = getArguments().getString("imageSource");

        if (src == null) return;

        if (src.startsWith("http")) {
            Glide.with(this).load(src).into(binding.fullScreenImageView);
        } else if (src.startsWith("res://")) {
            int resId = Integer.parseInt(src.substring(6));
            binding.fullScreenImageView.setImageResource(resId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}