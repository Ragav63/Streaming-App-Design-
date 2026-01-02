package com.example.streamingapp.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.ActivityHomeBinding;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private NavController navController;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding.bottomview.setOnApplyWindowInsetsListener((v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
            v.setPadding(0, 0, 0, bottomInset);
            return insets;
        });


        setupNavController();
    }

    private void setupNavController() {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        navController = navHostFragment.getNavController();

        binding.bottomview.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            int currentId = navController.getCurrentDestination() != null
                    ? navController.getCurrentDestination().getId()
                    : -1;

            if (itemId == currentId) return true;

            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .setPopUpTo(
                            navController.getGraph().getStartDestinationId(),
                            false
                    )
                    .build();

            Bundle bundle = null;

            if (itemId == R.id.searchFragment) {
                bundle = new Bundle();
                bundle.putString("source", "home_bottom_nav");
            }

            navController.navigate(itemId, bundle, navOptions);
            return true;
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            boolean showBottomNav =
                    id == R.id.homeFragment ||
                            id == R.id.searchFragment ||
                            id == R.id.tvFragment ||
                            id == R.id.favouriteFragment ||
                            id == R.id.accountFragment;

            binding.bottomview.setVisibility(showBottomNav ? View.VISIBLE : View.GONE);
        });

        updateBottomMenuForGuest();

    }

    private void updateBottomMenuForGuest() {
        boolean isGuest = LocalManager.isGuestSessionActive();

        // Account menu item
        binding.bottomview.getMenu()
                .findItem(R.id.accountFragment)
                .setVisible(!isGuest);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (LocalManager.isGuestSessionActive()) {
            updateBottomMenuForGuest();
            return;
        }

        // Guest expired → force login
        LocalManager.clearGuestSession();
        updateBottomMenuForGuest();
        navController.navigate(R.id.selectLoginActivity);
    }








}