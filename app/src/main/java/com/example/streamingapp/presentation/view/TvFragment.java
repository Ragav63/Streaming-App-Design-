package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvProgramItems;
import com.example.streamingapp.presentation.adapter.TvProgramRecItemAdapter;
import com.example.streamingapp.presentation.adapter.TvProgramTimingRecItemAdapter;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TvFragment extends Fragment  implements TvProgramTimingRecItemAdapter.OnTimingSelectedListener {
    ConstraintLayout videoCl;
    TextView liveTv, playerTimingTv;
    ImageView minmaxScreenIv, shareIv, settingsIv, fastBackwardIv, playIv, fastForwardIv, fullScreenIv;
    SeekBar playerSBar;
    VideoView videoView;
    private YouTubePlayerView youtubePlayerView;
    private WebView videoWebView;
    private PlayerView playerView;
    private ExoPlayer player;
    FrameLayout tvFrameLayout;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private Runnable hideControlsRunnable;
    private TvProgramRecItemAdapter tvProgramRecItemAdapter;
    private static final int REQUEST_CODE_TV_LANDSCAPE = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tv, container, false);

        videoCl = view.findViewById(R.id.videoCl);
        liveTv = view.findViewById(R.id.liveTv);
        playerTimingTv = view.findViewById(R.id.playerTimingTv);
        minmaxScreenIv = view.findViewById(R.id.minScreenIv);
        shareIv = view.findViewById(R.id.shareIv);
        settingsIv = view.findViewById(R.id.settingsIv);
        fastBackwardIv = view.findViewById(R.id.backwardIv);
        playIv = view.findViewById(R.id.playIv);
        fastForwardIv = view.findViewById(R.id.forwardIv);
        fullScreenIv = view.findViewById(R.id.fullScreenIv);
        playerSBar = view.findViewById(R.id.playerSBar);
        videoWebView = view.findViewById(R.id.videoView);
        tvFrameLayout = view.findViewById(R.id.tvFrameLayout);

        videoCl.setVisibility(View.VISIBLE);
        videoCl.setFocusable(true);

        if (videoCl == null) {
            Log.e("TvFragment", "videoCl is null");
        }


//        MediaController mediaController = new MediaController(getActivity());
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);

        hideControls();

        showControls();

//        videoView.setVideoURI(Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.videohz));
//        videoView.start();
//
//        videoView.setOnPreparedListener(mp -> {
//            playerSBar.setMax(videoView.getDuration());
//            updateSeekBar();
//        });
//
//        videoView.setOnCompletionListener(mp -> playIv.setImageResource(android.R.drawable.ic_media_play));

//        player = new ExoPlayer.Builder(getActivity()).build();
//        playerView.setPlayer(player);
//
//        Uri videoUri = Uri.parse("V2KCAfHjySQ");
//        MediaItem mediaItem = MediaItem.fromUri(videoUri);
//        player.setMediaItem(mediaItem);
//        player.prepare();
//        player.play();

//        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                String videoId = "https://youtu.be/V2KCAfHjySQ?si=PKe93pzE8tEmMDGk";
//                youTubePlayer.loadVideo(videoId, 0);
//            }
//        });

//        https://dai.ly/x93ne64

        // Configure WebView settings
        WebSettings webSettings = videoWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        videoWebView.setWebChromeClient(new WebChromeClient());
        videoWebView.setWebViewClient(new WebViewClient());

        String videoHtml = "<html><body style='margin:0;padding:0;overflow:hidden;'>"
                + "<div id='player'></div>"
//                + "<button id='playPauseBtn' style='display:none;'>Play/Pause</button>"
//                + "<button id='seekForwardBtn' style='display:none;'>Forward</button>"
//                + "<button id='seekBackwardBtn' style='display:none;'>Backward</button>"
                + "<script type='text/javascript'>"
                + "var player;"
                + "function onYouTubeIframeAPIReady() {"
                + "    player = new YT.Player('player', {"
                + "        height: '100%',"
                + "        width: '100%',"
                + "        videoId: 'V2KCAfHjySQ',"
                + "        playerVars: {"
                + "            'autoplay': 1,"
                + "            'controls': 0,"
                + "            'modestbranding': 1,"
                + "            'showinfo': 0,"
                + "            'rel': 0,"
                + "            'iv_load_policy': 3"
                + "        },"
                + "        events: {"
                + "            'onReady': onPlayerReady"
                + "        }"
                + "    });"
                + "}"
                + "function onPlayerReady(event) {"
                + "    player.playVideo();"
                + "    startUpdatingCurrentTime();"
                + "    window.AndroidInterface.sendVideoId(player.getVideoData().video_id);"
//                + "    document.getElementById('playPauseBtn').style.display = 'block';"
//                + "    document.getElementById('seekForwardBtn').style.display = 'block';"
//                + "    document.getElementById('seekBackwardBtn').style.display = 'block';"
                + "}"
                + "function startUpdatingCurrentTime() {"
                + "    setInterval(function() {"
                + "        var currentTime = player.getCurrentTime();"
                + "        var hours = Math.floor(currentTime / 3600);"
                + "        var minutes = Math.floor((currentTime % 3600) / 60);"
                + "        var seconds = Math.floor(currentTime % 60);"
                + "        var formattedTime = hours.toString().padStart(2, '0') + ':'"
                + "                         + minutes.toString().padStart(2, '0') + ':'"
                + "                         + seconds.toString().padStart(2, '0');"
                + "        window.AndroidInterface.updatePlayerTiming(formattedTime);"
                + "    }, 1000);"
                + "}"
                + "function getPlayerState() {"
                + "    return player.getPlayerState();"
                + "}"
                + "function togglePlayPause() {"
                + "    if(player.getPlayerState() == YT.PlayerState.PLAYING) {"
                + "        player.pauseVideo();"
                + "    } else {"
                + "        player.playVideo();"
                + "    }"
                + "}"
                + "function seekForward() {"
                + "    player.seekTo(player.getCurrentTime() + 10, true);"
                + "}"
                + "function seekBackward() {"
                + "    player.seekTo(player.getCurrentTime() - 10, true);"
                + "}"
                + "function setPlaybackQuality(quality) {"
                + "    if (player && player.setPlaybackQuality) {"
                + "        player.setPlaybackQuality(quality);"
                + "    }"
                + "}"
                + "document.getElementById('playPauseBtn').onclick = togglePlayPause;"
                + "document.getElementById('seekForwardBtn').onclick = seekForward;"
                + "document.getElementById('seekBackwardBtn').onclick = seekBackward;"
                + "</script>"
                + "<script src='https://www.youtube.com/iframe_api'></script>"
                + "</body></html>";



        videoWebView.loadData(videoHtml, "text/html", "UTF-8");



        videoWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Start updating progress and time when the page is fully loaded
                startProgressUpdate();
                // Make sure the page load is complete before evaluating JavaScript
                Log.d("WebView", "Page loaded: " + url);
                videoWebView.addJavascriptInterface(new WebAppInterface(getContext()), "AndroidInterface");
            }
        });

//        // Load the video URL
//        String videoUrl = "https://www.youtube.com/embed/V2KCAfHjySQ?autoplay=1&modestbranding=1&controls=0&showinfo=0&rel=0";
//        videoWebView.loadUrl(videoUrl);

//        // Step 1: Extract the current URL from the WebView
//        String videoUrl = videoWebView.getUrl(); // This gets the current URL loaded in the WebView

        shareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("ShareButton", "Share button clicked");
                // Ensure the WebView is fully loaded and ready
                videoWebView.evaluateJavascript("player.getVideoData().video_id;", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String videoId) {
                        if (videoId != null && !videoId.isEmpty()) {
                            // Clean up the videoId string (removing extra quotes if present)
                            videoId = videoId.replace("\"", "");
                            Log.d("ShareButton", "Video ID retrieved: " + videoId);

                            // Create the video URL
                            String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                            // Share the video URL
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, videoUrl);
                            startActivity(Intent.createChooser(shareIntent, "Share Video URL"));
                        } else {
                            Toast.makeText(getContext(), "Failed to get video ID", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

//        shareIv.setOnClickListener(v -> {
//            Uri videoUri1 = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.videohz);
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("video/*");
//            shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri1);
//            startActivity(Intent.createChooser(shareIntent, "Share Video"));
//        });

        settingsIv.setOnClickListener(v -> openSettingsDialog());

        playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
//                    videoView.seekTo(progress);
                    // Convert progress to video time
                    double duration = playerSBar.getMax(); // Max value of SeekBar should represent the duration
                    double currentTime = (progress / 100.0) * duration;

                    // Seek video to the new time
                    videoWebView.evaluateJavascript("player.seekTo(" + currentTime + ", true);", null);

                    // Update the time display
                    playerTimingTv.setText(formatTime(currentTime));
                }
//                updatePlayerTiming();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        playIv.setOnClickListener(v -> {
//            videoWebView.evaluateJavascript("togglePlayPause();", value -> Log.d("WebView", "togglePlayPause executed: " + value));
            videoWebView.evaluateJavascript("player.getPlayerState();", value -> {
                if (value != null) {
                    if (Integer.parseInt(value) == 1) {
                        videoWebView.evaluateJavascript("player.pauseVideo();", null);
                        playIv.setImageResource(android.R.drawable.ic_media_play);
                        liveTv.setText("To the Live");
                        liveTv.setBackgroundResource(R.drawable.lgtransparentwhitestroke_bg);
                    } else {
                        videoWebView.evaluateJavascript("player.playVideo();", null);
                        playIv.setImageResource(android.R.drawable.ic_media_pause);
                    }
                }
            });
//            if (videoView.isPlaying()) {
//                videoView.pause();
//                liveTv.setText("To the Live");
//                liveTv.setBackgroundResource(R.drawable.lgtransparentwhitestroke_bg);
//                playIv.setImageResource(android.R.drawable.ic_media_play);
//                handler.postDelayed(hideControlsRunnable, 5000);
//            } else {
//                videoView.start();
//                playIv.setImageResource(android.R.drawable.ic_media_pause);
//                liveTv.setText("Live");
//                liveTv.setBackgroundResource(R.drawable.lgblackcircle_bg);
//            }
        });

        liveTv.setOnClickListener(v -> {
            if ("To the Live".equals(liveTv.getText().toString())) {
//                videoView.start();
//                videoWebView.evaluateJavascript("player.playVideo();", null);
                videoWebView.evaluateJavascript("player.getPlayerState();", value -> {
                    if (value != null) {
                        if (Integer.parseInt(value) == 1) {
                            videoWebView.evaluateJavascript("player.pauseVideo();", null);
                            playIv.setImageResource(android.R.drawable.ic_media_play);
                        } else {
                            videoWebView.evaluateJavascript("player.playVideo();", null);
                            playIv.setImageResource(android.R.drawable.ic_media_pause);
                            liveTv.setText("Live");
                            liveTv.setBackgroundResource(R.drawable.lgblackcircle_bg);
                            showControls();
                            handler.postDelayed(hideControlsRunnable, 10000);
                        }
                    }
                });
//                playIv.setImageResource(android.R.drawable.ic_media_pause);
//                liveTv.setText("Live");
//                liveTv.setBackgroundResource(R.drawable.lgblackcircle_bg);
//                showControls();
//                handler.postDelayed(hideControlsRunnable, 10000);
            }
        });

        fastBackwardIv.setOnClickListener(v -> {
//            int currentPosition = videoView.getCurrentPosition();
//            videoView.seekTo(Math.max(currentPosition - 10000, 0));
            videoWebView.evaluateJavascript("seekBackward();", value -> Log.d("WebView", "seekBackward executed: " + value));
        });

        fastForwardIv.setOnClickListener(v -> {
//            int currentPosition = videoView.getCurrentPosition();
//            videoView.seekTo(Math.min(currentPosition + 10000, videoView.getDuration()));
            videoWebView.evaluateJavascript("seekForward();", value -> Log.d("WebView", "seekForward executed: " + value));
        });

        videoCl.setOnClickListener(v -> {
            Log.d("TvFragment", "Video cl clicked");
            if (playIv.getVisibility() == View.VISIBLE) {
                hideControls();
                handler.removeCallbacks(hideControlsRunnable);
            } else {
                showControls();
                handler.removeCallbacks(hideControlsRunnable);
                handler.postDelayed(hideControlsRunnable, 10000);
            }
        });

        videoWebView.setOnClickListener(v -> {
            Log.d("TvFragment", "Video Web view clicked");
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

        initTvSelectionFragment();

        fullScreenIv.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TvLandscapeActivity.class);
            Uri videoUri1 = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.videohz);
            intent.putExtra("VIDEO_URI", videoUri1.toString()); // Pass the URI as a string
//            intent.putExtra("CURRENT_POSITION", videoView.getCurrentPosition()); // Pass the current playback position
            startActivityForResult(intent, REQUEST_CODE_TV_LANDSCAPE);
        });

        return view;
    }

//    private MediaSource buildMediaSource(Uri uri) {
//        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(getActivity());
//        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
//    }

    private void startProgressUpdate() {
        final Handler handler = new Handler();
        final int delay = 1000; // Update every second

        handler.postDelayed(new Runnable() {
            public void run() {
                // Inject the JavaScript to get current time and duration
                String js = "(function() { return { currentTime: player.getCurrentTime(), duration: player.getDuration() }; })()";
                videoWebView.evaluateJavascript(js, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        // Parse the JSON string and update UI
                        try {
                            JSONObject jsonObject = new JSONObject(value);
                            double currentTime = jsonObject.getDouble("currentTime");
                            double duration = jsonObject.getDouble("duration");

                            playerSBar.setMax(100);
                            playerSBar.setProgress((int) ((currentTime / duration) * 100));

                            updateProgressBarAndTiming(currentTime, duration);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void updateProgressBarAndTiming(double currentTime, double duration) {
        if (duration > 0) {
            int progress = (int) ((currentTime / duration) * 100);
            playerSBar.setProgress(progress);

            String formattedCurrentTime = formatTime(currentTime);
            String formattedDuration = formatTime(duration);

            playerTimingTv.setText(String.format("%s / %s", formattedCurrentTime, formattedDuration));
        }
    }

    private String formatTime(double timeInSeconds) {
        int minutes = (int) (timeInSeconds / 60);
        int seconds = (int) (timeInSeconds % 60);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
//    private void updateSeekBar() {
//        playerSBar.setProgress(videoView.getCurrentPosition());
//        updatePlayerTiming();
//        handler.postDelayed(updateSeekBarRunnable = this::updateSeekBar, 1000);
//    }

//    private void updatePlayerTiming(String currentTime) {
//        playerTimingTv.setText(currentTime);
////        int currentPos = videoView.getCurrentPosition();
////        playerTimingTv.setText(String.format("%02d:%02d:%02d",
////                (currentPos / 1000) / 3600, ((currentPos / 1000) % 3600) / 60, (currentPos / 1000) % 60));
//    }

    private void showControls() {
        getActivity().runOnUiThread(() -> {
        Log.d("TvFragment", "Showing controls");
        liveTv.setVisibility(View.VISIBLE);
        playerTimingTv.setVisibility(View.VISIBLE);
        minmaxScreenIv.setVisibility(View.VISIBLE);
        shareIv.setVisibility(View.VISIBLE);
        settingsIv.setVisibility(View.VISIBLE);
        fastBackwardIv.setVisibility(View.VISIBLE);
        playIv.setVisibility(View.VISIBLE);
        fastForwardIv.setVisibility(View.VISIBLE);
        fullScreenIv.setVisibility(View.VISIBLE);
        playerSBar.setVisibility(View.VISIBLE);
        videoCl.setFocusable(true);
        });
    }

    private void hideControls() {
        getActivity().runOnUiThread(() -> {
        Log.d("TvFragment", "Hiding controls");
        liveTv.setVisibility(View.GONE);
        playerTimingTv.setVisibility(View.GONE);
        minmaxScreenIv.setVisibility(View.GONE);
        shareIv.setVisibility(View.GONE);
        settingsIv.setVisibility(View.GONE);
        fastBackwardIv.setVisibility(View.GONE);
        playIv.setVisibility(View.GONE);
        fastForwardIv.setVisibility(View.GONE);
        fullScreenIv.setVisibility(View.GONE);
        playerSBar.setVisibility(View.GONE);
        });
    }

    private void initTvSelectionFragment() {
        TvSelectionFragment tvSelectionFragment = new TvSelectionFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.tvFrameLayout, tvSelectionFragment);
        transaction.commit();
        Log.d("TvFragment", "TvSelectionFragment transaction committed");
    }


    @Override
    public void onTimingSelected(int position) {
        // Initialize tvProgramRecItemAdapter if not already initialized
        if (tvProgramRecItemAdapter == null) {
            tvProgramRecItemAdapter = new TvProgramRecItemAdapter(getActivity(), getProgramsForTiming(position));
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
        handler.removeCallbacks(updateSeekBarRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateSeekBar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TV_LANDSCAPE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // Retrieve the video URI and playback position from the result
                String videoUriString = data.getStringExtra("VIDEO_URI");
                int currentPosition = data.getIntExtra("CURRENT_POSITION", 0);

//                if (videoUriString != null) {
//                    Uri videoUri = Uri.parse(videoUriString);
//                    videoView.setVideoURI(videoUri);
//                    videoView.seekTo(currentPosition);
//                    videoView.start();
//                    playIv.setImageResource(android.R.drawable.ic_media_pause);
//                    liveTv.setText("Live");
//                    liveTv.setBackgroundResource(R.drawable.lgblackcircle_bg);
//                }
            }
        }
    }

    // JavaScript interface for communication between WebView and Android
    public class WebAppInterface {
        private Context activity;

        WebAppInterface(Context activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public void sendVideoId(String videoId) {
            Log.d("WebAppInterface", "sendVideoId called with videoId: " + videoId);
            // Construct the YouTube URL
            String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

            // Share the video URL
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, videoUrl);
            activity.startActivity(Intent.createChooser(shareIntent, "Share Video URL"));
        }
//        @JavascriptInterface
//        public void updatePlayerTiming(String currentTime) {
//            activity.runOnUiThread(() -> playerTimingTv.setText(currentTime));
//        }
    }


    private void openSettingsDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quality);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ConstraintLayout constraintLayout = dialog.findViewById(R.id.constraint);
        TextView qualityVal = dialog.findViewById(R.id.qualityVal);
        SeekBar qualitySbar = dialog.findViewById(R.id.qualitySeekbar);

        // Configure SeekBar for quality selection
        qualitySbar.setMax(100);
//        qualitySbar.setProgress(100); // Default to 25%

        qualitySbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String quality;
                if (progress < 25) {
                    quality = "small"; // 360p
                    qualityVal.setText("Low (360p)");
                    videoWebView.evaluateJavascript("setPlaybackQuality('small');", null);
                } else if (progress < 50) {
                    quality = "medium"; // 480p
                    qualityVal.setText("Medium (480p)");
                } else if (progress < 75) {
                    quality = "large"; // 720p
                    qualityVal.setText("High (720p)");
                } else {
                    quality = "hd1080"; // 1080p
                    qualityVal.setText("HD (1080p)");
                    videoWebView.evaluateJavascript("setPlaybackQuality('hd1080');", null);
                }
                // Call JavaScript function to set quality
                videoWebView.evaluateJavascript("setPlaybackQuality('" + quality + "')", null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Add GestureDetector to detect swipe down
        GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
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