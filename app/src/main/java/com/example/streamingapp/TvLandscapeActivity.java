package com.example.streamingapp;

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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class TvLandscapeActivity extends AppCompatActivity implements TvProgramTimingRecItemAdapter.OnTimingSelectedListener {
    ConstraintLayout videoCl;
    TextView liveTv, playerTimingTv;
    RelativeLayout fastForwardRl, fastBackwardRl;
    ImageView minmaxScreenIv, shareIv, settingsIv, fastBackwardIv, playIv, fastForwardIv, fullScreenIv, listModeIv;
    SeekBar playerSBar;
    VideoView videoView;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private Runnable hideControlsRunnable;
    private TvProgramRecItemAdapter tvProgramRecItemAdapter;
    private String videoUriString;
    private int currentPosition;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tv_landscape);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        videoCl = findViewById(R.id.videoCl);
        liveTv = findViewById(R.id.liveTv);
        playerTimingTv = findViewById(R.id.playerTimingTv);
        minmaxScreenIv = findViewById(R.id.minScreenIv);
        shareIv = findViewById(R.id.shareIv);
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

        listModeIv.setOnClickListener(v -> {
            dialogLandscapeTvFragment();
        });

        hideControls();

        Intent intent = getIntent();
        if (intent != null) {
            videoUriString = intent.getStringExtra("VIDEO_URI");
            currentPosition = intent.getIntExtra("CURRENT_POSITION", 0);
        }

        // Set up the VideoView with the URI and start playback
        if (videoUriString != null) {
            Uri videoUri = Uri.parse(videoUriString);
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(mp -> {
                videoView.seekTo(currentPosition);
                videoView.start();
            });
        }

        videoView.setOnPreparedListener(mp -> {
            playerSBar.setMax(videoView.getDuration());
            updateSeekBar();
        });

        videoView.setOnCompletionListener(mp -> playIv.setImageResource(android.R.drawable.ic_media_play));

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
                liveTv.setText("To the Live");
                liveTv.setBackgroundResource(R.drawable.lgtransparentwhitestroke_bg);
                playIv.setImageResource(android.R.drawable.ic_media_play);
                handler.postDelayed(hideControlsRunnable, 5000);
            } else {
                videoView.start();
                playIv.setImageResource(android.R.drawable.ic_media_pause);
                liveTv.setText("Live");
                liveTv.setBackgroundResource(R.drawable.lgblackcircle_bg);
            }
        });

        liveTv.setOnClickListener(v -> {
            if ("To the Live".equals(liveTv.getText().toString())) {
                videoView.start();
                playIv.setImageResource(android.R.drawable.ic_media_pause);
                liveTv.setText("Live");
                liveTv.setBackgroundResource(R.drawable.lgblackcircle_bg);
                showControls();
                handler.postDelayed(hideControlsRunnable, 10000);
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
            if (liveTv.getVisibility() == View.VISIBLE) {
                hideControls();
                handler.removeCallbacks(hideControlsRunnable);
            } else {
                showControls();
                handler.removeCallbacks(hideControlsRunnable);
                handler.postDelayed(hideControlsRunnable, 10000);
            }
        });

        hideControlsRunnable = this::hideControls;

        fullScreenIv.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("VIDEO_URI", videoUriString);
            resultIntent.putExtra("CURRENT_POSITION", videoView.getCurrentPosition());

            // Set the result and finish the activity
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

//    private void initTvSelectionFragment(FrameLayout container, Fragment fragment) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(container.getId(), fragment);
//        transaction.commit();
//        Log.d("TvLandscapeActivity", "TvSelectionFragment transaction committed");
//    }

    private void dialogLandscapeTvFragment() {

        TvSelectionFragment tvSelectionFragment = new TvSelectionFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add the fragment to the container
        fragmentTransaction.add(R.id.tvFrameLayout, tvSelectionFragment);

        // Load animations
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        // Add custom animations to the fragment transaction
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right, // Enter animation
                R.anim.slide_out_right // Exit animation
        );

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        // Optionally, start the animation after the fragment has been added
        FrameLayout frameLayout = findViewById(R.id.tvFrameLayout);
        if (frameLayout != null) {
            // Add GestureDetector to detect swipe left to dismiss the fragment
            GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                private static final int SWIPE_THRESHOLD = 100;
                private static final int SWIPE_VELOCITY_THRESHOLD = 100;

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX < 0) {
                            // Detected a left swipe, remove the fragment with animation
                            fragmentManager.popBackStack();
                            frameLayout.startAnimation(AnimationUtils.loadAnimation(TvLandscapeActivity.this, R.anim.slide_out_right));
                            return true;
                        }
                    }
                    return false;
                }
            });

            frameLayout.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        } else {
            Log.e("TvLandscapeActivity", "FrameLayout with ID frameLayout not found.");
        }
    }

    private void updateSeekBar() {
        playerSBar.setProgress(videoView.getCurrentPosition());
        updatePlayerTiming();
        handler.postDelayed(updateSeekBarRunnable = this::updateSeekBar, 1000);
    }

    private void updatePlayerTiming() {
        int currentPos = videoView.getCurrentPosition();
        playerTimingTv.setText(String.format("%02d:%02d:%02d",
                (currentPos / 1000) / 3600, ((currentPos / 1000) % 3600) / 60, (currentPos / 1000) % 60));
    }

    private void showControls() {
        liveTv.setVisibility(View.VISIBLE);
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
        liveTv.setVisibility(View.INVISIBLE);
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
    public void onTimingSelected(int position) {
        // Initialize tvProgramRecItemAdapter if not already initialized
        if (tvProgramRecItemAdapter == null) {
            tvProgramRecItemAdapter = new TvProgramRecItemAdapter(this, getProgramsForTiming(position));
            // Set adapter to RecyclerView or initialize it as needed
        } else {
            List<TvProgramItems> updatedProgramItems = getProgramsForTiming(position);
            tvProgramRecItemAdapter.updateProgramList(updatedProgramItems);
        }
    }

    private List<TvProgramItems> getProgramsForTiming(int position) {
        // Implement this method to return the list of programs for the selected timing
        return new ArrayList<>(); // Placeholder implementation
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