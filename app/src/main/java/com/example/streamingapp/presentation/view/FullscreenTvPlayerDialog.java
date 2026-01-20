package com.example.streamingapp.presentation.view;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.data.model.TvChannelUiItem;
import com.example.streamingapp.databinding.LandscapeSeriesPlayerScreenBinding;
import com.example.streamingapp.databinding.LandscapeTvPlayerScreenBinding;
import com.example.streamingapp.presentation.utils.PlayerController;
import com.example.streamingapp.presentation.utils.PlayerUIHelper;
import com.example.streamingapp.presentation.utils.PopupMenuHelper;
import com.example.streamingapp.presentation.viewmodel.PlayerViewModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FullscreenTvPlayerDialog extends DialogFragment {

    private LandscapeTvPlayerScreenBinding binding;
    private PlayerController playerController;

    private List<TvChannelUiItem> tvChannels = new ArrayList<>();
    private int currentChannelIndex = 0;
    private PlayerViewModel viewModel;
    private PlayerUIHelper uiHelper;
    private Runnable onDismissCallback;

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isControlsVisible = false;
    private boolean isCurrentlyPlaying = false;

    public static FullscreenTvPlayerDialog newInstance() {
        return new FullscreenTvPlayerDialog();
    }

    public void setPlayerController(PlayerController controller) {
        this.playerController = controller;
        if (playerController != null) {
            this.isCurrentlyPlaying = playerController.isPlaying();
        }
    }

    public void setTvChannels(List<TvChannelUiItem> tvChannels) {
        this.tvChannels = tvChannels;
    }

    public void setChannelIndex(int currentChannelIndex) {
        this.currentChannelIndex = currentChannelIndex;
    }

    public void setViewModel(PlayerViewModel vm) {
        this.viewModel = vm;
    }

    public void setUiHelper(PlayerUIHelper helper) {
        this.uiHelper = helper;
        if (uiHelper != null) {
            this.uiHelper.setCurrentPlayState(isCurrentlyPlaying);
        }
    }



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
            binding = LandscapeTvPlayerScreenBinding.inflate(inflater, container, false);
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
        if (requireActivity() instanceof HomeActivity) {
            ((HomeActivity) requireActivity()).enterFullscreen();
        }

        if (playerController != null && binding != null) {
            // Attach same player instance
            binding.videoView.setPlayer(playerController.getPlayer());
            binding.videoView.setUseController(false);

            // Setup landscape UI helper
            PlayerUIHelper landscapeUiHelper = new PlayerUIHelper(requireContext(), getViewLifecycleOwner(), true);
            landscapeUiHelper.setCurrentPlayState(isCurrentlyPlaying);
            landscapeUiHelper.startSeekBarUpdates(binding, playerController, viewModel);
            landscapeUiHelper.scheduleHideControls(binding, 5000);

            setupLandscapeUI(landscapeUiHelper);

            initTvSelectionFragment();
        }
    }

    private void setupLandscapeUI(PlayerUIHelper uiHelper) {
        binding.linearFrame.setVisibility(View.GONE);

        if (tvChannels != null && currentChannelIndex < tvChannels.size()) {
            TvChannelUiItem channel = tvChannels.get(currentChannelIndex);
            binding.titleTv.setText(channel.getProgrammeName());
        }

        // Play/pause with immediate feedback
        binding.playIv.setOnClickListener(v -> {
            if (playerController != null) {
                // Toggle play state
                isCurrentlyPlaying = !isCurrentlyPlaying;
                uiHelper.setCurrentPlayState(isCurrentlyPlaying);

                // Use IMMEDIATE update (ignores seeking state)
                int iconRes = isCurrentlyPlaying ?
                        android.R.drawable.ic_media_pause :
                        android.R.drawable.ic_media_play;
                binding.playIv.setImageResource(iconRes);

                // Toggle playback
                if (isCurrentlyPlaying) {
                    playerController.play();
                } else {
                    playerController.pause();
                }

                viewModel.updatePlaying(playerController.isPlaying());
                uiHelper.scheduleHideControls(binding, 5000);
            }
        });

        // Seekbar
        // Seekbar
        binding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && playerController != null) {
                    long duration = playerController.getDuration();
                    // FIX: Calculate actual position from percentage
                    long position = (progress * duration) / 100;
                    playerController.seekTo(position);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Set seeking flag
                uiHelper.setSeeking(true);
                uiHelper.cancelHideControls();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Restore play button after delay
                handler.postDelayed(() -> {
                    uiHelper.restorePlayButtonAfterSeek(binding);
                }, 300);
                uiHelper.scheduleHideControls(binding, 5000);
            }
        });

        // Seek buttons - with seeking state management
        binding.backwardIv.setOnClickListener(v -> {
            if (playerController != null) {
                // Set seeking flag
                uiHelper.setSeeking(true);
                playerController.seekBackward(10000);

                // Reset seeking flag after delay
                handler.postDelayed(() -> {
                    uiHelper.restorePlayButtonAfterSeek(binding);
                }, 300);
            }
            showControlsImmediate();
            uiHelper.scheduleHideControls(binding, 5000);
        });

        binding.forwardIv.setOnClickListener(v -> {
            if (playerController != null) {
                // Set seeking flag
                uiHelper.setSeeking(true);
                playerController.seekForward(10000);

                // Reset seeking flag after delay
                handler.postDelayed(() -> {
                    uiHelper.restorePlayButtonAfterSeek(binding);
                }, 300);
            }
            showControlsImmediate();
            uiHelper.scheduleHideControls(binding, 5000);
        });

        // Touch overlay
        binding.listMode.setOnClickListener(v -> {
            if (binding.linearFrame != null && binding.linearFrame.getVisibility() == View.VISIBLE) {
                return;
            }

            if (isControlsVisible) {
                hideControlsImmediate();
            } else {
                showControlsImmediate();
                uiHelper.scheduleHideControls(binding, 5000);
            }
        });

        // Exit fullscreen
        binding.fullScreenIv.setOnClickListener(v -> dismiss());

        // PIP
        binding.minScreenIv.setOnClickListener(v -> {
            uiHelper.hideControls(binding);

            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity())
                        .enterPipMode(binding.videoView);
            }
        });


        // Settings
        binding.settingsIv.setOnClickListener(v -> {
            PopupMenuHelper.showPlayerSettingsMenu(binding.settingsIv, requireContext(), playerController.getPlayer());
            uiHelper.scheduleHideControls(binding, 5000);
        });

        // List mode (channel list)
        binding.listMode.setOnClickListener(v -> {
            if (tvChannels == null || tvChannels.isEmpty()) return;

            // If controls are visible, hide them first
            if (uiHelper.areControlsVisible(binding)) {
                uiHelper.hideControls(binding);
                uiHelper.cancelHideControls();
            }

            // Show channel selection
            showChannelSelection();

            // Disable overlay clicks while selection UI is visible
            if (binding.touchOverlay != null) {
                binding.touchOverlay.setClickable(false);
            }
        });

        binding.view.setOnClickListener(v -> {
            binding.linearFrame.setVisibility(View.GONE);
            if (binding.touchOverlay != null) binding.touchOverlay.setClickable(true);
            showControlsImmediate();
        });


        binding.touchOverlay.setOnClickListener(v->{
            if (uiHelper.areControlsVisible(binding)) {
                uiHelper.hideControls(binding);
                uiHelper.cancelHideControls();
            } else {
                uiHelper.showControls(binding);
                uiHelper.scheduleHideControls(binding, 5000);
            }
        });

        // Initial UI state
        updatePlayButtonImmediate(playerController != null && playerController.isPlaying());
    }

    private void showChannelSelection() {
        binding.linearFrame.setVisibility(View.VISIBLE);


    }

    private void initTvSelectionFragment() {
        if (binding == null) return;

        TvSelectionFragment tvSelectionFragment = new TvSelectionFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.tvFrameLayout, tvSelectionFragment);
        transaction.commit();
    }

    private void loadChannel(int index) {
        if (index < 0 || index >= tvChannels.size()) return;

        currentChannelIndex = index;

        TvChannelUiItem channel = tvChannels.get(index);
        String url = channel.getProgrammeUrl();

        if (url == null) return;

        ExoPlayer player = playerController.getPlayer();

        // If same channel & already prepared → DO NOTHING
        if (index == currentChannelIndex &&
                (player.getPlaybackState() == Player.STATE_READY ||
                        player.getPlaybackState() == Player.STATE_BUFFERING)) {
            return;
        }

        // If same media item → DO NOTHING
        MediaItem currentItem = player.getCurrentMediaItem();
        if (currentItem != null &&
                currentItem.localConfiguration != null &&
                url.equals(currentItem.localConfiguration.uri.toString())) {
            return;
        }

        currentChannelIndex = index;



        binding.liveTv.setText(
                channel.getProgrammeStatus().equalsIgnoreCase("live") ? "Live" : "To the Live"
        );

        playerController.setMediaItem(MediaItem.fromUri(url));
        playerController.prepare();
        playerController.play();

        uiHelper.updatePlayButton(binding, true);

        viewModel.updateState(new PlayerViewModel.PlayerState(
                channel.getProgrammeName(),
                channel.getProgrammeTiming(),
                1, 1, true, 0, 0, 1.0f, false, false
        ));
    }



    private void showControlsImmediate() {
        requireActivity().runOnUiThread(() -> {
            if (binding == null) return;

            isControlsVisible = true;
            uiHelper.showControls(binding);

            // Restore correct play button state
            if (uiHelper != null) {
                uiHelper.restorePlayButtonAfterSeek(binding);
            }
        });
    }

    private void hideControlsImmediate() {
        requireActivity().runOnUiThread(() -> {
            if (binding == null) return;

            isControlsVisible = false;
            uiHelper.hideControls(binding);
        });
    }

    // Add this method to update play button from parent fragment
    public void updatePlayButton(boolean isPlaying) {
        if (binding != null) {
            isCurrentlyPlaying = isPlaying;
            int iconRes = isPlaying ?
                    android.R.drawable.ic_media_pause :
                    android.R.drawable.ic_media_play;
            binding.playIv.setImageResource(iconRes);
        }
    }

    // Update play button immediately
    private void updatePlayButtonImmediate(boolean isPlaying) {
        if (binding != null) {
            isCurrentlyPlaying = isPlaying;
            int iconRes = isPlaying ?
                    android.R.drawable.ic_media_pause :
                    android.R.drawable.ic_media_play;
            binding.playIv.setImageResource(iconRes);
        }
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        // detach player from dialog view to avoid leak
        if (binding != null) binding.videoView.setPlayer(null);
        if (onDismissCallback != null) onDismissCallback.run();
        if (requireActivity() instanceof HomeActivity) {
            ((HomeActivity) requireActivity()).exitFullscreen();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
