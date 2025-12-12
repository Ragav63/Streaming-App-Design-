package com.example.streamingapp.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private NavController navController;

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





}