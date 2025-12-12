package com.example.streamingapp.presentation.utils;

import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Rational;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.databinding.FragmentSeriesPlayerScreenBinding;
import com.example.streamingapp.databinding.LandscapeSeriesPlayerScreenBinding;
import com.example.streamingapp.presentation.viewmodel.PlayerViewModel;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Locale;

public class PlayerUIHelper {
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable hideControlsRunnable;
    private Runnable updateSeekBarRunnable;
    private boolean isLandscape;
    private Context context;
    private final LifecycleOwner lifecycleOwner;


    public interface SeekBarListener {
        void onSeekStarted();
        void onSeekChanged(int progress);
        void onSeekCompleted();
    }

    public PlayerUIHelper(Context context, LifecycleOwner owner, boolean isLandscape) {
        this.context = context;
        this.lifecycleOwner = owner;
        this.isLandscape = isLandscape;
    }



    public void setupPortraitUI(FragmentSeriesPlayerScreenBinding binding,
                                PlayerViewModel viewModel) {
        // Observe ViewModel for UI updates
        viewModel.getPlayerState().observe(lifecycleOwner, state -> {
            if (state != null) {
                binding.titleTv.setText(state.title);
                binding.ratingTv.setText(state.rating);
                updatePlayButton(binding, state.isPlaying);
                updateFavouriteButton(binding, state.isFavourite);
                updateDownloadButton(binding, state.isDownloaded);
            }
        });
    }

    public void setupLandscapeUI(LandscapeSeriesPlayerScreenBinding binding,
                                 PlayerViewModel viewModel,
                                 Runnable onListModeClicked) {
        viewModel.getPlayerState().observe(lifecycleOwner, state -> {
            if (state != null) {
                binding.titleTv.setText(state.title);
//                binding.ratingTv.setText(state.rating);
                updatePlayButton(binding, state.isPlaying);
                updateFavouriteButton(binding, state.isFavourite);
                updateDownloadButton(binding, state.isDownloaded);
            }
        });

        binding.listMode.setOnClickListener(v -> onListModeClicked.run());
    }

    public void setupSeasonTabs(TabLayout tabLayout, List<SeasonItems> seasons,
                                TabLayout.OnTabSelectedListener listener) {
        tabLayout.removeAllTabs();

        for (SeasonItems season : seasons) {
            tabLayout.addTab(tabLayout.newTab().setText("Season " + season.getSeasonNumber()));
        }

        tabLayout.addOnTabSelectedListener(listener);
    }

    public void startSeekBarUpdates(Object binding, PlayerController playerController,
                                    PlayerViewModel viewModel) {
        if (updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }

        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (playerController != null) {
                    long position = playerController.getCurrentPosition();
                    long duration = playerController.getDuration();

                    if (binding instanceof FragmentSeriesPlayerScreenBinding) {
                        FragmentSeriesPlayerScreenBinding portrait =
                                (FragmentSeriesPlayerScreenBinding) binding;
                        portrait.playerSBar.setMax((int) duration);
                        portrait.playerSBar.setProgress((int) position);
                        portrait.playerTimingTv.setText(formatTime(position));
                    } else if (binding instanceof LandscapeSeriesPlayerScreenBinding) {
                        LandscapeSeriesPlayerScreenBinding landscape =
                                (LandscapeSeriesPlayerScreenBinding) binding;
                        landscape.playerSBar.setMax((int) duration);
                        landscape.playerSBar.setProgress((int) position);
                        landscape.playerTimingTv.setText(formatTime(position));
                    }

                    // Update ViewModel
                    viewModel.updatePlaybackState(
                            playerController.isPlaying(),
                            position,
                            duration
                    );

                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.post(updateSeekBarRunnable);
    }

    // Control visibility methods
    public void scheduleHideControls(Object binding, long delayMs) {
        cancelHideControls();
        hideControlsRunnable = () -> hideControls(binding);
        handler.postDelayed(hideControlsRunnable, delayMs);
    }

    public void cancelHideControls() {
        if (hideControlsRunnable != null) {
            handler.removeCallbacks(hideControlsRunnable);
        }
    }

    public boolean areControlsVisible(Object binding) {
        if (binding instanceof FragmentSeriesPlayerScreenBinding) {
            FragmentSeriesPlayerScreenBinding portrait = (FragmentSeriesPlayerScreenBinding) binding;
            return portrait.playIv.getVisibility() == View.VISIBLE;
        } else if (binding instanceof LandscapeSeriesPlayerScreenBinding) {
            LandscapeSeriesPlayerScreenBinding landscape =
                    (LandscapeSeriesPlayerScreenBinding) binding;
            return landscape.playIv.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    public void hideControls(Object binding) {
        if (binding instanceof FragmentSeriesPlayerScreenBinding) {
            FragmentSeriesPlayerScreenBinding portrait = (FragmentSeriesPlayerScreenBinding) binding;
            portrait.playerTimingTv.setVisibility(View.GONE);
            portrait.playIv.setVisibility(View.GONE);
            portrait.forwardIv.setVisibility(View.GONE);
            portrait.backwardIv.setVisibility(View.GONE);
            portrait.playerSBar.setVisibility(View.GONE);
            portrait.fullScreenIv.setVisibility(View.GONE);
            portrait.minScreenIv.setVisibility(View.GONE);
            portrait.settingsIv.setVisibility(View.GONE);
        } else if (binding instanceof LandscapeSeriesPlayerScreenBinding) {
            LandscapeSeriesPlayerScreenBinding landscape =
                    (LandscapeSeriesPlayerScreenBinding) binding;
            landscape.clTopBar.setVisibility(View.GONE);
            landscape.playerTimingTv.setVisibility(View.GONE);
            landscape.playIv.setVisibility(View.GONE);
            landscape.forwardIv.setVisibility(View.GONE);
            landscape.backwardIv.setVisibility(View.GONE);
            landscape.playerSBar.setVisibility(View.GONE);
            landscape.fullScreenIv.setVisibility(View.GONE);
            landscape.minScreenIv.setVisibility(View.GONE);
            landscape.settingsIv.setVisibility(View.GONE);
            landscape.listMode.setVisibility(View.GONE);
        }
    }

    public void showControls(Object binding) {
        if (binding instanceof FragmentSeriesPlayerScreenBinding) {
            FragmentSeriesPlayerScreenBinding portrait = (FragmentSeriesPlayerScreenBinding) binding;
            portrait.playerTimingTv.setVisibility(View.VISIBLE);
            portrait.playIv.setVisibility(View.VISIBLE);
            portrait.forwardIv.setVisibility(View.VISIBLE);
            portrait.backwardIv.setVisibility(View.VISIBLE);
            portrait.playerSBar.setVisibility(View.VISIBLE);
            portrait.fullScreenIv.setVisibility(View.VISIBLE);
            portrait.minScreenIv.setVisibility(View.VISIBLE);
            portrait.settingsIv.setVisibility(View.VISIBLE);
        } else if (binding instanceof LandscapeSeriesPlayerScreenBinding) {
            LandscapeSeriesPlayerScreenBinding landscape =
                    (LandscapeSeriesPlayerScreenBinding) binding;
            landscape.clTopBar.setVisibility(View.VISIBLE);
            landscape.playerTimingTv.setVisibility(View.VISIBLE);
            landscape.playIv.setVisibility(View.VISIBLE);
            landscape.forwardIv.setVisibility(View.VISIBLE);
            landscape.backwardIv.setVisibility(View.VISIBLE);
            landscape.playerSBar.setVisibility(View.VISIBLE);
            landscape.fullScreenIv.setVisibility(View.VISIBLE);
            landscape.minScreenIv.setVisibility(View.VISIBLE);
            landscape.settingsIv.setVisibility(View.VISIBLE);
            landscape.listMode.setVisibility(View.VISIBLE);
        }
    }

    // Button setup methods
    public void setupPlayButton(View rootView, View.OnClickListener listener) {
        View playButton = rootView.findViewById(R.id.playIv);
        if (playButton != null) {
            playButton.setOnClickListener(listener);
        }
    }

    public void setupSeekButtons(View rootView, View.OnClickListener backwardListener,
                                 View.OnClickListener forwardListener) {
        View backwardButton = rootView.findViewById(R.id.backwardIv);
        View forwardButton = rootView.findViewById(R.id.forwardIv);

        if (backwardButton != null) {
            backwardButton.setOnClickListener(backwardListener);
        }
        if (forwardButton != null) {
            forwardButton.setOnClickListener(forwardListener);
        }
    }

    public void setupFullscreenButton(View rootView, View.OnClickListener listener) {
        View button = rootView.findViewById(R.id.fullScreenIv);
        if (button != null) {
            button.setOnClickListener(listener);
        }
    }

    public void setupPipButton(View rootView, View.OnClickListener listener) {
        View button = rootView.findViewById(R.id.minScreenIv);
        if (button != null) {
            button.setOnClickListener(listener);
        }
    }

    public void setupShareButton(View rootView, View.OnClickListener listener) {
        View button = rootView.findViewById(R.id.shareIv);
        if (button != null) {
            button.setOnClickListener(listener);
        }
    }

    public void shareVideo(Context context, String videoUrl) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, videoUrl);
        context.startActivity(Intent.createChooser(i, "Share Episode"));
    }

    public void setupFavouriteButton(View rootView, View.OnClickListener listener) {
        View button = rootView.findViewById(R.id.favIv);
        if (button != null) {
            button.setOnClickListener(listener);
        }
    }

    public void setupDownloadButton(View rootView, View.OnClickListener listener) {
        View button = rootView.findViewById(R.id.downloadIv);
        if (button != null) {
            button.setOnClickListener(listener);
        }
    }

    public void setupSettingsButton(View rootView, View.OnClickListener listener) {
        View button = rootView.findViewById(R.id.settingsIv);
        if (button != null) {
            button.setOnClickListener(listener);
        }
    }

    public void showSettingsMenu(Context context, View anchor, PlayerController playerController) {
        PopupMenuHelper.showPlayerSettingsMenu(anchor, context, playerController.getPlayer());
    }

    // Update UI methods
    public void updatePlayButton(Object binding, boolean isPlaying) {
        int iconRes = isPlaying ?
                android.R.drawable.ic_media_pause :
                android.R.drawable.ic_media_play;

        if (binding instanceof FragmentSeriesPlayerScreenBinding) {
            ((FragmentSeriesPlayerScreenBinding) binding).playIv.setImageResource(iconRes);
        } else if (binding instanceof LandscapeSeriesPlayerScreenBinding) {
            ((LandscapeSeriesPlayerScreenBinding) binding).playIv.setImageResource(iconRes);
        }
    }

    public void updateFavouriteButton(Object binding, boolean isFavourite) {
        int tintColor = isFavourite ? R.color.bluemain : R.color.white;

        if (binding instanceof FragmentSeriesPlayerScreenBinding) {
            ((FragmentSeriesPlayerScreenBinding) binding).favIv.setColorFilter(
                    context.getResources().getColor(tintColor)
            );
        } else if (binding instanceof LandscapeSeriesPlayerScreenBinding) {
//            ((FragmentSeriesLandscapePlayerScreenBinding) binding).favIv.setColorFilter(
//                    context.getResources().getColor(tintColor)
//            );
        }
    }

    public void updateDownloadButton(Object binding, boolean isDownloaded) {
        int tintColor = isDownloaded ? R.color.bluemain : R.color.white;

        if (binding instanceof FragmentSeriesPlayerScreenBinding) {
            ((FragmentSeriesPlayerScreenBinding) binding).downloadIv.setColorFilter(
                    context.getResources().getColor(tintColor)
            );
        } else if (binding instanceof LandscapeSeriesPlayerScreenBinding) {
//            ((FragmentSeriesLandscapePlayerScreenBinding) binding).downloadIv.setColorFilter(
//                    context.getResources().getColor(tintColor)
//            );
        }
    }

    // Touch listener
    public View.OnTouchListener getTouchListener(
            ControlsChecker controlsChecker,
            Runnable onHideControls,
            Runnable onShowControls) {
        return (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (controlsChecker.areControlsVisible()) {
                    onHideControls.run();
                    cancelHideControls();
                } else {
                    onShowControls.run();
                }
            }
            return true;
        };
    }

    public interface ControlsChecker {
        boolean areControlsVisible();
    }

    // SeekBar setup
    public void setupSeekBar(View rootView, SeekBarListener listener) {
        SeekBar seekBar = rootView.findViewById(R.id.playerSBar);
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        listener.onSeekChanged(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    listener.onSeekStarted();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    listener.onSeekCompleted();
                }
            });
        }
    }

    public void updatePlayerTiming(FragmentSeriesPlayerScreenBinding binding, long position) {
        binding.playerTimingTv.setText(formatTime(position));
    }

    // PIP methods
    @RequiresApi(api = android.os.Build.VERSION_CODES.S)
    public void enterPictureInPictureMode(Activity activity, StyledPlayerView videoView) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int width = videoView.getWidth();
            int height = videoView.getHeight();
            Rational aspect = (width > 0 && height > 0) ? new Rational(width, height) : new Rational(16, 9);

            PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder()
                    .setAspectRatio(aspect)
                    .setAutoEnterEnabled(true);

            activity.enterPictureInPictureMode(pipBuilder.build());
        } else {
            Toast.makeText(context, "PIP not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper methods
    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
        }
    }

    public void cleanup() {
        cancelAll();
    }

    public void cancelAll() {
        if (hideControlsRunnable != null) {
            handler.removeCallbacks(hideControlsRunnable);
        }
        if (updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }
        handler.removeCallbacksAndMessages(null);
    }
}
