package com.example.streamingapp.presentation.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSplashScreenBinding;

public class SplashScreenFragment extends Fragment {

    private FragmentSplashScreenBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentSplashScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Navigation.findNavController(requireView())
                        .navigate(R.id.selectLoginActivity);
            }
        },3000);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}