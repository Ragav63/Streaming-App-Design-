package com.example.streamingapp.presentation.view;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.data.model.ContentType;
import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.FragmentSeriesPlayerScreenBinding;
import com.example.streamingapp.presentation.adapter.GenreFilterAdapter;
import com.example.streamingapp.presentation.utils.Constants;
import com.example.streamingapp.presentation.utils.PipActionReceiver;
import com.example.streamingapp.presentation.utils.PlayerController;
import com.example.streamingapp.presentation.utils.PlayerUIHelper;
import com.example.streamingapp.presentation.viewmodel.PlayerViewModel;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeriesPlayerScreenFragment extends Fragment {

    private FragmentSeriesPlayerScreenBinding portraitBinding;

    private PlayerViewModel viewModel;
    private PlayerController playerController;
    private PlayerUIHelper uiHelper;
    private PipActionReceiver pipActionReceiver;

    // Arguments
    private Episode episode;
    private SeriesItems seriesItem;
    private long startPosition = 0L;
    private List<SeasonItems> seasonList;
    private GenreFilterAdapter genreFilterAdapter;
    private FullscreenSeriesPlayerDialog fullscreenDialog;

    private boolean isPrepared = false;
    private Player.Listener playerStateListener;
    private StreamingViewModel vm;


    public SeriesPlayerScreenFragment() { /* required empty */ }

    public static SeriesPlayerScreenFragment newInstance(Bundle args) {
        SeriesPlayerScreenFragment f = new SeriesPlayerScreenFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        portraitBinding = FragmentSeriesPlayerScreenBinding.inflate(inflater, container, false);
        return portraitBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        uiHelper = new PlayerUIHelper(requireContext(), getViewLifecycleOwner(), false);

        readArgs();

        setupPlayerController();
        bindUi();
        setupUiListeners();
        registerPipReceiver();

        // Setup season tabs if available
        if (seasonList != null && !seasonList.isEmpty()) {
            setupSeasonTabs();
            // Load initial season fragment based on current episode
            int seasonNumber = findSeasonNumberForEpisode(episode, seasonList);
            loadSeasonFragment(seasonNumber, episode != null ? episode.episodeNumber : 1);
        }

        // Auto-play when fragment visible
        if (playerController != null && !playerController.isPlaying()) {
            playerController.play();
            uiHelper.updatePlayButton(portraitBinding, true);
        }
    }

    private void readArgs() {
        Bundle args = getArguments();
        if (args == null) return;
        startPosition = args.getLong("currentProgress", 0L);
        episode = args.getParcelable("episode");
        seriesItem = args.getParcelable("seriesItem");


        if (seriesItem != null) {
            seasonList = seriesItem.getSeasons();
        }
    }

    private void setupPlayerController() {
        // reuse player if already in ViewModel
        ExoPlayer existing = viewModel.getExoPlayer();
        if (existing != null) {
            playerController = new PlayerController(requireContext(), existing);
            isPrepared = true;
        } else {
            playerController = new PlayerController(requireContext());
            viewModel.setExoPlayer(playerController.getPlayer());
        }

        // Disable default controller
        portraitBinding.videoView.setUseController(false);
        portraitBinding.videoView.setPlayer(playerController.getPlayer());

        // Setup player state listener for immediate updates
        setupPlayerStateListener();

        // If episode supplied, always set media item (reset to new episode)
        if (episode != null && episode.getUrl() != null) {
            try {
                playerController.setMediaItem(MediaItem.fromUri(Uri.parse(episode.getUrl())));
            } catch (Exception e) {
                // fall back to string uri
                playerController.setMediaItem(MediaItem.fromUri(episode.getUrl()));
            }
            if (!isPrepared) {
                playerController.prepare();
                isPrepared = true;
            }

            if (startPosition > 0) playerController.seekTo(startPosition);
            // autoplay (you requested)
            playerController.play();
            updatePlayButtonImmediately(true);

            // Start seekbar updates
            uiHelper.startSeekBarUpdates(portraitBinding, playerController, viewModel);
            uiHelper.scheduleHideControls(portraitBinding, 5000);
        }

        // disable default controller to use our custom controls
        portraitBinding.videoView.setUseController(false);
        portraitBinding.videoView.setPlayer(playerController.getPlayer());

        // Player listener for state changes
        playerController.getPlayer().addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    // Start seekbar updates when player is ready
                    uiHelper.startSeekBarUpdates(portraitBinding, playerController, viewModel);
                    updatePipActions();
                } else if (state == Player.STATE_ENDED) {
                    uiHelper.updatePlayButton(portraitBinding, false);
                    viewModel.updatePlaying(false);
                    updatePipActions();
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                uiHelper.updatePlayButton(portraitBinding, isPlaying);
                viewModel.updatePlaying(isPlaying);
                updatePipActions();
            }
        });

        // start seekbar updates and auto-hide
        uiHelper.startSeekBarUpdates(portraitBinding, playerController, viewModel);
        uiHelper.scheduleHideControls(portraitBinding, 5000);
    }

    private void setupPlayerStateListener() {
        playerStateListener = new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                // Update UI on main thread immediately
                requireActivity().runOnUiThread(() -> {
                    if (state == Player.STATE_READY) {
                        uiHelper.startSeekBarUpdates(portraitBinding, playerController, viewModel);
                        updatePipActions();

                        // Update play button based on actual player state
                        updatePlayButtonImmediately(playerController.isPlaying());
                    } else if (state == Player.STATE_ENDED) {
                        updatePlayButtonImmediately(false);
                        viewModel.updatePlaying(false);
                        updatePipActions();
                    }
                });
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // IMMEDIATE UI update
                requireActivity().runOnUiThread(() -> {
                    updatePlayButtonImmediately(isPlaying);
                    viewModel.updatePlaying(isPlaying);
                    updatePipActions();
                });
            }
        };

        playerController.addPlayerListener(playerStateListener);
    }

    // Helper method for immediate UI updates
    private void updatePlayButtonImmediately(boolean isPlaying) {
        if (portraitBinding != null) {
            int iconRes = isPlaying ?
                    android.R.drawable.ic_media_pause :
                    android.R.drawable.ic_media_play;
            portraitBinding.playIv.setImageResource(iconRes);
        }

        // Also update in fullscreen dialog if open
        if (fullscreenDialog != null && fullscreenDialog.isVisible()) {
            fullscreenDialog.updatePlayButton(isPlaying);
        }
    }

    private void bindUi() {
        if (seriesItem != null) {
            portraitBinding.titleTv.setText(seriesItem.getTitle());
            String rating = seriesItem.getImdb_rating();
            float imdb = Float.parseFloat(rating); // 0–10

            Drawable drawable = portraitBinding.starView.getDrawable();
            if (drawable instanceof LayerDrawable) {
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                Drawable progress = layerDrawable.findDrawableByLayerId(android.R.id.progress);

                if (progress instanceof ClipDrawable) {
                    // ClipDrawable level range: 0–10000
                    int level = (int) (imdb / 10f * 10000);
                    ((ClipDrawable) progress).setLevel(level);
                }
            }

            portraitBinding.ratingTv.setText(String.valueOf(imdb));

            int totalSeasons = seasonList.size();
            String genre = TextUtils.join(" • ", seriesItem.getGenres());

            portraitBinding.tvTimingGenre.setText(" · " +
                    genre
                    + " · " + totalSeasons + " Seasons"
            );

            portraitBinding.recVGenre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            genreFilterAdapter = new GenreFilterAdapter(
                    requireContext(),
                    new ArrayList<>(),
                    true,   // assign-only mode
                    null    // no selection callback needed
            );

            portraitBinding.recVGenre.setAdapter(genreFilterAdapter);

            genreFilterAdapter.submitList(
                    mapGenresToPickItems(seriesItem.getGenres())
            );
        }

        uiHelper.updatePlayButton(portraitBinding, playerController.isPlaying());
        uiHelper.updateDownloadButton(portraitBinding,
                viewModel.getPlayerState().getValue() != null && viewModel.getPlayerState().getValue().isDownloaded);
        uiHelper.updateFavouriteButton(portraitBinding,
                viewModel.getPlayerState().getValue() != null && viewModel.getPlayerState().getValue().isFavourite);
    }

    private List<PickItem> mapGenresToPickItems(List<String> genres) {
        List<PickItem> list = new ArrayList<>();
        if (genres == null) return list;

        for (String genre : genres) {
            list.add(new PickItem(0, genre)); // img defaults to 0
        }
        return list;
    }

    private void setupSeasonTabs() {
        if (seasonList == null || seasonList.isEmpty()) return;

        portraitBinding.tabLayout.removeAllTabs();

        for (SeasonItems season : seasonList) {
            portraitBinding.tabLayout.addTab(
                    portraitBinding.tabLayout.newTab()
                            .setText("Season " + season.getSeasonNumber())
            );
        }

        portraitBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int seasonNumber = tab.getPosition() + 1;
                loadSeasonFragment(seasonNumber, 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Select tab for current episode's season
        if (episode != null && seasonList != null) {
            int seasonNumber = findSeasonNumberForEpisode(episode, seasonList);
            int tabPosition = seasonNumber - 1;
            if (tabPosition >= 0 && tabPosition < portraitBinding.tabLayout.getTabCount()) {
                portraitBinding.tabLayout.selectTab(portraitBinding.tabLayout.getTabAt(tabPosition));
            }
        }
    }

    private int findSeasonNumberForEpisode(Episode episode, List<SeasonItems> seasonList) {
        if (episode == null || seasonList == null) return 1;

        for (SeasonItems season : seasonList) {
            if (season.getEpisodes() != null) {
                for (Episode ep : season.getEpisodes()) {
                    if (ep.getEpisodeNumber() == episode.getEpisodeNumber()) {
                        return season.getSeasonNumber();
                    }
                }
            }
        }
        return 1; // default fallback
    }

    private void setupUiListeners() {
        // Play/pause
        // Play/pause - use direct toggle
        portraitBinding.playIv.setOnClickListener(v -> {
            // IMMEDIATE UI feedback
            boolean wasPlaying = playerController.isPlaying();
            updatePlayButtonImmediately(!wasPlaying);

            // Toggle playback
            playerController.togglePlayPause();

            // Update ViewModel
            viewModel.updatePlaying(playerController.isPlaying());
            uiHelper.scheduleHideControls(portraitBinding, 5000);
        });

        // Seekbar
        portraitBinding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) playerController.seekTo(progress);
                uiHelper.updatePlayerTiming(portraitBinding, playerController.getCurrentPosition());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                uiHelper.cancelHideControls();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                uiHelper.scheduleHideControls(portraitBinding, 5000);
            }
        });

        portraitBinding.backwardIv.setOnClickListener(v -> {
            playerController.seekBackward(10000);
            uiHelper.scheduleHideControls(portraitBinding, 5000);
        });

        portraitBinding.forwardIv.setOnClickListener(v -> {
            playerController.seekForward(10000);
            uiHelper.scheduleHideControls(portraitBinding, 5000);
        });

        // Touch overlay: show controls and reset hide timer
        portraitBinding.touchOverlay.setOnClickListener(v -> {
            if (uiHelper.areControlsVisible(portraitBinding)) {
                uiHelper.hideControls(portraitBinding);
                uiHelper.cancelHideControls();
            } else {
                uiHelper.showControls(portraitBinding);
                uiHelper.scheduleHideControls(portraitBinding, 5000);
            }
        });

        portraitBinding.fullScreenIv.setOnClickListener(v -> openFullscreen());

        portraitBinding.minScreenIv.setOnClickListener(v -> {
            uiHelper.hideControls(portraitBinding);
            // delegate to host activity if it supports PIP
            if (requireActivity() instanceof HomeActivity) {
                ((HomeActivity) requireActivity()).enterPictureInPictureMode();
            } else {
                Toast.makeText(requireContext(), "PIP not available", Toast.LENGTH_SHORT).show();
            }
        });

        portraitBinding.shareIv.setOnClickListener(v -> {
            if (episode != null) uiHelper.shareVideo(requireContext(), episode.getUrl());
            uiHelper.scheduleHideControls(portraitBinding, 5000);
        });

        portraitBinding.settingsIv.setOnClickListener(v -> {
            uiHelper.showSettingsMenu(requireContext(), portraitBinding.settingsIv, playerController);
            uiHelper.scheduleHideControls(portraitBinding, 5000);
        });

        portraitBinding.downloadIv.setOnClickListener(v -> {
            viewModel.toggleDownloaded();
            boolean now = viewModel.getPlayerState().getValue() != null && viewModel.getPlayerState().getValue().isDownloaded;
            uiHelper.updateDownloadButton(portraitBinding, now);
            Toast.makeText(requireContext(), now ? "Added to downloads" : "Removed from downloads", Toast.LENGTH_SHORT).show();
            uiHelper.scheduleHideControls(portraitBinding, 5000);
        });

        portraitBinding.favIv.setOnClickListener(v -> {
            viewModel.toggleFavourite();
            boolean fav = viewModel.getPlayerState().getValue() != null && viewModel.getPlayerState().getValue().isFavourite;
            uiHelper.updateFavouriteButton(portraitBinding, fav);
            uiHelper.scheduleHideControls(portraitBinding, 5000);
        });
    }

    public void loadSeasonFragment(int seasonNumber, int episodeNumber) {
        Fragment fragment = SeasonFragment.newInstance(seasonNumber, episodeNumber, seriesItem, true, false);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout, fragment)
                .commit();
    }

    private void openFullscreen() {
        if (playerController == null) return;

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        if (fullscreenDialog == null) {
            fullscreenDialog = FullscreenSeriesPlayerDialog.newInstance();
            fullscreenDialog.setPlayerController(playerController);
            fullscreenDialog.setEpisode(episode);
            fullscreenDialog.setSeriesItem(seriesItem);
            fullscreenDialog.setSeasonList(seasonList);
            fullscreenDialog.setViewModel(viewModel);
            fullscreenDialog.setUiHelper(uiHelper);

            fullscreenDialog.setOnDismissListener(() -> {
                // restore portrait when dialog closes
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                if (portraitBinding != null && playerController != null) {
                    portraitBinding.videoView.setPlayer(playerController.getPlayer());
                    uiHelper.startSeekBarUpdates(portraitBinding, playerController, viewModel);
                    uiHelper.scheduleHideControls(portraitBinding, 5000);
                }
            });
        }

        portraitBinding.videoView.setPlayer(null);
        fullscreenDialog.show(requireActivity().getSupportFragmentManager(), "fullscreen_player");
    }

    // PIP functionality
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerPipReceiver() {
        pipActionReceiver = new PipActionReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_PLAY);
        filter.addAction(Constants.ACTION_PAUSE);

        Context applicationContext = requireContext().getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            applicationContext.registerReceiver(
                    pipActionReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
            );
        } else {
            applicationContext.registerReceiver(pipActionReceiver, filter);
        }
    }

    private void unregisterPipReceiver() {
        try {
            if (pipActionReceiver != null) {
                requireContext().getApplicationContext().unregisterReceiver(pipActionReceiver);
                pipActionReceiver = null;
            }
        } catch (Exception ignored) {}
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private ArrayList<RemoteAction> buildPipActions() {
        ArrayList<RemoteAction> actions = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || playerController == null) return actions;

        if (playerController.isPlaying()) {
            // Player is playing, show PAUSE button
            Intent pauseIntent = new Intent(Constants.ACTION_PAUSE);
            PendingIntent pausePending = PendingIntent.getBroadcast(
                    requireContext(),
                    1,
                    pauseIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            Icon pauseIcon = Icon.createWithResource(requireContext(), android.R.drawable.ic_media_pause);
            actions.add(new RemoteAction(pauseIcon, "Pause", "Pause", pausePending));
        } else {
            // Player is paused, show PLAY button
            Intent playIntent = new Intent(Constants.ACTION_PLAY);
            PendingIntent playPending = PendingIntent.getBroadcast(
                    requireContext(),
                    2,
                    playIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );
            Icon playIcon = Icon.createWithResource(requireContext(), android.R.drawable.ic_media_play);
            actions.add(new RemoteAction(playIcon, "Play", "Play", playPending));
        }

        return actions;
    }

    private void updatePipActions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                requireActivity().isInPictureInPictureMode()) {
            ArrayList<RemoteAction> actions = buildPipActions();
            PictureInPictureParams params = new PictureInPictureParams.Builder()
                    .setActions(actions)
                    .build();
            requireActivity().setPictureInPictureParams(params);
        }
    }

    public void onPlayActionFromActivity() {
        if (playerController != null) {
            playerController.play();
            uiHelper.updatePlayButton(portraitBinding, true);
            updatePipActions();
        }
    }

    public void onPauseActionFromActivity() {
        if (playerController != null) {
            playerController.pause();
            uiHelper.updatePlayButton(portraitBinding, false);
            updatePipActions();
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        uiHelper.cancelAll();
        if (playerController != null && !requireActivity().isInPictureInPictureMode()) {
            viewModel.updatePlaybackState(playerController.isPlaying(),
                    playerController.getCurrentPosition(),
                    playerController.getDuration());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Immediate UI update on resume
        if (playerController != null) {
            updatePlayButtonImmediately(playerController.isPlaying());
            uiHelper.startSeekBarUpdates(portraitBinding, playerController, viewModel);
            uiHelper.scheduleHideControls(portraitBinding, 5000);
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterPipReceiver();
        uiHelper.cleanup();
        // Clean up listener
        if (playerController != null && playerStateListener != null) {
            long watched = playerController.getCurrentPosition();
            long duration = playerController.getDuration();

            boolean fullyWatched = duration > 0 && watched >= (duration * 0.95);

            HistoryItems historyItem = new HistoryItems(
                    seriesItem.getId(),
                    seriesItem.getTitle(),
                    getCurrentTime(),
                    seriesItem.getPoster(),
                    episode.getUrl(),
                    duration,
                    watched,
                    fullyWatched,
                    ContentType.SERIES
            );

            vm.saveHistory(historyItem);
            playerController.removePlayerListener(playerStateListener);
            playerStateListener = null;
        }

        if (portraitBinding != null) {
            portraitBinding.videoView.setPlayer(null);

            // Only release player if not in PIP mode
            if (!requireActivity().isInPictureInPictureMode() && playerController != null) {
                playerController.getPlayer().release();
                playerController = null;
            }
        }
        portraitBinding = null;
    }

}
