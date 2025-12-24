package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.databinding.FragmentSplashScreenBinding;

@SuppressLint("CustomSplashScreen")
public class SplashScreenFragment extends Fragment {

    private FragmentSplashScreenBinding binding;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private static final long SPLASH_DURATION = 3000; // 3 seconds
    private long splashStartTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Track when splash started (SURVIVES screen off/on)
        splashStartTime = System.currentTimeMillis();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        long elapsed = System.currentTimeMillis() - splashStartTime;
        long remaining = SPLASH_DURATION - elapsed;

        if (remaining <= 0) {
            navigateNext();
        } else {
            handler.postDelayed(this::navigateNext, remaining);
        }
    }

    private void navigateNext() {
        if (!isAdded() || binding == null) return;

        LocalManager localManager = new LocalManager(requireContext());
        boolean loggedIn = localManager.isLoggedIn();

        if (loggedIn) {
            Navigation.findNavController(requireView())
                    .navigate(R.id.homeFragment);
        } else {
            Navigation.findNavController(requireView())
                    .navigate(R.id.onBoardingFragment);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up handler completely
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}