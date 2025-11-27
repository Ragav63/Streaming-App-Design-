package com.example.streamingapp.presentation.view;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSeriesPlayerScreenBinding;

import java.util.List;

public class SeriesPlayerScreenFragment extends Fragment {
    private FragmentSeriesPlayerScreenBinding binding;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private Runnable hideControlsRunnable;

    private List<SeriesItems> seriesItemsList;
    private int seriesImage;
    private String rating;
    private String title;
    private boolean isDownloaded = false;
    private boolean isFavourite = false;

    private static final int DEFAULT_TINT_COLOR = R.color.white;
    private static final int SELECTED_TINT_COLOR = R.color.bluemain;

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

        // load SeasonFragment
        loadSeasonFragment();
    }

    private void getDataFromBundle() {
        if (getArguments() == null) return;

        seriesImage = getArguments().getInt("imageResource", -1);
        rating = getArguments().getString("rating");
        title = getArguments().getString("title");
        seriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");

        if (seriesItemsList == null || seriesItemsList.isEmpty()) {
            Toast.makeText(requireContext(), "Series List Missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        binding.titleTv.setText(title);
        binding.ratingTv.setText(rating);
    }

    private void initVideoPlayer() {
        Uri videoUri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.videohz);
        binding.videoView.setVideoURI(videoUri);
        binding.videoView.start();

        binding.videoView.setOnPreparedListener(mp -> {
            binding.playerSBar.setMax(binding.videoView.getDuration());
            updateSeekBar();
        });

        binding.videoView.setOnCompletionListener(mp ->
                binding.playIv.setImageResource(android.R.drawable.ic_media_play)
        );

        hideControlsRunnable = this::hideControls;
    }

    private void initClickListeners() {

        binding.playIv.setOnClickListener(v -> {
            if (binding.videoView.isPlaying()) {
                binding.videoView.pause();
                binding.playIv.setImageResource(android.R.drawable.ic_media_play);
            } else {
                binding.videoView.start();
                binding.playIv.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        binding.backwardIv.setOnClickListener(v -> {
            int pos = binding.videoView.getCurrentPosition();
            binding.videoView.seekTo(Math.max(pos - 10000, 0));
        });

        binding.forwardIv.setOnClickListener(v -> {
            int pos = binding.videoView.getCurrentPosition();
            binding.videoView.seekTo(Math.min(pos + 10000, binding.videoView.getDuration()));
        });

        binding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) binding.videoView.seekTo(progress);
                updatePlayerTiming();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Back navigation
        binding.minScreenIv.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        // Share example
        binding.sharePlayerIv.setOnClickListener(v -> {
            Uri uri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.videohz);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("video/*");
            i.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(i, "Share Video"));
        });

        // Download
        binding.downloadIv.setOnClickListener(v -> {
            if (!isDownloaded) {
                isDownloaded = true;
                binding.downloadIv.setColorFilter(
                        ContextCompat.getColor(requireContext(), SELECTED_TINT_COLOR),
                        PorterDuff.Mode.SRC_IN
                );
                Toast.makeText(requireContext(), "Added to Download", Toast.LENGTH_SHORT).show();
            }
        });

        // Favourite
        binding.favIv.setOnClickListener(v -> {
            isFavourite = !isFavourite;
            binding.favIv.setColorFilter(
                    ContextCompat.getColor(requireContext(),
                            isFavourite ? SELECTED_TINT_COLOR : DEFAULT_TINT_COLOR),
                    PorterDuff.Mode.SRC_IN
            );
        });
    }

    private void updateSeekBar() {
        binding.playerSBar.setProgress(binding.videoView.getCurrentPosition());
        updatePlayerTiming();
        handler.postDelayed(updateSeekBarRunnable = this::updateSeekBar, 1000);
    }

    private void updatePlayerTiming() {
        int pos = binding.videoView.getCurrentPosition();
        binding.playerTimingTv.setText(String.format("%02d:%02d:%02d",
                (pos / 1000) / 3600,
                ((pos / 1000) % 3600) / 60,
                (pos / 1000) % 60));
    }

    private void hideControls() {
        binding.playerTimingTv.setVisibility(View.GONE);
        binding.playIv.setVisibility(View.GONE);
        binding.forwardIv.setVisibility(View.GONE);
        binding.backwardIv.setVisibility(View.GONE);
        binding.playerSBar.setVisibility(View.GONE);
    }

    private void showControls() {
        binding.playerTimingTv.setVisibility(View.VISIBLE);
        binding.playIv.setVisibility(View.VISIBLE);
        binding.forwardIv.setVisibility(View.VISIBLE);
        binding.backwardIv.setVisibility(View.VISIBLE);
        binding.playerSBar.setVisibility(View.VISIBLE);
    }

    private void loadSeasonFragment() {
        SeasonFragment fragment = SeasonFragment.newInstance(seriesItemsList, true, false);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout, fragment)
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateSeekBarRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSeekBar();
    }

}