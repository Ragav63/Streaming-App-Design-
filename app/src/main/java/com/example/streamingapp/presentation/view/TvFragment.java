package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

    private StreamingViewModel vm;
    private PlayerViewModel playerViewModel;
    private PlayerController playerController;
    private PlayerUIHelper uiHelper;

    private TvProgramRecItemAdapter tvProgramRecItemAdapter;
    private List<TvChannelUiItem> tvChannels = new ArrayList<>();

    private int channelIndexFromArgs = 0;
    private int currentChannelIndex = -1;

    private boolean initialLoadDone = false;
    private boolean isControlsVisible = false;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable hideControlsRunnable;

    private Player.Listener playerStateListener;
    private FullscreenTvPlayerDialog fullscreenDialog;

    // ----------------------------------------------------

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTvBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            channelIndexFromArgs = getArguments().getInt("channelIndex", 0);
        }

        vm = new ViewModelProvider(
                requireActivity(),
                new StreamingViewModelFactory()
        ).get(StreamingViewModel.class);

        playerViewModel = new ViewModelProvider(requireActivity())
                .get(PlayerViewModel.class);

        uiHelper = new PlayerUIHelper(requireContext(), getViewLifecycleOwner(), false);

        setupPlayer();
        setupControls();
        setupClickListeners();
        initTvSelectionFragment();

        tvProgramRecItemAdapter = new TvProgramRecItemAdapter(this::onChannelSelected);

        vm.loadTvItems();
        vm.getTvLiveData().observe(getViewLifecycleOwner(), channels -> {

            tvChannels = mapChannelsToUi(channels);
            tvProgramRecItemAdapter.submitList(tvChannels);

            if (tvChannels.isEmpty()) return;

            // ✅ LOAD ONLY ONCE
            if (!initialLoadDone) {
                int safeIndex = Math.min(channelIndexFromArgs, tvChannels.size() - 1);
                loadChannel(safeIndex);
                initialLoadDone = true;
            }
        });
    }

    // ----------------------------------------------------

    public List<TvChannelUiItem> mapChannelsToUi(List<TvChannel> channels) {
        List<TvChannelUiItem> list = new ArrayList<>();

        for (TvChannel channel : channels) {
            if (channel.getProgrammes() == null || channel.getProgrammes().isEmpty()) continue;

            Programme live = null;
            for (Programme p : channel.getProgrammes()) {
                if ("live".equalsIgnoreCase(p.getStatus())) {
                    live = p;
                    break;
                }
            }
            if (live == null) live = channel.getProgrammes().get(0);

            list.add(new TvChannelUiItem(
                    channel.getChannelLogo(),
                    channel.getChannelName(),
                    live.getName(),
                    live.getTiming(),
                    live.getUrl(),
                    live.getStatus()
            ));
        }
        return list;
    }

    // ----------------------------------------------------

    private void setupPlayer() {
        ExoPlayer existing = playerViewModel.getExoPlayer();

        if (existing != null) {
            playerController = new PlayerController(requireContext(), existing);
        } else {
            playerController = new PlayerController(requireContext());
            playerViewModel.setExoPlayer(playerController.getPlayer());
        }

        binding.videoView.setUseController(false);
        binding.videoView.setPlayer(playerController.getPlayer());

        setupPlayerStateListener();

        uiHelper.startSeekBarUpdates(binding, playerController, playerViewModel);
        uiHelper.scheduleHideControls(binding, 5000);
    }

    // ----------------------------------------------------

    private void setupPlayerStateListener() {
        playerStateListener = new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                uiHelper.updatePlayButton(binding, isPlaying);
                playerViewModel.updatePlaying(isPlaying);
            }
        };

        playerController.addPlayerListener(playerStateListener);
    }

    // ----------------------------------------------------

    private void loadChannel(int index) {
        if (index < 0 || index >= tvChannels.size()) return;
        if (index == currentChannelIndex) return;

        TvChannelUiItem channel = tvChannels.get(index);
        currentChannelIndex = index;

        playerController.setMediaItem(MediaItem.fromUri(channel.getProgrammeUrl()));
        playerController.prepare();
        playerController.play();

        binding.liveTv.setText(
                channel.getProgrammeStatus().equalsIgnoreCase("live")
                        ? "Live"
                        : "To the Live"
        );

        playerViewModel.updateState(new PlayerViewModel.PlayerState(
                channel.getProgrammeName(),
                channel.getProgrammeTiming(),
                1, 1, true,
                0, 0, 1f,
                false, false
        ));
    }

    // ----------------------------------------------------

    private void onChannelSelected(TvChannelUiItem item) {
        Toast.makeText(requireContext(), "Switching to: " + item.getProgrammeName(), Toast.LENGTH_SHORT).show();

        // Find index of selected channel
        int index = tvChannels.indexOf(item);
        if (index != -1) {
            loadChannel(index);
        }
        showControls();
    }

    // ----------------------------------------------------

    private void setupClickListeners() {

        binding.playIv.setOnClickListener(v -> {
            playerController.togglePlayPause();
            uiHelper.scheduleHideControls(binding, 5000);
        });

        binding.fullScreenIv.setOnClickListener(v -> openFullScreen());

        binding.videoCl.setOnClickListener(v -> toggleControls());
        binding.videoView.setOnClickListener(v -> toggleControls());
    }

    // ----------------------------------------------------

    private void setupControls() {
        hideControlsRunnable = this::hideControls;
        hideControls();
    }

    private void toggleControls() {
        if (isControlsVisible) hideControls();
        else showControls();
    }

    private void showControls() {
        isControlsVisible = true;
        uiHelper.showControls(binding);
        uiHelper.scheduleHideControls(binding, 5000);
    }

    private void hideControls() {
        isControlsVisible = false;
        uiHelper.hideControls(binding);
    }

    // ----------------------------------------------------

    private void openFullScreen() {
        if (fullscreenDialog != null) return;

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        if (fullscreenDialog == null) {
            fullscreenDialog = FullscreenTvPlayerDialog.newInstance();
            fullscreenDialog.setPlayerController(playerController);
            fullscreenDialog.setChannelIndex(currentChannelIndex);
            fullscreenDialog.setTvChannels(tvChannels);
            fullscreenDialog.setViewModel(playerViewModel);
            fullscreenDialog.setUiHelper(uiHelper);

            fullscreenDialog.setOnDismissListener(() -> {
                // restore portrait when dialog closes
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                if (binding != null && playerController != null) {
                    binding.videoView.setPlayer(playerController.getPlayer());
                    uiHelper.startSeekBarUpdates(binding, playerController, playerViewModel);
                    uiHelper.scheduleHideControls(binding, 5000);
                }
            });
        }

        binding.videoView.setPlayer(null);
        fullscreenDialog.show(requireActivity().getSupportFragmentManager(), "fullscreen_player");
    }

    private void initTvSelectionFragment() {
        if (binding == null) return;

        TvSelectionFragment tvSelectionFragment = new TvSelectionFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.tvFrameLayout, tvSelectionFragment);
        transaction.commit();
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


}