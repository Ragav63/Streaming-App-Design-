package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.Programme;
import com.example.streamingapp.data.model.TvChannel;
import com.example.streamingapp.data.model.TvChannelUiItem;
import com.example.streamingapp.databinding.FragmentTvBinding;
import com.example.streamingapp.presentation.adapter.TvProgramRecItemAdapter;
import com.example.streamingapp.presentation.utils.PlayerController;
import com.example.streamingapp.presentation.utils.PlayerUIHelper;
import com.example.streamingapp.presentation.viewmodel.PlayerViewModel;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TvFragment extends Fragment {

    private FragmentTvBinding binding;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable hideControlsRunnable;
    private TvProgramRecItemAdapter tvProgramRecItemAdapter;
    private NavController navController;
    private StreamingViewModel vm;
    private PlayerViewModel playerViewModel;
    private PlayerController playerController;
    private PlayerUIHelper uiHelper;

    private boolean isControlsVisible = false;
    private Player.Listener playerStateListener;
    private List<TvChannelUiItem> tvChannels = new ArrayList<>();
    private int currentChannelIndex = 0;
    private boolean isCurrentlyPlaying = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTvBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        // Initialize PlayerViewModel (same as SeriesPlayerScreenFragment)
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        uiHelper = new PlayerUIHelper(requireContext(), getViewLifecycleOwner(), false);

        setupPlayerController();
        setupClickListeners();
        setupControls();
        initTvSelectionFragment();
        setupFragmentResultListener();

        if (tvProgramRecItemAdapter == null) {
            tvProgramRecItemAdapter = new TvProgramRecItemAdapter(this::onChannelSelected);
        }

        vm.loadTvItems();
        vm.getTvLiveData().observe(getViewLifecycleOwner(), channels -> {
            tvChannels = mapChannelsToUi(channels);
            tvProgramRecItemAdapter.submitList(tvChannels);

            if (!tvChannels.isEmpty()) {
                // Play first channel by default
                loadChannel(0);
            }
        });
    }

    private void setupPlayerController() {
        // Reuse player if already in ViewModel
        ExoPlayer existing = playerViewModel.getExoPlayer();
        if (existing != null) {
            playerController = new PlayerController(requireContext(), existing);
        } else {
            playerController = new PlayerController(requireContext());
            playerViewModel.setExoPlayer(playerController.getPlayer());
        }

        // Setup video view
        binding.videoView.setUseController(false);
        binding.videoView.setPlayer(playerController.getPlayer());

        // Setup player state listener for immediate updates
        setupPlayerStateListener();

        // Start seekbar updates
        uiHelper.startSeekBarUpdates(binding, playerController, playerViewModel);
        uiHelper.scheduleHideControls(binding, 5000);
        uiHelper.setCurrentPlayState(isCurrentlyPlaying);

    }

    private void setupPlayerStateListener() {
        playerStateListener = new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                requireActivity().runOnUiThread(() -> {
                    if (state == Player.STATE_READY) {
                        uiHelper.startSeekBarUpdates(binding, playerController, playerViewModel);
                        uiHelper.updatePlayButtonImmediate(binding, playerController.isPlaying());
                        isCurrentlyPlaying = playerController.isPlaying();

                    } else if (state == Player.STATE_ENDED) {
                        uiHelper.updatePlayButton(binding, false);
                        playerViewModel.updatePlaying(false);
                        isCurrentlyPlaying = false;
                    }
                });
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                requireActivity().runOnUiThread(() -> {
                    isCurrentlyPlaying = isPlaying;
                    uiHelper.setCurrentPlayState(isPlaying);
                    uiHelper.updatePlayButton(binding, isPlaying);
                    playerViewModel.updatePlaying(isPlaying);
                });
            }
        };

        playerController.addPlayerListener(playerStateListener);
    }

    // Helper method for immediate UI updates


    private void loadChannel(int index) {
        if (index < 0 || index >= tvChannels.size()) return;

        currentChannelIndex = index;
        TvChannelUiItem channel = tvChannels.get(index);

        // Update UI
        binding.liveTv.setText(channel.getProgrammeStatus().equalsIgnoreCase("live") ? "Live" : "To the Live");

        // Update player
        if (channel.getProgrammeUrl() != null) {
            Log.d("VideoValue", "Values i got from channel"+channel.getProgrammeUrl());
            playerController.setMediaItem(MediaItem.fromUri(channel.getProgrammeUrl()));
            playerController.prepare();
            playerController.play();
            uiHelper.updatePlayButton(binding, true);

            // Update ViewModel state
            playerViewModel.updateState(new PlayerViewModel.PlayerState(
                    channel.getProgrammeName(),
                    channel.getProgrammeTiming(),
                    1,
                    1,
                    true,
                    0,
                    0,
                    1.0f,
                    false,
                    false
            ));
        }
    }

    private void onChannelSelected(TvChannelUiItem item) {
        Toast.makeText(requireContext(), "Switching to: " + item.getProgrammeName(), Toast.LENGTH_SHORT).show();

        // Find index of selected channel
        int index = tvChannels.indexOf(item);
        if (index != -1) {
            loadChannel(index);
        }

        showControlsImmediate();
        scheduleHideControls(5000);
    }

    private void setupClickListeners() {
        if (binding == null) return;

        // Play/Pause with immediate UI feedback (same pattern as SeriesPlayerScreenFragment)
        binding.playIv.setOnClickListener(v -> {
            if (playerController != null) {
                isCurrentlyPlaying = !isCurrentlyPlaying;
                uiHelper.setCurrentPlayState(isCurrentlyPlaying);

                // Use IMMEDIATE update (ignores seeking state)
                uiHelper.updatePlayButtonImmediate(binding, isCurrentlyPlaying);

                // Toggle playback
                playerController.togglePlayPause();

                // Update ViewModel
                playerViewModel.updatePlaying(playerController.isPlaying());

                uiHelper.scheduleHideControls(binding, 5000);
            }
        });

        binding.liveTv.setOnClickListener(v -> {
            // "To the Live" functionality
            if ("To the Live".equals(binding.liveTv.getText().toString()) && playerController != null) {
                playerController.play();
                isCurrentlyPlaying = true;
                uiHelper.setCurrentPlayState(true);
                uiHelper.updatePlayButtonImmediate(binding, true);
                binding.liveTv.setText("Live");
                binding.liveTv.setBackgroundResource(R.drawable.lgblackcircle_bg);
            }
            showControlsImmediate();
            scheduleHideControls(5000);
        });

        binding.fastBackwardRl.setOnClickListener(v -> {
            if (playerController != null) {
                uiHelper.setSeeking(true);
                playerController.seekBackward(10000);
                // Reset seeking flag after delay
                handler.postDelayed(() -> {
                    uiHelper.restorePlayButtonAfterSeek(binding);
                }, 300);
            }
            showControlsImmediate();
            scheduleHideControls(5000);
        });

        binding.fastForwardRl.setOnClickListener(v -> {
            if (playerController != null) {
                uiHelper.setSeeking(true);
                playerController.seekForward(10000);

                // Reset seeking flag after delay
                handler.postDelayed(() -> {
                    uiHelper.restorePlayButtonAfterSeek(binding);
                }, 300);
            }
            showControlsImmediate();
            scheduleHideControls(5000);
        });

        binding.fullScreenIv.setOnClickListener(v -> openFullScreen());

        binding.settingsIv.setOnClickListener(v -> uiHelper.showSettingsMenu(requireContext(), binding.settingsIv, playerController ));

        // Touch controls (same pattern as SeriesPlayerScreenFragment)
        binding.videoCl.setOnClickListener(v -> toggleControlsVisibilityImmediate());
        binding.videoView.setOnClickListener(v -> toggleControlsVisibilityImmediate());

        // Touch overlay: show controls and reset hide timer
        binding.touchOverlay.setOnClickListener(v -> {
            if (uiHelper.areControlsVisible(binding)) {
                uiHelper.hideControls(binding);
                uiHelper.cancelHideControls();
            } else {
                uiHelper.showControls(binding);
                uiHelper.scheduleHideControls(binding, 5000);
            }
        });

        // SeekBar listener
        binding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && playerController != null) {
                    long duration = playerController.getDuration();
                    long position = (long) ((progress / 100.0) * duration);
                    playerController.seekTo(position);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                uiHelper.cancelHideControls();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                scheduleHideControls(5000);
            }
        });
    }

    private void setupControls() {
        hideControlsRunnable = this::hideControlsImmediate;
        hideControlsImmediate();
    }

    private void scheduleHideControls(long delayMs) {
        handler.removeCallbacks(hideControlsRunnable);
        handler.postDelayed(hideControlsRunnable, delayMs);
    }

    private void toggleControlsVisibilityImmediate() {
        if (binding == null) return;

        if (isControlsVisible) {
            hideControlsImmediate();
        } else {
            showControlsImmediate();
            scheduleHideControls(5000);
        }
    }

    private void showControlsImmediate() {
        requireActivity().runOnUiThread(() -> {
            if (binding == null) return;

            isControlsVisible = true;
            uiHelper.showControls(binding);

            uiHelper.restorePlayButtonAfterSeek(binding);
        });
    }

    private void hideControlsImmediate() {
        requireActivity().runOnUiThread(() -> {
            if (binding == null) return;

            isControlsVisible = false;
            uiHelper.hideControls(binding);
        });
    }

    // Channel mapping method (keep as is)
    public List<TvChannelUiItem> mapChannelsToUi(List<TvChannel> channels) {
        List<TvChannelUiItem> uiList = new ArrayList<>();

        for (TvChannel channel : channels) {
            if (channel.getProgrammes() != null && !channel.getProgrammes().isEmpty()) {
                Programme current = null;
                for (Programme p : channel.getProgrammes()) {
                    if ("live".equalsIgnoreCase(p.getStatus())) {
                        current = p;
                        break;
                    }
                }
                if (current == null) current = channel.getProgrammes().get(0);

                uiList.add(new TvChannelUiItem(
                        channel.getChannelLogo(),
                        channel.getChannelName(),
                        current.getName(),
                        current.getTiming(),
                        current.getUrl(),
                        current.getStatus()
                ));
            }
        }

        return uiList;
    }

    private void openFullScreen() {
        if (binding == null || playerController == null) return;

        Bundle bundle = new Bundle();
        bundle.putString("VIDEO_URI", "android.resource://" + getActivity().getPackageName() + "/" + R.raw.videohz);
        navController.navigate(R.id.action_tvFragment_to_tvLandscapeActivity, bundle);
    }

    private void initTvSelectionFragment() {
        if (binding == null) return;

        TvSelectionFragment tvSelectionFragment = new TvSelectionFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.tvFrameLayout, tvSelectionFragment);
        transaction.commit();
    }

    private void setupFragmentResultListener() {
        getParentFragmentManager().setFragmentResultListener("tv_landscape_result", this, (requestKey, result) -> {
            if (requestKey.equals("tv_landscape_result")) {
                String videoUri = result.getString("VIDEO_URI");
                int position = result.getInt("CURRENT_POSITION");

                Log.d("TvFragment", "Received result from landscape: " + videoUri + ", position: " + position);

                if (videoUri != null && binding != null && binding.videoView != null) {
                    // Update playback if needed
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.cancelAll();
        if (playerController != null) {
            playerViewModel.updatePlaybackState(
                    playerController.isPlaying(),
                    playerController.getCurrentPosition(),
                    playerController.getDuration()
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (playerController != null) {
            isCurrentlyPlaying = playerController.isPlaying();
            uiHelper.setCurrentPlayState(isCurrentlyPlaying);

            uiHelper.updatePlayButtonImmediate(binding, isCurrentlyPlaying);
            uiHelper.startSeekBarUpdates(binding, playerController, playerViewModel);
            uiHelper.scheduleHideControls(binding, 5000);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        uiHelper.cleanup();

        if (playerController != null && playerStateListener != null) {
            playerController.removePlayerListener(playerStateListener);
            playerStateListener = null;
        }

        if (binding != null) {
            binding.videoView.setPlayer(null);
        }

        // Don't release player here - let ViewModel manage it
        // This allows sharing player between fragments
        binding = null;
    }

    private void openSettingsDialog() {
        if (binding == null) return;

        final Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quality);

        View constraintLayout = dialog.findViewById(R.id.constraint);
        TextView qualityVal = dialog.findViewById(R.id.qualityVal);
        SeekBar qualitySbar = dialog.findViewById(R.id.qualitySeekbar);

        qualitySbar.setMax(100);

        qualitySbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String qualityText;
                if (progress < 25) {
                    qualityText = "Low (360p)";
                    if (playerController != null) {
                        // Adjust playback quality if supported
                    }
                } else if (progress < 50) {
                    qualityText = "Medium (480p)";
                } else if (progress < 75) {
                    qualityText = "High (720p)";
                } else {
                    qualityText = "HD (1080p)";
                }
                qualityVal.setText(qualityText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        GestureDetector gestureDetector = new GestureDetector(requireActivity(), new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        dialog.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });

        constraintLayout.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        scheduleHideControls(5000);
    }


}