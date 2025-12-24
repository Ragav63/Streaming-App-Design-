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
import com.example.streamingapp.databinding.FragmentTvBinding;
import com.example.streamingapp.databinding.LandscapeSeriesPlayerScreenBinding;
import com.example.streamingapp.databinding.LandscapeTvPlayerScreenBinding;
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
    private boolean isSeeking = false;
    private boolean currentPlayState = false;

    public PlayerUIHelper(Context context, LifecycleOwner owner, boolean isLandscape) {
        this.context = context;
        this.lifecycleOwner = owner;
        this.isLandscape = isLandscape;
    }

    public void setCurrentPlayState(boolean isPlaying) {
        this.currentPlayState = isPlaying;
    }

    public void setSeeking(boolean seeking) {
        this.isSeeking = seeking;
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

                    // FIX: Only update if duration is valid (not 0)
                    if (duration > 0) {
                        // Calculate progress percentage (0-100)
                        int progress = (int) ((position * 100) / duration);

                        // FIX: Ensure progress stays within bounds
                        progress = Math.max(0, Math.min(100, progress));

                        if (binding instanceof FragmentSeriesPlayerScreenBinding) {
                            FragmentSeriesPlayerScreenBinding portrait =
                                    (FragmentSeriesPlayerScreenBinding) binding;
                            portrait.playerSBar.setMax(100);  // Set max to 100 for percentage
                            portrait.playerSBar.setProgress(progress);
                            portrait.playerTimingTv.setText(formatTime(position));
                        } else if (binding instanceof LandscapeSeriesPlayerScreenBinding) {
                            LandscapeSeriesPlayerScreenBinding landscape =
                                    (LandscapeSeriesPlayerScreenBinding) binding;
                            landscape.playerSBar.setMax(100);
                            landscape.playerSBar.setProgress(progress);
                            landscape.playerTimingTv.setText(formatTime(position));
                        } else if (binding instanceof FragmentTvBinding) {
                            FragmentTvBinding portrait =
                                    (FragmentTvBinding) binding;
                            portrait.playerSBar.setMax(100);
                            portrait.playerSBar.setProgress(progress);
                            portrait.playerTimingTv.setText(formatTime(position));
                        } else if (binding instanceof LandscapeTvPlayerScreenBinding) {
                            LandscapeTvPlayerScreenBinding landscape =
                                    (LandscapeTvPlayerScreenBinding) binding;
                            landscape.playerSBar.setMax(100);
                            landscape.playerSBar.setProgress(progress);
                            landscape.playerTimingTv.setText(formatTime(position));
                        }

                        // Update ViewModel
                        viewModel.updatePlaybackState(
                                playerController.isPlaying(),
                                position,
                                duration
                        );
                    }

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
        } else if (binding instanceof FragmentTvBinding) {
            FragmentTvBinding portrait =
                    (FragmentTvBinding) binding;
            return portrait.playIv.getVisibility() == View.VISIBLE;
        }else if (binding instanceof LandscapeTvPlayerScreenBinding) {
            LandscapeTvPlayerScreenBinding landscape =
                    (LandscapeTvPlayerScreenBinding) binding;
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
        } else if (binding instanceof FragmentTvBinding) {
            FragmentTvBinding portrait = (FragmentTvBinding) binding;
            portrait.playerTimingTv.setVisibility(View.GONE);
            portrait.playIv.setVisibility(View.GONE);
            portrait.fastForwardRl.setVisibility(View.GONE);
            portrait.fastBackwardRl.setVisibility(View.GONE);
            portrait.playerSBar.setVisibility(View.GONE);
            portrait.fullScreenIv.setVisibility(View.GONE);
            portrait.minScreenIv.setVisibility(View.GONE);
            portrait.settingsIv.setVisibility(View.GONE);
        } else if (binding instanceof LandscapeTvPlayerScreenBinding) {
            LandscapeTvPlayerScreenBinding landscape =
                    (LandscapeTvPlayerScreenBinding) binding;
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
        } else if (binding instanceof FragmentTvBinding) {
            FragmentTvBinding portrait = (FragmentTvBinding) binding;
            portrait.playerTimingTv.setVisibility(View.VISIBLE);
            portrait.playIv.setVisibility(View.VISIBLE);
            portrait.fastForwardRl.setVisibility(View.VISIBLE);
            portrait.fastBackwardRl.setVisibility(View.VISIBLE);
            portrait.playerSBar.setVisibility(View.VISIBLE);
            portrait.fullScreenIv.setVisibility(View.VISIBLE);
            portrait.minScreenIv.setVisibility(View.VISIBLE);
            portrait.settingsIv.setVisibility(View.VISIBLE);
        } else if (binding instanceof LandscapeTvPlayerScreenBinding) {
            LandscapeTvPlayerScreenBinding landscape =
                    (LandscapeTvPlayerScreenBinding) binding;
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



    public void shareVideo(Context context, String videoUrl) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, videoUrl);
        context.startActivity(Intent.createChooser(i, "Share Episode"));
    }


    public void showSettingsMenu(Context context, View anchor, PlayerController playerController) {
        PopupMenuHelper.showPlayerSettingsMenu(anchor, context, playerController.getPlayer());
    }

    // Update UI methods
    public void updatePlayButton(Object binding, boolean isPlaying) {
        // Store the current play state
        this.currentPlayState = isPlaying;

        // Only update if not seeking
        if (!isSeeking) {
            updatePlayButtonInternal(binding, isPlaying);
        }
    }

    // UPDATED: Force update play button regardless of seeking state
    public void updatePlayButtonImmediate(Object binding, boolean isPlaying) {
        this.currentPlayState = isPlaying;
        updatePlayButtonInternal(binding, isPlaying);
    }

    // Internal method to actually update the UI
    private void updatePlayButtonInternal(Object binding, boolean isPlaying) {
        int iconRes = isPlaying ?
                android.R.drawable.ic_media_pause :
                android.R.drawable.ic_media_play;

        if (binding instanceof FragmentSeriesPlayerScreenBinding) {
            ((FragmentSeriesPlayerScreenBinding) binding).playIv.setImageResource(iconRes);
        } else if (binding instanceof LandscapeSeriesPlayerScreenBinding) {
            ((LandscapeSeriesPlayerScreenBinding) binding).playIv.setImageResource(iconRes);
        } else if (binding instanceof FragmentTvBinding) {
            ((FragmentTvBinding) binding).playIv.setImageResource(iconRes);
        } else if (binding instanceof LandscapeTvPlayerScreenBinding) {
            ((LandscapeTvPlayerScreenBinding) binding).playIv.setImageResource(iconRes);
        }
    }

    // UPDATED: Restore play button to current state after seeking
    public void restorePlayButtonAfterSeek(Object binding) {
        // Reset seeking flag
        isSeeking = false;
        // Restore to current play state
        updatePlayButtonInternal(binding, currentPlayState);
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





    public void updatePlayerTiming(FragmentSeriesPlayerScreenBinding binding, long position) {
        binding.playerTimingTv.setText(formatTime(position));
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
