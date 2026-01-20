package com.example.streamingapp.presentation.view;

import android.Manifest;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }


        binding.bottomview.setOnApplyWindowInsetsListener((v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
            v.setPadding(0, 0, 0, bottomInset);
            return insets;
        });


        setupNavController();
    }

    public void enterPipMode(@NonNull View videoView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        Rect sourceRect = new Rect();
        videoView.getGlobalVisibleRect(sourceRect);

        PictureInPictureParams.Builder builder =
                new PictureInPictureParams.Builder()
                        .setSourceRectHint(sourceRect);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setAutoEnterEnabled(true);
        }

        enterPictureInPictureMode(builder.build());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterFullscreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            exitFullscreen();
        }
    }

    public void enterFullscreen() {
        Window window = getWindow();

        WindowCompat.setDecorFitsSystemWindows(window, false);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());

        if (controller != null) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
    }

    public void exitFullscreen() {
        Window window = getWindow();

        WindowCompat.setDecorFitsSystemWindows(window, true);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());

        if (controller != null) {
            controller.show(WindowInsetsCompat.Type.systemBars());
        }
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
                            id == R.id.myListFragment ||
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



}