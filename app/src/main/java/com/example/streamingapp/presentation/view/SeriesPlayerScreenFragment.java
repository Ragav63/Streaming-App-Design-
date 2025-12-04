package com.example.streamingapp.presentation.view;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSeriesPlayerScreenBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeriesPlayerScreenFragment extends Fragment {

    private FragmentSeriesPlayerScreenBinding binding;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private Runnable hideControlsRunnable;

    private List<SeriesItems> seriesItemsList;
    private SeriesItems seriesItems;
    private List<SeasonItems> seasonList;

    private Episode episode;
    private String rating;
    private String title;

    private boolean isDownloaded = false;
    private boolean isFavourite = false;

    private static final int DEFAULT_TINT_COLOR = R.color.white;
    private static final int SELECTED_TINT_COLOR = R.color.bluemain;

    public ExoPlayer exoPlayer; // Made public so HomeActivity can access

    // auto-hide delay (5 seconds)
    private static final long AUTO_HIDE_DELAY_MS = 5000L;

    // current playback speed for display/use
    private float currentPlaybackSpeed = 1f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSeriesPlayerScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        getDataFromBundle();
        initUI();
        initVideoPlayer();
        initClickListeners();
        loadSeasonFragment();
        registerWithHomeActivity();

    }

    private void getDataFromBundle() {
        if (getArguments() == null) return;

        episode = getArguments().getParcelable("episode");
        seriesItems = getArguments().getParcelable("seriesItem");
        seriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");

        if (seriesItemsList == null || seriesItemsList.isEmpty()) {
            Toast.makeText(requireContext(), "Series List Missing", Toast.LENGTH_SHORT).show();
        }
        seasonList = seriesItems.getSeasons();
        rating = seriesItems.getImdb_rating();
        title = seriesItems.getTitle();

        setupTabs(seasonList);
    }

    private void initUI() {
        binding.titleTv.setText(title);
        binding.ratingTv.setText(rating);
    }

    private void initVideoPlayer() {
        String url = episode.getUrl();

        exoPlayer = new ExoPlayer.Builder(requireContext()).build();

        // Attach player to StyledPlayerView (binding.videoView)
        binding.videoView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(url);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();

        // Sync seekbar when ready
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    long duration = exoPlayer.getDuration();
                    // guard against unknown duration
                    binding.playerSBar.setMax(duration > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) duration);
                    updateSeekBar();
                    // start auto-hide timer on ready
                    scheduleHideControls();
                } else if (state == Player.STATE_ENDED) {
                    binding.playIv.setImageResource(android.R.drawable.ic_media_play);
                    showControls(); // show controls at end
                }
            }
        });

        // Hide default player UI controls (if using StyledPlayerView) — ensure controller is disabled
        try {
            binding.videoView.setUseController(false);
        } catch (Exception ignored) {
            // if your view doesn't have setUseController, ignore
        }

        binding.touchOverlay.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (areControlsVisible()) {
                    hideControls();
                    cancelHideControls();
                } else {
                    showControls();
                }
            }
            return true;
        });



    }

    private void registerWithHomeActivity() {
        // Check if the activity is HomeActivity and register this fragment
        if (requireActivity() instanceof HomeActivity) {
            ((HomeActivity) requireActivity()).setBigPlayerFragment(this);
        }
    }

    private void initClickListeners() {

        binding.playIv.setOnClickListener(v -> {
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
                binding.playIv.setImageResource(android.R.drawable.ic_media_play);
                // keep controls visible when paused (cancel hide)
                cancelHideControls();
            } else {
                exoPlayer.play();
                binding.playIv.setImageResource(android.R.drawable.ic_media_pause);
                scheduleHideControls();
            }
        });

        binding.backwardIv.setOnClickListener(v -> {
            exoPlayer.seekTo(Math.max(exoPlayer.getCurrentPosition() - 10000, 0));
            scheduleHideControls();
        });

        binding.forwardIv.setOnClickListener(v -> {
            exoPlayer.seekTo(Math.min(exoPlayer.getCurrentPosition() + 10000, exoPlayer.getDuration()));
            scheduleHideControls();
        });

        binding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) exoPlayer.seekTo(progress);
                updatePlayerTiming();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {
                cancelHideControls();
            }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                scheduleHideControls();
            }
        });

        binding.minScreenIv.setOnClickListener(v -> {
            // Check if activity is HomeActivity before calling
            if (requireActivity() instanceof HomeActivity) {
                ((HomeActivity) requireActivity()).showMiniPlayer();
            }
        });

        binding.fullScreenIv.setOnClickListener(v -> {
            // Check if activity is HomeActivity before calling
            if (requireActivity() instanceof HomeActivity) {
                ((HomeActivity) requireActivity()).restoreFullPlayer();
            }
        });

        // Share example
        binding.shareIv.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, episode.getUrl());
            startActivity(Intent.createChooser(i, "Share Episode"));
            scheduleHideControls();
        });

        // Download toggle
        binding.downloadIv.setOnClickListener(v -> {
            if (!isDownloaded) {
                isDownloaded = true;
                binding.downloadIv.setColorFilter(
                        ContextCompat.getColor(requireContext(), SELECTED_TINT_COLOR)
                );
                Toast.makeText(requireContext(), "Added to Download", Toast.LENGTH_SHORT).show();
            }
            scheduleHideControls();
        });

        // Favourite toggle
        binding.favIv.setOnClickListener(v -> {
            isFavourite = !isFavourite;
            binding.favIv.setColorFilter(ContextCompat.getColor(requireContext(),
                    isFavourite ? SELECTED_TINT_COLOR : DEFAULT_TINT_COLOR));
            scheduleHideControls();
        });

        binding.settingsIv.setOnClickListener(v -> showSettingsMenu(v));
    }

    private void showSettingsMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchor, 0,
                0, R.style.PopupTransparent);
        popupMenu.getMenu().add("Speed");
        popupMenu.getMenu().add("Audio");
        forcePopupMenuTextWhite(popupMenu);

        popupMenu.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();

            if (title.equals("Speed")) {
                showSpeedMenu(anchor);
            } else if (title.equals("Audio")) {
                showAudioMenu(anchor);
            }

            return true;
        });

        popupMenu.show();
        scheduleHideControls();
    }

    private void showSpeedMenu(View anchor) {
        PopupMenu speedMenu =new PopupMenu(requireContext(), anchor, 0, 0, R.style.PopupTransparent);

        speedMenu.getMenu().add("0.25x");
        speedMenu.getMenu().add("0.5x");
        speedMenu.getMenu().add("0.75x");
        speedMenu.getMenu().add("Normal");
        speedMenu.getMenu().add("1.25x");
        speedMenu.getMenu().add("1.5x");
        speedMenu.getMenu().add("2x");
        forcePopupMenuTextWhite(speedMenu);

        speedMenu.setOnMenuItemClickListener(item -> {
            String speed = item.getTitle().toString();

            float speedValue = 1f;
            switch (speed) {
                case "0.25x": speedValue = 0.25f; break;
                case "0.5x": speedValue = 0.5f; break;
                case "0.75x": speedValue = 0.75f; break;
                case "1.25x": speedValue = 1.25f; break;
                case "1.5x": speedValue = 1.5f; break;
                case "2x": speedValue = 2f; break;
                case "Normal": speedValue = 1f; break;
            }

            currentPlaybackSpeed = speedValue;

            if (exoPlayer != null) {
                // THIS is the correct way to set playback speed
                exoPlayer.setPlaybackParameters(new PlaybackParameters(speedValue));
                Toast.makeText(requireContext(), String.format(Locale.getDefault(),"Speed: %sx", speedValue), Toast.LENGTH_SHORT).show();
            }

            scheduleHideControls();
            return true;
        });

        speedMenu.show();
    }

    private void showAudioMenu(View anchor) {
        PopupMenu audioMenu = new PopupMenu(requireContext(), anchor, 0, 0, R.style.PopupTransparent);

        audioMenu.getMenu().add("Auto");
        audioMenu.getMenu().add("Stereo");
        audioMenu.getMenu().add("0.17 Mbps");
        forcePopupMenuTextWhite(audioMenu);

        audioMenu.setOnMenuItemClickListener(item -> {
            String audio = item.getTitle().toString();

            Toast.makeText(requireContext(), "Selected: " + audio, Toast.LENGTH_SHORT).show();

            // Future expansion → actual audio track selection using TrackSelector

            scheduleHideControls();
            return true;
        });

        audioMenu.show();
    }

    private void forcePopupMenuTextWhite(PopupMenu menu) {
        menu.setOnDismissListener(null); // avoid leaks
        menu.show();
        for (int i = 0; i < menu.getMenu().size(); i++) {
            MenuItem item = menu.getMenu().getItem(i);
            SpannableString s = new SpannableString(item.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            item.setTitle(s);
        }
    }


    private void updateSeekBar() {
        if (exoPlayer == null) return;

        binding.playerSBar.setProgress((int) exoPlayer.getCurrentPosition());
        updatePlayerTiming();

        handler.postDelayed(updateSeekBarRunnable = this::updateSeekBar, 1000);
    }

    private void updatePlayerTiming() {
        long pos = exoPlayer.getCurrentPosition();
        binding.playerTimingTv.setText(String.format("%02d:%02d:%02d",
                (pos / 1000) / 3600,
                ((pos / 1000) % 3600) / 60,
                (pos / 1000) % 60));
    }

    // Schedules auto-hide after AUTO_HIDE_DELAY_MS
    private void scheduleHideControls() {
        cancelHideControls();
        hideControlsRunnable = this::hideControls;
        handler.postDelayed(hideControlsRunnable, AUTO_HIDE_DELAY_MS);
    }

    private void cancelHideControls() {
        if (hideControlsRunnable != null) {
            handler.removeCallbacks(hideControlsRunnable);
        }
    }

    private boolean areControlsVisible() {
        return binding.playIv.getVisibility() == View.VISIBLE;
    }

    private void hideControls() {
        binding.playerTimingTv.setVisibility(View.GONE);
        binding.playIv.setVisibility(View.GONE);
        binding.forwardIv.setVisibility(View.GONE);
        binding.backwardIv.setVisibility(View.GONE);
        binding.playerSBar.setVisibility(View.GONE);
        binding.fullScreenIv.setVisibility(View.GONE);
        binding.minScreenIv.setVisibility(View.GONE);
        binding.settingsIv.setVisibility(View.GONE);
        // optionally hide other overlays
    }

    private void showControls() {
        binding.playerTimingTv.setVisibility(View.VISIBLE);
        binding.playIv.setVisibility(View.VISIBLE);
        binding.forwardIv.setVisibility(View.VISIBLE);
        binding.backwardIv.setVisibility(View.VISIBLE);
        binding.playerSBar.setVisibility(View.VISIBLE);
        binding.fullScreenIv.setVisibility(View.VISIBLE);
        binding.minScreenIv.setVisibility(View.VISIBLE);
        binding.settingsIv.setVisibility(View.VISIBLE);
        // Keep controls visible for AUTO_HIDE_DELAY_MS after showing
        scheduleHideControls();
    }

    private void loadSeasonFragment() {
        Fragment fragment = SeasonFragment.newInstance(
                1,
                seriesItems,
                seriesItemsList,
                true,
                false
        );

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout, fragment)
                .commit();
    }

    private void setupTabs(List<SeasonItems> seasons) {

        // Remove all previous tabs
        binding.tabLayout.clearOnTabSelectedListeners();
        binding.tabLayout.removeAllTabs();

        // Add new tabs based on seasons
        for (SeasonItems item : seasons) {
            binding.tabLayout.addTab(
                    binding.tabLayout.newTab()
                            .setText("Season " + item.getSeasonNumber())
            );
        }

        // Listener
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = getFragmentForTab(tab.getPosition());

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.framelayout, fragment)
                        .commit();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Select first tab
        if (binding.tabLayout.getTabCount() > 0) {
            binding.tabLayout.getTabAt(0).select();
        }
    }

    private Fragment getFragmentForTab(int position) {
        int seasonNumber = position + 1; // seasons start from 1
        return SeasonFragment.newInstance(seasonNumber, seriesItems, seriesItemsList, false, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateSeekBarRunnable);
        cancelHideControls();
        if (exoPlayer != null) exoPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSeekBar();
        scheduleHideControls();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelHideControls();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
