package com.example.streamingapp.presentation.view;

import android.app.Dialog;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.LandscapeSeriesPlayerScreenBinding;
import com.example.streamingapp.presentation.utils.PlayerController;
import com.example.streamingapp.presentation.utils.PlayerUIHelper;
import com.example.streamingapp.presentation.utils.PopupMenuHelper;
import com.example.streamingapp.presentation.viewmodel.PlayerViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class FullscreenSeriesPlayerDialog extends DialogFragment {

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

    public static FullscreenSeriesPlayerDialog newInstance() { return new FullscreenSeriesPlayerDialog(); }
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
                ((HomeActivity) getActivity())
                        .enterPipMode(binding.videoView);
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
