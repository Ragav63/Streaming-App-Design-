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
    // 1. Declare the Handler and the Runnable as class members
    private Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable navigateRunnable = new Runnable() {
        @Override
        public void run() {
            // Check if the Fragment is still attached and its view is valid
            if (getView() != null) {
                LocalManager localManager = new LocalManager(requireContext());
                boolean loggedIn = localManager.isLoggedIn();

                // Use safe navigation with findNavController(getView()) or check isAdded()
                if (loggedIn) {
                    Navigation.findNavController(getView())
                            .navigate(R.id.homeFragment);
                } else {
                    Navigation.findNavController(getView())
                            .navigate(R.id.onBoardingFragment);
                }
            }
        }
    };

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

        // 2. Start the navigation flow ONLY here
        startDelayedNavigation();
    }

    // 3. Rename the function to clearly indicate its action
    private void startDelayedNavigation() {
        // Post the Runnable to the Handler
        handler.postDelayed(navigateRunnable, 3000);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 4. CRITICAL: Remove any pending callbacks when the view is destroyed
        handler.removeCallbacks(navigateRunnable);
        binding = null;
    }
}