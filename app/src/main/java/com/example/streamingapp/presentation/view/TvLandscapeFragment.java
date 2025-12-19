package com.example.streamingapp.presentation.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.Programme;
import com.example.streamingapp.data.model.TvChannel;
import com.example.streamingapp.data.model.TvChannelUiItem;
import com.example.streamingapp.databinding.FragmentTvLandscapeBinding;
import com.example.streamingapp.presentation.adapter.TvProgramRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class TvLandscapeFragment extends Fragment{

    private FragmentTvLandscapeBinding binding;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private Runnable hideControlsRunnable;
    private TvProgramRecItemAdapter tvProgramRecItemAdapter;
    private String videoUriString;
    private int currentPosition;
    private NavController navController;
    private StreamingViewModel vm;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTvLandscapeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        navController = Navigation.findNavController(view);
        setupArguments();
        setupVideoView();
        setupClickListeners();
        setupControls();
        if (tvProgramRecItemAdapter == null) {
            tvProgramRecItemAdapter =new TvProgramRecItemAdapter(item -> {
                Toast.makeText(requireContext(), "Currently Watching: " + item.getProgrammeName(), Toast.LENGTH_SHORT).show();
            });
        } else {
            vm.loadTvItems();
            vm.getTvLiveData().observe(getViewLifecycleOwner(), items ->{
                tvProgramRecItemAdapter.submitList(mapChannelsToUi(items));
            });
        }
    }


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
                if (current == null) current = channel.getProgrammes().get(0); // fallback

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


    private void setupArguments() {
        Bundle args = getArguments();
        if (args != null) {
            videoUriString = args.getString("VIDEO_URI");
            currentPosition = args.getInt("CURRENT_POSITION", 0);
        }
    }

    private void setupVideoView() {
        if (videoUriString != null) {
            Uri videoUri = Uri.parse(videoUriString);
            binding.videoView.setVideoURI(videoUri);
            binding.videoView.setOnPreparedListener(mp -> {
                binding.videoView.seekTo(currentPosition);
                binding.videoView.start();
            });
        }

        binding.videoView.setOnPreparedListener(mp -> {
            binding.playerSBar.setMax(binding.videoView.getDuration());
            updateSeekBar();
        });

        binding.videoView.setOnCompletionListener(mp ->
                binding.playIv.setImageResource(android.R.drawable.ic_media_play));
    }

    private void setupClickListeners() {
        binding.listMode.setOnClickListener(v -> showTvSelectionFragment());
        binding.shareIv.setOnClickListener(v -> shareVideo());
        binding.settingsIv.setOnClickListener(v -> openSettingsDialog());
        binding.playIv.setOnClickListener(v -> togglePlayPause());
        binding.liveTv.setOnClickListener(v -> goToLive());
        binding.fastBackwardRl.setOnClickListener(v -> seekBackward());
        binding.fastForwardRl.setOnClickListener(v -> seekForward());
        binding.fullScreenIv.setOnClickListener(v -> exitFullScreen());
        binding.videoCl.setOnClickListener(v -> toggleControlsVisibility());

        binding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    binding.videoView.seekTo(progress);
                }
                updatePlayerTiming();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupControls() {
        hideControlsRunnable = this::hideControls;
        hideControls();
    }

    private void shareVideo() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, videoUriString);
        startActivity(Intent.createChooser(shareIntent, "Share Video"));
    }

    private void togglePlayPause() {
        if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
            binding.liveTv.setText("To the Live");
            binding.liveTv.setBackgroundResource(R.drawable.lgtransparentwhitestroke_bg);
            binding.playIv.setImageResource(android.R.drawable.ic_media_play);
            handler.postDelayed(hideControlsRunnable, 5000);
        } else {
            binding.videoView.start();
            binding.playIv.setImageResource(android.R.drawable.ic_media_pause);
            binding.liveTv.setText("Live");
            binding.liveTv.setBackgroundResource(R.drawable.lgblackcircle_bg);
        }
    }

    private void goToLive() {
        if ("To the Live".equals(binding.liveTv.getText().toString())) {
            binding.videoView.start();
            binding.playIv.setImageResource(android.R.drawable.ic_media_pause);
            binding.liveTv.setText("Live");
            binding.liveTv.setBackgroundResource(R.drawable.lgblackcircle_bg);
            showControls();
            handler.postDelayed(hideControlsRunnable, 10000);
        }
    }

    private void seekBackward() {
        int currentPosition = binding.videoView.getCurrentPosition();
        binding.videoView.seekTo(Math.max(currentPosition - 10000, 0));
    }

    private void seekForward() {
        int currentPosition = binding.videoView.getCurrentPosition();
        binding.videoView.seekTo(Math.min(currentPosition + 10000, binding.videoView.getDuration()));
    }

    private void exitFullScreen() {
        Bundle result = new Bundle();
        result.putString("VIDEO_URI", videoUriString);
        result.putInt("CURRENT_POSITION", binding.videoView.getCurrentPosition());

        getParentFragmentManager().setFragmentResult("tv_landscape_result", result);
        navController.navigateUp();
    }

    private void toggleControlsVisibility() {
        if (binding.liveTv.getVisibility() == View.VISIBLE) {
            hideControls();
            handler.removeCallbacks(hideControlsRunnable);
        } else {
            showControls();
            handler.removeCallbacks(hideControlsRunnable);
            handler.postDelayed(hideControlsRunnable, 10000);
        }
    }

    private void showTvSelectionFragment() {
        TvSelectionFragment tvSelectionFragment = new TvSelectionFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.tvFrameLayout, tvSelectionFragment);
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_right
        );
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        setupSwipeToDismiss();
    }

    private void setupSwipeToDismiss() {
        GestureDetector gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX < 0) {
                        getChildFragmentManager().popBackStack();
                        binding.tvFrameLayout.startAnimation(
                                AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right)
                        );
                        return true;
                    }
                }
                return false;
            }
        });

        binding.tvFrameLayout.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void updateSeekBar() {
        binding.playerSBar.setProgress(binding.videoView.getCurrentPosition());
        updatePlayerTiming();
        handler.postDelayed(updateSeekBarRunnable = this::updateSeekBar, 1000);
    }

    private void updatePlayerTiming() {
        int currentPos = binding.videoView.getCurrentPosition();
        binding.playerTimingTv.setText(String.format("%02d:%02d:%02d",
                (currentPos / 1000) / 3600,
                ((currentPos / 1000) % 3600) / 60,
                (currentPos / 1000) % 60));
    }

    private void showControls() {
        binding.liveTv.setVisibility(View.VISIBLE);
        binding.playerTimingTv.setVisibility(View.VISIBLE);
        binding.minScreenIv.setVisibility(View.VISIBLE);
        binding.shareIv.setVisibility(View.VISIBLE);
        binding.settingsIv.setVisibility(View.VISIBLE);
        binding.fastBackwardRl.setVisibility(View.VISIBLE);
        binding.fastForwardRl.setVisibility(View.VISIBLE);
        binding.fastBackwardRl.setVisibility(View.VISIBLE);
        binding.playIv.setVisibility(View.VISIBLE);
        binding.fastForwardRl.setVisibility(View.VISIBLE);
        binding.fullScreenIv.setVisibility(View.VISIBLE);
        binding.playerSBar.setVisibility(View.VISIBLE);
        binding.listMode.setVisibility(View.VISIBLE);
    }

    private void hideControls() {
        binding.liveTv.setVisibility(View.INVISIBLE);
        binding.playerTimingTv.setVisibility(View.INVISIBLE);
        binding.minScreenIv.setVisibility(View.INVISIBLE);
        binding.shareIv.setVisibility(View.INVISIBLE);
        binding.settingsIv.setVisibility(View.INVISIBLE);
        binding.fastBackwardRl.setVisibility(View.INVISIBLE);
        binding.playIv.setVisibility(View.INVISIBLE);
        binding.fastForwardRl.setVisibility(View.INVISIBLE);
        binding.fastForwardRl.setVisibility(View.INVISIBLE);
        binding.fastBackwardRl.setVisibility(View.INVISIBLE);
        binding.fullScreenIv.setVisibility(View.INVISIBLE);
        binding.playerSBar.setVisibility(View.INVISIBLE);
        binding.listMode.setVisibility(View.INVISIBLE);
    }



    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateSeekBarRunnable);
        if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.videoView != null) {
            binding.videoView.seekTo(currentPosition);
            binding.videoView.start();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding.videoView != null) {
            outState.putInt("CURRENT_POSITION", binding.videoView.getCurrentPosition());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("CURRENT_POSITION");
            if (binding.videoView != null) {
                binding.videoView.seekTo(currentPosition);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateSeekBarRunnable);
        handler.removeCallbacks(hideControlsRunnable);
        binding = null;
    }

    private void openSettingsDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quality);

        ConstraintLayout constraintLayout = dialog.findViewById(R.id.constraint);
        TextView qualityVal = dialog.findViewById(R.id.qualityVal);
        SeekBar qualitySbar = dialog.findViewById(R.id.qualitySeekbar);

        qualitySbar.setMax(100);
        qualitySbar.setProgress(100);

        qualitySbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 25) {
                    qualityVal.setText("Low (360p)");
                } else if (progress < 50) {
                    qualityVal.setText("Medium (480p)");
                } else if (progress < 75) {
                    qualityVal.setText("High (720p)");
                } else {
                    qualityVal.setText("HD (1080p)");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        GestureDetector gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
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
    }
}