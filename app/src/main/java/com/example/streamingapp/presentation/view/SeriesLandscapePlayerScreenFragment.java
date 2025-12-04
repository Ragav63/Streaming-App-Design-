package com.example.streamingapp.presentation.view;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSeriesLandscapePlayerScreenBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SeriesLandscapePlayerScreenFragment extends Fragment {
    private FragmentSeriesLandscapePlayerScreenBinding binding;

    private Handler handler = new Handler();
    private Runnable hideControlsRunnable;

    private String videoUriString;
    private int currentPosition;

    private List<SeriesItems> seriesItemsList;
    private SeriesItems seriesItems;

    private SeasonFragment seasonFragment;
    private boolean isDialogOpen = true;

    public SeriesLandscapePlayerScreenFragment() {}

    public static SeriesLandscapePlayerScreenFragment newInstance(String uri, int position, ArrayList<SeriesItems> list) {
        SeriesLandscapePlayerScreenFragment f = new SeriesLandscapePlayerScreenFragment();
        Bundle b = new Bundle();
        b.putString("VIDEO_URI", uri);
        b.putInt("CURRENT_POSITION", position);
        b.putParcelableArrayList("popularSeriesItemsList", list);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSeriesLandscapePlayerScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        extractArguments();

        initVideo();

        initListeners();

        setupTabs();

        hideControlsRunnable = this::hideControls;
    }

    private void extractArguments() {
        Bundle args = getArguments();
        if (args == null) return;

        videoUriString = args.getString("VIDEO_URI");
        currentPosition = args.getInt("CURRENT_POSITION", 0);
        seriesItems = args.getParcelable("seriesItem");
        seriesItemsList = args.getParcelableArrayList("popularSeriesItemsList");
    }

    private void initVideo() {

        binding.videoView.setOnPreparedListener(mp -> {
            binding.playerSBar.setMax(binding.videoView.getDuration());
            binding.videoView.seekTo(currentPosition);
            binding.videoView.start();
            updateSeek();
        });

        binding.videoView.setOnCompletionListener(mp ->
                binding.playIv.setImageResource(android.R.drawable.ic_media_play)
        );

        if (videoUriString != null) {
            Uri uri = Uri.parse(videoUriString);
            binding.videoView.setVideoURI(uri);
        }
    }

    private void initListeners() {

        binding.playIv.setOnClickListener(v -> {
            if (binding.videoView.isPlaying()) {
                binding.videoView.pause();
                binding.playIv.setImageResource(android.R.drawable.ic_media_play);
            } else {
                binding.videoView.start();
                binding.playIv.setImageResource(android.R.drawable.ic_media_pause);
                showControls();
                updateSeek();
            }
        });

        binding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) binding.videoView.seekTo(progress);
                updateTime();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
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

        binding.listMode.setOnClickListener(v -> {
            openSeasonDialog();
        });
    }

    private void setupTabs() {

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                seasonFragment = SeasonFragment.newInstance(1,seriesItems,seriesItemsList, false, true);
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.seriesLFrameLayout, seasonFragment)
                        .commit();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        binding.tabLayout.getTabAt(0).select();
    }

    private void openSeasonDialog() {
        isDialogOpen = false;
        binding.linearFrame.setVisibility(View.VISIBLE);

        seasonFragment = SeasonFragment.newInstance(1, seriesItems, seriesItemsList, false, true);
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
        binding.playerSBar.setProgress(binding.videoView.getCurrentPosition());
        updateTime();

        if (binding.videoView.isPlaying()) {
            handler.postDelayed(this::updateSeek, 1000);
        }
    }

    private void updateTime() {
        int pos = binding.videoView.getCurrentPosition();
        binding.playerTimingTv.setText(String.format("%02d:%02d:%02d",
                pos / 3600000, (pos / 60000) % 60, (pos / 1000) % 60));
    }

    private void hideControls() {
        binding.playerTimingTv.setVisibility(View.INVISIBLE);
        binding.minScreenIv.setVisibility(View.INVISIBLE);
        binding.sharePlayerIv.setVisibility(View.INVISIBLE);
        binding.settingsIv.setVisibility(View.INVISIBLE);
        binding.backwardIv.setVisibility(View.INVISIBLE);
        binding.forwardIv.setVisibility(View.INVISIBLE);
        binding.playIv.setVisibility(View.INVISIBLE);
        binding.playerSBar.setVisibility(View.INVISIBLE);
        binding.listMode.setVisibility(View.INVISIBLE);
    }

    private void showControls() {
        binding.playerTimingTv.setVisibility(View.VISIBLE);
        binding.minScreenIv.setVisibility(View.VISIBLE);
        binding.sharePlayerIv.setVisibility(View.VISIBLE);
        binding.settingsIv.setVisibility(View.VISIBLE);
        binding.backwardIv.setVisibility(View.VISIBLE);
        binding.forwardIv.setVisibility(View.VISIBLE);
        binding.playIv.setVisibility(View.VISIBLE);
        binding.playerSBar.setVisibility(View.VISIBLE);
        binding.listMode.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}