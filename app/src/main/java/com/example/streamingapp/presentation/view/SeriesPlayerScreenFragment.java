package com.example.streamingapp.presentation.view;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.FragmentSeriesPlayerScreenBinding;
import com.example.streamingapp.databinding.LandscapeSeriesPlayerScreenBinding;
import com.example.streamingapp.presentation.utils.PipActionReceiver;
import com.example.streamingapp.presentation.utils.PopupMenuHelper;
import com.example.streamingapp.presentation.utils.PlayerController;
import com.example.streamingapp.presentation.utils.PlayerUIHelper;
import com.example.streamingapp.presentation.viewmodel.PlayerViewModel;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

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

    // Fullscreen dialog (re-usable)
    private FullscreenPlayerDialog fullscreenDialog;

    private boolean isPrepared = false;
    private Player.Listener playerStateListener;

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
            portraitBinding.ratingTv.setText(seriesItem.getImdb_rating());
        }

        uiHelper.updatePlayButton(portraitBinding, playerController.isPlaying());
        uiHelper.updateDownloadButton(portraitBinding,
                viewModel.getPlayerState().getValue() != null && viewModel.getPlayerState().getValue().isDownloaded);
        uiHelper.updateFavouriteButton(portraitBinding,
                viewModel.getPlayerState().getValue() != null && viewModel.getPlayerState().getValue().isFavourite);
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
            fullscreenDialog = FullscreenPlayerDialog.newInstance();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterPipReceiver();
        uiHelper.cleanup();
        // Clean up listener
        if (playerController != null && playerStateListener != null) {
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

    // ---------------- Fullscreen DialogFragment (re-uses same player) ----------------
    public static class FullscreenPlayerDialog extends DialogFragment {
        private LandscapeSeriesPlayerScreenBinding binding;
        private PlayerController playerController;

        private Episode episode;
        private SeriesItems seriesItem;
        private List<SeasonItems> seasonList;
        private PlayerViewModel viewModel;
        private PlayerUIHelper uiHelper;
        private Runnable onDismissCallback;

        // Container ID for fragments within the dialog
        private static final int DIALOG_FRAGMENT_CONTAINER = R.id.seriesLFrameLayout;

        public static FullscreenPlayerDialog newInstance() { return new FullscreenPlayerDialog(); }
        public void setPlayerController(PlayerController controller) { this.playerController = controller; }
        public void setEpisode(Episode e) { this.episode = e; }
        public void setSeriesItem(SeriesItems s) { this.seriesItem = s; }
        public void setSeasonList(List<SeasonItems> list) { this.seasonList = list; }
        public void setViewModel(PlayerViewModel vm) { this.viewModel = vm; }
        public void setUiHelper(PlayerUIHelper helper) { this.uiHelper = helper; }
        public void setOnDismissListener(Runnable cb) { this.onDismissCallback = cb; }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            return dialog;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            try {
                binding = LandscapeSeriesPlayerScreenBinding.inflate(inflater, container, false);
            } catch (Exception ex) {
                Toast.makeText(requireContext(), "Landscape layout missing", Toast.LENGTH_LONG).show();
                dismissAllowingStateLoss();
                return null;
            }
            return binding.getRoot();
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog dlg = getDialog();
            if (dlg != null && dlg.getWindow() != null) {
                dlg.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dlg.getWindow().setBackgroundDrawable(null);
                dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }

            if (playerController != null && binding != null) {
                // Attach same player instance
                binding.videoView.setPlayer(playerController.getPlayer());
                binding.videoView.setUseController(false);

                // Setup landscape UI helper
                PlayerUIHelper landscapeUiHelper = new PlayerUIHelper(requireContext(), getViewLifecycleOwner(), true);
                landscapeUiHelper.startSeekBarUpdates(binding, playerController, viewModel);
                landscapeUiHelper.scheduleHideControls(binding, 5000);

                setupLandscapeUI(landscapeUiHelper);
            }
        }

        private void setupLandscapeUI(PlayerUIHelper uiHelper) {
            binding.linearFrame.setVisibility(View.GONE);

            binding.titleTv.setText(seriesItem.getTitle());

            // Play/pause with immediate feedback
            binding.playIv.setOnClickListener(v -> {
                boolean wasPlaying = playerController.isPlaying();

                // Immediate UI update
                int iconRes = wasPlaying ?
                        android.R.drawable.ic_media_play :
                        android.R.drawable.ic_media_pause;
                binding.playIv.setImageResource(iconRes);

                // Toggle playback
                if (wasPlaying) {
                    playerController.pause();
                } else {
                    playerController.play();
                }

                viewModel.updatePlaying(playerController.isPlaying());
                uiHelper.scheduleHideControls(binding, 5000);
            });

            // Seekbar
            binding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) playerController.seekTo(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    uiHelper.cancelHideControls();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    uiHelper.scheduleHideControls(binding, 5000);
                }
            });

            // Seek buttons
            binding.backwardIv.setOnClickListener(v -> {
                playerController.seekBackward(10000);
                uiHelper.scheduleHideControls(binding, 5000);
            });

            binding.forwardIv.setOnClickListener(v -> {
                playerController.seekForward(10000);
                uiHelper.scheduleHideControls(binding, 5000);
            });

            // Touch overlay
            binding.touchOverlay.setOnClickListener(v -> {
                if (binding.linearFrame != null && binding.linearFrame.getVisibility() == View.VISIBLE) {
                    return;
                }

                if (uiHelper.areControlsVisible(binding)) {
                    uiHelper.hideControls(binding);
                    uiHelper.cancelHideControls();
                } else {
                    uiHelper.showControls(binding);
                    uiHelper.scheduleHideControls(binding, 5000);
                }
            });

            // Exit fullscreen
            binding.fullScreenIv.setOnClickListener(v -> dismiss());

            // PIP
            binding.minScreenIv.setOnClickListener(v -> {
                uiHelper.hideControls(binding);
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).enterPictureInPictureMode();
                }
            });

            // Settings
            binding.settingsIv.setOnClickListener(v -> {
                PopupMenuHelper.showPlayerSettingsMenu(binding.settingsIv, requireContext(), playerController.getPlayer());
                uiHelper.scheduleHideControls(binding, 5000);
            });

            // List mode (season/episode list)
            binding.listMode.setOnClickListener(v -> {
                if (seasonList == null || seasonList.isEmpty()) return;

                // If controls are visible, hide them first
                if (uiHelper.areControlsVisible(binding)) {
                    uiHelper.hideControls(binding);
                    uiHelper.cancelHideControls();
                }

                // Show season/episode selection
                showSeasonSelectionDialog();

                // Disable overlay clicks while selection UI is visible
                if (binding.touchOverlay != null) {
                    binding.touchOverlay.setClickable(false);
                }
            });

            binding.closeIv.setOnClickListener(v -> {
                binding.linearFrame.setVisibility(View.GONE);
                if (binding.touchOverlay != null) binding.touchOverlay.setClickable(true);
            });

            // Initial UI state
            uiHelper.updatePlayButton(binding, playerController.isPlaying());
            uiHelper.updateDownloadButton(binding,
                    viewModel.getPlayerState().getValue() != null && viewModel.getPlayerState().getValue().isDownloaded);
            uiHelper.updateFavouriteButton(binding,
                    viewModel.getPlayerState().getValue() != null && viewModel.getPlayerState().getValue().isFavourite);
        }


        // Add this method to update play button from parent fragment
        public void updatePlayButton(boolean isPlaying) {
            if (binding != null) {
                int iconRes = isPlaying ?
                        android.R.drawable.ic_media_pause :
                        android.R.drawable.ic_media_play;
                binding.playIv.setImageResource(iconRes);
            }
        }

        private void showSeasonSelectionDialog() {
            binding.linearFrame.setVisibility(View.VISIBLE);

            if (seasonList == null || seasonList.isEmpty()) return;

            binding.tabLayout.removeAllTabs();

            for (SeasonItems season : seasonList) {
                binding.tabLayout.addTab(
                        binding.tabLayout.newTab()
                                .setText("Season " + season.getSeasonNumber())
                );
            }

            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int seasonNumber = tab.getPosition() + 1;
                    // CRITICAL FIX: Use the correct container ID for the dialog
                    loadSeasonFragment(seasonNumber, episode != null ? episode.episodeNumber : 1);
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
                if (tabPosition >= 0 && tabPosition < binding.tabLayout.getTabCount()) {
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(tabPosition));
                    // Load the fragment immediately
                    loadSeasonFragment(seasonNumber, episode.episodeNumber);
                }
            }
        }

        public void loadSeasonFragment(int seasonNumber, int episodeNumber) {
            // CRITICAL FIX: Use getChildFragmentManager() instead of requireActivity().getSupportFragmentManager()
            Fragment fragment = SeasonFragment.newInstance(seasonNumber, episodeNumber, seriesItem, false, true);

            // Clear any existing fragments from the dialog's container
            Fragment existingFragment = getChildFragmentManager().findFragmentById(DIALOG_FRAGMENT_CONTAINER);

            if (existingFragment != null) {
                getChildFragmentManager().beginTransaction()
                        .remove(existingFragment)
                        .commitNow();
            }

            // Add new fragment to the dialog's container
            getChildFragmentManager().beginTransaction()
                    .replace(DIALOG_FRAGMENT_CONTAINER, fragment)
                    .addToBackStack(null)
                    .commit();

            // Debug log
            Log.d("FullscreenDialog", "Loaded SeasonFragment for season " + seasonNumber + " in container: " + DIALOG_FRAGMENT_CONTAINER);
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

        @Override
        public void onDismiss(@NonNull android.content.DialogInterface dialog) {
            super.onDismiss(dialog);
            // detach player from dialog view to avoid leak
            if (binding != null) binding.videoView.setPlayer(null);
            if (onDismissCallback != null) onDismissCallback.run();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }
}
