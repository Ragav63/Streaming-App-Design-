package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class SeriesLandscapePlayerScreenActivity extends AppCompatActivity {
    ConstraintLayout videoCl;
    LinearLayout linearFrame;
    TextView playerTimingTv;
    RelativeLayout fastForwardRl, fastBackwardRl;
    ImageView minmaxScreenIv, shareIv, settingsIv, fastBackwardIv, playIv, fastForwardIv, fullScreenIv, listModeIv, closeIv;
    SeekBar playerSBar;
    VideoView videoView;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private Runnable hideControlsRunnable;
    private List<SeriesItems> seriesItemsList;
    private TabLayout tabLayout;
    private FrameLayout frameLayout;
    private FragmentManager fragmentManager;
    private String videoUriString;
    private int currentPosition;
    private SeasonFragment seasonFragment;
    private boolean isDialogOpen = true;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_series_landscape_player_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();

        initializeVideoView();

        // Initialize the FragmentManager
        fragmentManager = getSupportFragmentManager();

        listModeIv.setOnClickListener(v -> {
            dialogLandscapeSeasonFragment();
        });

        hideControls();

        Intent intent = getIntent();
        if (intent != null) {
            videoUriString = intent.getStringExtra("VIDEO_URI");
            currentPosition = intent.getIntExtra("CURRENT_POSITION", 0);
        }

        seriesItemsList = intent.getParcelableArrayListExtra("popularSeriesItemsList");

        if (seriesItemsList == null || seriesItemsList.isEmpty()) {
            Log.d("SeriesLandscapeActivity", "popularSeriesItemsList is null or empty");
            Toast.makeText(this, "Error: No series items found", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if list is empty or null
            return;
        }

        // Log the contents of the list
        for (SeriesItems item : seriesItemsList) {
            Log.d("SeriesLandscapeActivity", "Item from list: " + item.toString());
        }


        // Set up the VideoView with the URI and start playback
        if (videoUriString != null) {
            Uri videoUri = Uri.parse(videoUriString);
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(mp -> {
                videoView.seekTo(currentPosition);
                videoView.start();
                playerSBar.setMax(videoView.getDuration());
                updateSeekBar();
            });
        }

        videoView.setOnCompletionListener(mp -> playIv.setImageResource(android.R.drawable.ic_media_play));

        setListeners();

        setTabLayoutListener();

        hideControlsRunnable = this::hideControls;

    }

    private void initializeViews() {
        videoCl = findViewById(R.id.videoCl);
        linearFrame = findViewById(R.id.linearFrame);
        playerTimingTv = findViewById(R.id.playerTimingTv);
        minmaxScreenIv = findViewById(R.id.minScreenIv);
        shareIv = findViewById(R.id.sharePlayerIv);
        settingsIv = findViewById(R.id.settingsIv);
        fastBackwardIv = findViewById(R.id.backwardIv);
        playIv = findViewById(R.id.playIv);
        fastForwardIv = findViewById(R.id.forwardIv);
        fastForwardRl = findViewById(R.id.fastForwardRl);
        fastBackwardRl = findViewById(R.id.fastBackwardRl);
        fullScreenIv = findViewById(R.id.fullScreenIv);
        playerSBar = findViewById(R.id.playerSBar);
        videoView = findViewById(R.id.videoView);
        listModeIv = findViewById(R.id.listMode);
        tabLayout = findViewById(R.id.tab_layout);
        frameLayout = findViewById(R.id.seriesLFrameLayout);
        closeIv = findViewById(R.id.closeIv);
    }

    private void initializeVideoView() {
        videoView.setOnPreparedListener(mp -> {
            playerSBar.setMax(videoView.getDuration());
            updateSeekBar();
        });

        videoView.setOnCompletionListener(mp -> playIv.setImageResource(android.R.drawable.ic_media_play));
    }

    private void setListeners() {
        shareIv.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("video/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, videoUriString);
            startActivity(Intent.createChooser(shareIntent, "Share Video"));
        });

        settingsIv.setOnClickListener(v -> openSettingsDialog());

        playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
                updatePlayerTiming();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        playIv.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                playIv.setImageResource(android.R.drawable.ic_media_play);
                handler.postDelayed(hideControlsRunnable, 5000);
            } else {
                videoView.start();
                playIv.setImageResource(android.R.drawable.ic_media_pause);
                showControls();
                updateSeekBar();
            }
        });

        fastBackwardIv.setOnClickListener(v -> {
            int currentPosition = videoView.getCurrentPosition();
            videoView.seekTo(Math.max(currentPosition - 10000, 0));
        });

        fastForwardIv.setOnClickListener(v -> {
            int currentPosition = videoView.getCurrentPosition();
            videoView.seekTo(Math.min(currentPosition + 10000, videoView.getDuration()));
        });

        videoCl.setOnClickListener(v -> {
            if (isDialogOpen){
            if (playIv.getVisibility() == View.VISIBLE) {
                hideControls();
                handler.removeCallbacks(hideControlsRunnable);
            } else {
                showControls();
                handler.removeCallbacks(hideControlsRunnable);
                handler.postDelayed(hideControlsRunnable, 10000);
            }}
        });

        fullScreenIv.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("VIDEO_URI", videoUriString);
            resultIntent.putExtra("CURRENT_POSITION", videoView.getCurrentPosition());
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setTabLayoutListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selectedFragment = getFragmentForPosition(position);
                if (selectedFragment != null) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.seriesLFrameLayout, selectedFragment, "fragment:" + position);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselected if needed
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselected if needed
            }
        });
        tabLayout.getTabAt(0);
    }

    private Fragment getFragmentForPosition(int position) {
        seasonFragment = SeasonFragment.newInstance(seriesItemsList, false, true);
        if (position == 0) {
            return seasonFragment;
        }
        return seasonFragment;
    }

    private void dialogLandscapeSeasonFragment() {

        // Load animations
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_botttom);

        hideControls();
        // Start animation for linearFrame
        if (linearFrame != null) {
            // Set linearFrame to be initially invisible
            linearFrame.setVisibility(View.INVISIBLE);

            // Start the slide in animation and make linearFrame visible
            linearFrame.setVisibility(View.VISIBLE);
            isDialogOpen = false;
            linearFrame.startAnimation(slideIn);
            // Ensure seasonFragment is not null before transaction
            if (seasonFragment == null) {
                seasonFragment = SeasonFragment.newInstance(seriesItemsList, false,true);
            }

            // FragmentTransaction with null check
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.seriesLFrameLayout, seasonFragment, "SEASON_FRAGMENT");
            transaction.commit();

        } else {
            Log.e("SeriesLandscapePlayer", "linearFrame not found.");
        }

        // Handle close button click to dismiss the fragment
        closeIv.setOnClickListener(v -> {
            Log.d("SeriesLandscapePlayer", "Close button clicked");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = fm.findFragmentById(R.id.seriesLFrameLayout);

            if (fragment != null) {
                Log.d("SeriesLandscapePlayer", "Fragment found, removing...");
                ft.remove(fragment);
                ft.commit();
            } else {
                Log.d("SeriesLandscapePlayer", "No fragment found to remove.");
            }
            isDialogOpen = true;
            if (linearFrame != null) {
                Animation slideOut1 = AnimationUtils.loadAnimation(this, R.anim.slide_out_botttom);
                linearFrame.startAnimation(slideOut1);
                showControls();
                linearFrame.setVisibility(View.GONE);
            }
        });
    }

    private void updateSeekBar() {
        playerSBar.setProgress(videoView.getCurrentPosition());
        updatePlayerTiming();
        if (videoView.isPlaying()) {
            handler.postDelayed(updateSeekBarRunnable = this::updateSeekBar, 1000);
        }
    }

    private void updatePlayerTiming() {
        int currentPos = videoView.getCurrentPosition();
        playerTimingTv.setText(String.format("%02d:%02d:%02d",
                (currentPos / 1000) / 3600, ((currentPos / 1000) % 3600) / 60, (currentPos / 1000) % 60));
    }

    private void showControls() {
        playerTimingTv.setVisibility(View.VISIBLE);
        minmaxScreenIv.setVisibility(View.VISIBLE);
        shareIv.setVisibility(View.VISIBLE);
        settingsIv.setVisibility(View.VISIBLE);
        fastBackwardIv.setVisibility(View.VISIBLE);
        fastForwardRl.setVisibility(View.VISIBLE);
        fastBackwardRl.setVisibility(View.VISIBLE);
        playIv.setVisibility(View.VISIBLE);
        fastForwardIv.setVisibility(View.VISIBLE);
        fullScreenIv.setVisibility(View.VISIBLE);
        playerSBar.setVisibility(View.VISIBLE);
        listModeIv.setVisibility(View.VISIBLE);
    }

    private void hideControls() {
        playerTimingTv.setVisibility(View.INVISIBLE);
        minmaxScreenIv.setVisibility(View.INVISIBLE);
        shareIv.setVisibility(View.INVISIBLE);
        settingsIv.setVisibility(View.INVISIBLE);
        fastBackwardIv.setVisibility(View.INVISIBLE);
        playIv.setVisibility(View.INVISIBLE);
        fastForwardIv.setVisibility(View.INVISIBLE);
        fastForwardRl.setVisibility(View.INVISIBLE);
        fastBackwardRl.setVisibility(View.INVISIBLE);
        fullScreenIv.setVisibility(View.INVISIBLE);
        playerSBar.setVisibility(View.INVISIBLE);
        listModeIv.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onPause() {
        super.onPause();
//        handler.removeCallbacks(updateSeekBarRunnable);
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateSeekBar();
        videoView.seekTo(currentPosition);
        videoView.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current playback position
        outState.putInt("CURRENT_POSITION", videoView.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the current playback position
        currentPosition = savedInstanceState.getInt("CURRENT_POSITION");
        videoView.seekTo(currentPosition);
    }

    private void openSettingsDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quality);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ConstraintLayout constraintLayout = dialog.findViewById(R.id.constraint);
        TextView qualityVal = dialog.findViewById(R.id.qualityVal);
        SeekBar qualitySbar = dialog.findViewById(R.id.qualitySeekbar);

        // Configure SeekBar for quality selection
        qualitySbar.setMax(100);
        qualitySbar.setProgress(100); // Default to 25%

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
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Add GestureDetector to detect swipe down
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("VIDEO_URI", videoUriString); // Pass the URI as a string
        resultIntent.putExtra("CURRENT_POSITION", videoView.getCurrentPosition()); // Pass the current playback position
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}