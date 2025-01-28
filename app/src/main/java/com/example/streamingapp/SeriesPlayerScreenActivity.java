package com.example.streamingapp;

import static android.app.PendingIntent.getActivity;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SeriesPlayerScreenActivity extends AppCompatActivity {
    ConstraintLayout videoCl;
    TextView playerTimingTv;
    ImageView minmaxScreenIv, shareVideoIv, settingsIv, fastBackwardIv, playIv, fastForwardIv, fullScreenIv;
    SeekBar playerSBar;
    VideoView videoView;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private Runnable hideControlsRunnable;
    private static final int DEFAULT_TINT_COLOR = R.color.white;
    private static final int SELECTED_TINT_COLOR = R.color.bluemain;
    TabLayout tabLayout;
    FrameLayout frameLayout;
    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ImageView downloadIv, favIv, shareIv;
    TextView ratingTv, titleTv, playTv;
    private List<PopularSeriesItems> popularSeriesItemsList;
    private boolean isDownloaded = false;
    private boolean isFavourite = false;
    int seriesScreenIv;
    private static final int REQUEST_CODE_TV_LANDSCAPE = 1001;
    SeasonFragment seasonFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_series_player_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        videoCl = findViewById(R.id.videoCl);
        playerTimingTv = findViewById(R.id.playerTimingTv);
        minmaxScreenIv = findViewById(R.id.minScreenIv);
        shareVideoIv = findViewById(R.id.sharePlayerIv);
        settingsIv = findViewById(R.id.settingsIv);
        fastBackwardIv = findViewById(R.id.backwardIv);
        playIv = findViewById(R.id.playIv);
        fastForwardIv = findViewById(R.id.forwardIv);
        fullScreenIv = findViewById(R.id.fullScreenIv);
        playerSBar = findViewById(R.id.playerSBar);
        videoView = findViewById(R.id.videoView);
        ratingTv = findViewById(R.id.ratingTv);
        titleTv = findViewById(R.id.titleTv);
        playTv = findViewById(R.id.playTv);
        downloadIv = findViewById(R.id.downloadIv);
        favIv = findViewById(R.id.favIv);
        shareIv = findViewById(R.id.shareIv);
        tabLayout = findViewById(R.id.tab_layout);
        frameLayout = findViewById(R.id.framelayout);

        Intent intent = getIntent();
        int imageResource = intent.getIntExtra("imageResource", -1);
        String rating = intent.getStringExtra("rating");
        String title = intent.getStringExtra("title");

        // Log the received data
        Log.d(TAG, "Received imageResource: " + imageResource);
        Log.d(TAG, "Received rating: " + rating);

        if (imageResource != -1) {
            seriesScreenIv = imageResource;
            titleTv.setText(title);
            ratingTv.setText(rating);
        } else {
            Toast.makeText(this, "Data Not Found", Toast.LENGTH_SHORT).show();
        }

        popularSeriesItemsList = intent.getParcelableArrayListExtra("popularSeriesItemsList");

        if (popularSeriesItemsList == null || popularSeriesItemsList.isEmpty()) {
            Log.d(TAG, "popularSeriesItemsList is null or empty");
            Toast.makeText(this, "Error: No series items found", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if list is empty or null
            return;
        }

        // Log the contents of the list
        for (PopularSeriesItems item : popularSeriesItemsList) {
            Log.d(TAG, "Item from list: " + item.toString());
        }

        // Create the SeasonFragment with data
        seasonFragment = SeasonFragment.newInstance(popularSeriesItemsList, true, false);

        hideControls();

        videoView.setVideoURI(Uri.parse("android.resource://"+this.getPackageName()+"/"+R.raw.videohz));
        videoView.start();

        videoView.setOnPreparedListener(mp -> {
            playerSBar.setMax(videoView.getDuration());
            updateSeekBar();
        });

        videoView.setOnCompletionListener(mp -> playIv.setImageResource(android.R.drawable.ic_media_play));

        shareVideoIv.setOnClickListener(v -> {
            Uri videoUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.videohz);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("video/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
            startActivity(Intent.createChooser(shareIntent, "Share Video"));
        });

//
//        shareIv.setOnClickListener(v -> {
//            Uri videoUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.videohz);
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("video/*");
//            shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
//            startActivity(Intent.createChooser(shareIntent, "Share Video"));
//        });

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
            if (playIv.getVisibility() == View.VISIBLE) {
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
            Intent intent1 = new Intent(this, SeriesLandscapePlayerScreenActivity.class);
            Uri videoUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.videohz);
            intent1.putExtra("VIDEO_URI", videoUri.toString()); // Pass the URI as a string
            intent1.putExtra("CURRENT_POSITION", videoView.getCurrentPosition()); // Pass the current playback position
            intent1.putParcelableArrayListExtra("popularSeriesItemsList", (ArrayList<? extends Parcelable>) popularSeriesItemsList);
            startActivityForResult(intent1, REQUEST_CODE_TV_LANDSCAPE);
        });

        playTv.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                playIv.setImageResource(android.R.drawable.ic_media_play);
                handler.postDelayed(hideControlsRunnable, 5000);
            } else {
                videoView.start();
                playIv.setImageResource(android.R.drawable.ic_media_pause);
            }
        });


        downloadIv.setOnClickListener(v -> {
            if (isDownloaded) {
                Toast.makeText(this, "Already added to Download", Toast.LENGTH_SHORT).show();
            } else {
                downloadIv.setColorFilter(ContextCompat.getColor(this, SELECTED_TINT_COLOR), PorterDuff.Mode.SRC_IN);
                Toast.makeText(this, "Added to Download", Toast.LENGTH_SHORT).show();
                openDownloadDialog(seriesScreenIv, titleTv.getText().toString());
                isDownloaded = true;
            }
        });

        favIv.setOnClickListener(v -> {
            if (isFavourite) {
                favIv.setColorFilter(ContextCompat.getColor(this, DEFAULT_TINT_COLOR), PorterDuff.Mode.SRC_IN);
                Toast.makeText(this, "Removed from Favourite", Toast.LENGTH_SHORT).show();
                isFavourite = false;
            } else {
                favIv.setColorFilter(ContextCompat.getColor(this, SELECTED_TINT_COLOR), PorterDuff.Mode.SRC_IN);
                Toast.makeText(this, "Added to Favourite", Toast.LENGTH_SHORT).show();
                isFavourite = true;
            }
        });

        shareIv.setOnClickListener(v -> {
            shareIv.setColorFilter(ContextCompat.getColor(this, SELECTED_TINT_COLOR), PorterDuff.Mode.SRC_IN);
            Toast.makeText(this, "Starting to share", Toast.LENGTH_SHORT).show();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this Series!");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            shareIv.postDelayed(() -> shareIv.setColorFilter(ContextCompat.getColor(this, DEFAULT_TINT_COLOR), PorterDuff.Mode.SRC_IN), 1000);
        });

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.framelayout, seasonFragment);
        fragmentTransaction.commit();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                String tag = "android:switcher:" + R.id.framelayout + ":" + position;
                Fragment selectedFragment = getFragmentForPosition(position);
                if (selectedFragment != null) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.framelayout, seasonFragment, "fragment:" + position);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });
        tabLayout.getTabAt(0); // Select the first tab
    }

    private Fragment getFragmentForPosition(int position) {
        seasonFragment = SeasonFragment.newInstance(popularSeriesItemsList, true, false);
        if (position == 0) {
            return seasonFragment;
        }
        return seasonFragment;
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
        playerTimingTv.setVisibility(View.VISIBLE);
        minmaxScreenIv.setVisibility(View.VISIBLE);
        shareVideoIv.setVisibility(View.VISIBLE);
        settingsIv.setVisibility(View.VISIBLE);
        fastBackwardIv.setVisibility(View.VISIBLE);
        playIv.setVisibility(View.VISIBLE);
        fastForwardIv.setVisibility(View.VISIBLE);
        fullScreenIv.setVisibility(View.VISIBLE);
        playerSBar.setVisibility(View.VISIBLE);
    }

    private void hideControls() {
        playerTimingTv.setVisibility(View.GONE);
        minmaxScreenIv.setVisibility(View.GONE);
        shareVideoIv.setVisibility(View.GONE);
        settingsIv.setVisibility(View.GONE);
        fastBackwardIv.setVisibility(View.GONE);
        playIv.setVisibility(View.GONE);
        fastForwardIv.setVisibility(View.GONE);
        fullScreenIv.setVisibility(View.GONE);
        playerSBar.setVisibility(View.GONE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TV_LANDSCAPE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // Retrieve the video URI and playback position from the result
                String videoUriString = data.getStringExtra("VIDEO_URI");
                int currentPosition = data.getIntExtra("CURRENT_POSITION", 0);

                if (videoUriString != null) {
                    Uri videoUri = Uri.parse(videoUriString);
                    videoView.setVideoURI(videoUri);
                    videoView.seekTo(currentPosition);
                    videoView.start();
                    playIv.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        }
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

    private void openDownloadDialog(int movieImageView, String movieTitle) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_download);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView downloadIv = dialog.findViewById(R.id.downloadIv);
        TextView downloadTv = dialog.findViewById(R.id.downloadTv);
        TextView downloadTitle, downloadQualityVal, downloadAudio1, downloadAudio2, downloadSubtitleOff, downloadSubtitle1, downloadSubtitle2;
        SeekBar qualitySbar = dialog.findViewById(R.id.qualitySeekbar);

        downloadTitle = dialog.findViewById(R.id.downloadTitleTv);
        downloadQualityVal = dialog.findViewById(R.id.qualityValTv);
        downloadAudio1 = dialog.findViewById(R.id.audio1Tv);
        downloadAudio2 = dialog.findViewById(R.id.audio2Tv);
        downloadSubtitleOff = dialog.findViewById(R.id.subtitleOffTv);
        downloadSubtitle1 = dialog.findViewById(R.id.subtitle1Tv);
        downloadSubtitle2 = dialog.findViewById(R.id.subtitle2Tv);

        downloadIv.setImageResource(movieImageView);
        downloadTitle.setText(movieTitle);

        // Set default audio and subtitle selections
        downloadAudio1.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
        downloadSubtitleOff.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);

        // Configure SeekBar for quality selection
        qualitySbar.setMax(100);
        qualitySbar.setProgress(25); // Default to 25%

        qualitySbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 25) {
                    downloadQualityVal.setText("Low (360p)");
                } else if (progress < 50) {
                    downloadQualityVal.setText("Medium (480p)");
                } else if (progress < 75) {
                    downloadQualityVal.setText("High (720p)");
                } else {
                    downloadQualityVal.setText("HD (1080p)");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Set listeners for audio options
        downloadAudio1.setOnClickListener(v -> {
            downloadAudio1.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadAudio2.setBackgroundResource(R.drawable.dimcircle_bg);
        });

        downloadAudio2.setOnClickListener(v -> {
            downloadAudio2.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadAudio1.setBackgroundResource(R.drawable.dimcircle_bg);
        });

        downloadSubtitleOff.setOnClickListener(v -> {
            downloadSubtitleOff.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadSubtitle1.setBackgroundResource(R.drawable.dimcircle_bg);
            downloadSubtitle2.setBackgroundResource(R.drawable.dimcircle_bg);
        });
        // Set listeners for subtitle options
        downloadSubtitle1.setOnClickListener(v -> {
            downloadSubtitle1.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadSubtitle2.setBackgroundResource(R.drawable.dimcircle_bg);
            downloadSubtitleOff.setBackgroundResource(R.drawable.dimcircle_bg);
        });

        downloadSubtitle2.setOnClickListener(v -> {
            downloadSubtitle2.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadSubtitle1.setBackgroundResource(R.drawable.dimcircle_bg);
            downloadSubtitleOff.setBackgroundResource(R.drawable.dimcircle_bg);
        });


        downloadTv.setOnClickListener(v -> {
            Toast.makeText(this, "Download in Progress/You can watch while download", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
//            Intent intent = new Intent(this, SeriesPlayerScreenActivity.class);
//            startActivity(intent);
//            finish();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}