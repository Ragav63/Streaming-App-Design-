package com.example.streamingapp.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.ActivityHomeBinding;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private NavController navController;
    private Fragment playerFragment;
    private Episode currentEpisode;
    private SeriesItems currentSeriesItem;
    private ArrayList<SeriesItems> currentSeriesItemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavController();
    }

    private void setupNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomview, navController);

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
    }



    public void setBigPlayerFragment(Fragment fragment) {
        this.playerFragment = (SeriesPlayerScreenFragment) fragment;
    }

     public void showMiniPlayer() {
        if (playerFragment == null) return;
        View playerView = playerFragment.getView();
        if (playerView == null) return;
         ((ViewGroup) playerView.getParent()).removeView(playerView);
         binding.miniPlayerContainer.setVisibility(View.VISIBLE);
        binding.miniPlayerContainer.addView(playerView);
    }

    public void restoreFullPlayer() {
        if (playerFragment == null) return;
        View playerView = playerFragment.getView(); if (playerView == null) return;
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        ViewGroup navContainer = navHostFragment.requireView().findViewById(R.id.playerContainer);
        navContainer.addView(playerView); binding.miniPlayerContainer.setVisibility(View.GONE);
    }







    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handle external navigation (e.g. navigate to FiltersFragment)
     */
    private void handleIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("navigate_to_filters", false)) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("selectedCategories", intent.getStringArrayListExtra("selectedCategories"));
            bundle.putStringArrayList("selectedGenres", intent.getStringArrayListExtra("selectedGenres"));

            navController.navigate(R.id.filtersFragment, bundle);
        }
    }
}