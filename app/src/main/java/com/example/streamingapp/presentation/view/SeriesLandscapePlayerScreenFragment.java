package com.example.streamingapp.presentation.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSeriesLandscapePlayerScreenBinding;
import com.example.streamingapp.presentation.utils.PopupMenuHelper;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SeriesLandscapePlayerScreenFragment extends Fragment {
    private FragmentSeriesLandscapePlayerScreenBinding binding;

    private Handler handler = new Handler();
    private Runnable hideControlsRunnable;
    private ExoPlayer exoPlayer;

    private String videoUriString;
    private Long currentPosition;

    private List<SeriesItems> seriesItemsList;
    private SeriesItems seriesItems;
    private Episode episode;
    private int seasonNumber = 1;
    private SeasonFragment seasonFragment;
    private boolean isDialogOpen = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSeriesLandscapePlayerScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        binding.linearFrame.setVisibility(View.GONE);

        extractArguments();

        initVideo();

        initListeners();

        setupTabs();

        hideControlsRunnable = this::hideControls;
    }

    private void extractArguments() {
        Bundle args = getArguments();
        if (args == null) return;

        currentPosition = args.getLong("currentProgress", 0l);
        seriesItems = args.getParcelable("seriesItem");
        episode = getArguments().getParcelable("episode");
        seriesItemsList = args.getParcelableArrayList("popularSeriesItemsList");

    }

    private void initVideo() {
        if (episode == null || episode.getUrl() == null) return;

        videoUriString = episode.getUrl();

        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        binding.videoView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(videoUriString);
        exoPlayer.setMediaItem(mediaItem);

        exoPlayer.prepare();
        exoPlayer.seekTo(currentPosition);
        exoPlayer.play();

        updateSeek(); // <-- REQUIRED

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    binding.playerSBar.setMax((int) exoPlayer.getDuration());
                    updateSeek();
                }
            }
        });

    }



    private void initListeners() {

        binding.playIv.setOnClickListener(v -> {
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
                binding.playIv.setImageResource(android.R.drawable.ic_media_play);
            } else {
                exoPlayer.play();
                binding.playIv.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        binding.videoCl.setOnClickListener(v -> {
            if (!isDialogOpen) return;

            if (binding.playIv.getVisibility() == View.VISIBLE) {
                hideControls();
            } else {
                showControls();
                handler.postDelayed(hideControlsRunnable, 7000);
            }
        });

        binding.listMode.setOnClickListener(v -> openSeasonDialog());

        binding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && exoPlayer != null) {
                    exoPlayer.seekTo(progress);
                }
                updateTime();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.backwardIv.setOnClickListener(v -> {
            if (exoPlayer != null) {
                long pos = exoPlayer.getCurrentPosition();
                long seekPos = Math.max(pos - 10000, 0); // 10 sec backward
                exoPlayer.seekTo(seekPos);
                updateSeek();
            }
        });

        binding.forwardIv.setOnClickListener(v -> {
            if (exoPlayer != null) {
                long pos = exoPlayer.getCurrentPosition();
                long dur = exoPlayer.getDuration();
                long seekPos = Math.min(pos + 10000, dur); // 10 sec forward
                exoPlayer.seekTo(seekPos);
                updateSeek();
            }
        });

        binding.fullScreenIv.setOnClickListener(v -> {
            if (exoPlayer == null) return;

            long currentProgress = exoPlayer.getCurrentPosition();

            Bundle bundle = new Bundle();
            bundle.putLong("currentProgress", currentProgress);
            bundle.putParcelable("episode", episode);
            bundle.putParcelable("seriesItem", seriesItems);
            bundle.putParcelableArrayList(
                    "popularSeriesItemsList",
                    (ArrayList<? extends Parcelable>) seriesItemsList
            );
            NavHostFragment.findNavController(this)
                    .navigate(R.id.seriesPlayerScreenActivity, bundle);
        });

        binding.settingsIv.setOnClickListener(v -> {
            PopupMenuHelper.showPlayerSettingsMenu(v, requireContext(), exoPlayer);
            hideControls();
        });


    }


    private void setupTabs() {
        binding.tabLayout.removeAllTabs();

        int seasonCount = seriesItems.getSeasons().size(); // must exist

        for (int i = 1; i <= seasonCount; i++) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Season " + i));
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                seasonNumber = tab.getPosition() + 1;
                seasonFragment = SeasonFragment.newInstance(
                        seasonNumber,
                        episode.episodeNumber,
                        seriesItems,
                        seriesItemsList,
                        false,
                        true
                );

                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.seriesLFrameLayout, seasonFragment)
                        .commit();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.tabLayout.getTabAt(0).select();
    }


    private void openSeasonDialog() {
        hideControls();
        isDialogOpen = false;
        binding.linearFrame.setVisibility(View.VISIBLE);

        seasonFragment = SeasonFragment.newInstance(
                seasonNumber,
                episode.episodeNumber,
                seriesItems,
                seriesItemsList,
                false,
                true
        );

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.seriesLFrameLayout, seasonFragment)
                .commit();

        binding.closeIv.setOnClickListener(v -> {
            getChildFragmentManager().beginTransaction()
                    .remove(seasonFragment)
                    .commit();

            binding.linearFrame.setVisibility(View.GONE);
            isDialogOpen = true;
            showControls();
        });
    }


    private void updateSeek() {

        if (binding == null || exoPlayer == null) return;

        if (exoPlayer == null) return;

        long pos = exoPlayer.getCurrentPosition();
        long dur = exoPlayer.getDuration();

        if (dur > 0) {
            binding.playerSBar.setMax((int) dur);
            binding.playerSBar.setProgress((int) pos);
        }

        updateTime();

        handler.postDelayed(this::updateSeek, 1000);
    }


    private void updateTime() {
        int pos = Math.toIntExact(exoPlayer.getCurrentPosition());
        binding.playerTimingTv.setText(String.format("%02d:%02d:%02d",
                pos / 3600000, (pos / 60000) % 60, (pos / 1000) % 60));
    }

    private void hideControls() {

        if (binding == null) return; // <-- absolutely needed

        binding.playerTimingTv.setVisibility(View.INVISIBLE);
        binding.minScreenIv.setVisibility(View.INVISIBLE);
        binding.sharePlayerIv.setVisibility(View.INVISIBLE);
        binding.settingsIv.setVisibility(View.INVISIBLE);
        binding.backwardIv.setVisibility(View.INVISIBLE);
        binding.fastBackwardRl.setVisibility(View.INVISIBLE);
        binding.forwardIv.setVisibility(View.INVISIBLE);
        binding.fastForwardRl.setVisibility(View.INVISIBLE);
        binding.playIv.setVisibility(View.INVISIBLE);
        binding.playerSBar.setVisibility(View.INVISIBLE);
        binding.listMode.setVisibility(View.INVISIBLE);
    }

    private void showControls() {

        if (binding == null) return; // <-- absolutely needed

        binding.playerTimingTv.setVisibility(View.VISIBLE);
        binding.minScreenIv.setVisibility(View.VISIBLE);
        binding.sharePlayerIv.setVisibility(View.VISIBLE);
        binding.settingsIv.setVisibility(View.VISIBLE);
        binding.backwardIv.setVisibility(View.VISIBLE);
        binding.fastBackwardRl.setVisibility(View.VISIBLE);
        binding.forwardIv.setVisibility(View.VISIBLE);
        binding.fastForwardRl.setVisibility(View.VISIBLE);
        binding.playIv.setVisibility(View.VISIBLE);
        binding.playerSBar.setVisibility(View.VISIBLE);
        binding.listMode.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        handler.removeCallbacksAndMessages(null);  // <-- required

        if (exoPlayer != null) {
            currentPosition = (Long) exoPlayer.getCurrentPosition();
            exoPlayer.release();
            exoPlayer = null;
        }

        binding = null; // <-- last
    }


}