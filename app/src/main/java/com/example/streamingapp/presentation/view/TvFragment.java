package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.databinding.FragmentTvBinding;
import com.example.streamingapp.presentation.adapter.TvProgramRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TvFragment extends Fragment {

    private FragmentTvBinding binding;
    private Handler handler = new Handler();
    private Runnable hideControlsRunnable;
    private TvProgramRecItemAdapter tvProgramRecItemAdapter;
    private static final int REQUEST_CODE_TV_LANDSCAPE = 1001;
    private NavController navController;
    private StreamingViewModel vm;
    private Handler progressHandler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTvBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        setupVideoWebView();
        setupClickListeners();
        setupControls();
        initTvSelectionFragment();
        setupFragmentResultListener();

        if (tvProgramRecItemAdapter == null) {
            tvProgramRecItemAdapter = new TvProgramRecItemAdapter(item -> {
                Toast.makeText(requireContext(), "Currently Watching: " + item.getCurrentProgramName(), Toast.LENGTH_SHORT).show();
            });
        } else {
            tvProgramRecItemAdapter.submitList(vm.getNowOnTvItems());
        }
    }

    private void setupVideoWebView() {
        if (binding == null) return;

        WebSettings webSettings = binding.videoView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        binding.videoView.setWebChromeClient(new WebChromeClient());
        binding.videoView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // NULL CHECK - This prevents the crash
                if (binding == null || binding.videoView == null) {
                    Log.d("WebView", "Binding is null in onPageFinished, skipping");
                    return;
                }

                startProgressUpdate();
                Log.d("WebView", "Page loaded: " + url);
                binding.videoView.addJavascriptInterface(new WebAppInterface(getContext()), "AndroidInterface");
            }
        });

        String videoHtml = "<html><body style='margin:0;padding:0;overflow:hidden;'>"
                + "<div id='player'></div>"
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
                + "</script>"
                + "<script src='https://www.youtube.com/iframe_api'></script>"
                + "</body></html>";

        binding.videoView.loadData(videoHtml, "text/html", "UTF-8");
    }

    private void setupClickListeners() {
        if (binding == null) return;

        binding.shareIv.setOnClickListener(v -> shareVideo());
        binding.settingsIv.setOnClickListener(v -> openSettingsDialog());
        binding.playIv.setOnClickListener(v -> togglePlayPause());
        binding.liveTv.setOnClickListener(v -> goToLive());
        binding.fastBackwardRl.setOnClickListener(v -> seekBackward());
        binding.fastForwardRl.setOnClickListener(v -> seekForward());
        binding.fullScreenIv.setOnClickListener(v -> openFullScreen());

        binding.videoCl.setOnClickListener(v -> toggleControlsVisibility());
        binding.videoView.setOnClickListener(v -> toggleControlsVisibility());

        binding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && binding != null) {
                    double duration = binding.playerSBar.getMax();
                    double currentTime = (progress / 100.0) * duration;
                    if (binding.videoView != null) {
                        binding.videoView.evaluateJavascript("player.seekTo(" + currentTime + ", true);", null);
                    }
                    binding.playerTimingTv.setText(formatTime(currentTime));
                }
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
        if (binding == null || binding.videoView == null) return;

        binding.videoView.evaluateJavascript("player.getVideoData().video_id;", videoId -> {
            if (videoId != null && !videoId.isEmpty()) {
                videoId = videoId.replace("\"", "");
                Log.d("ShareButton", "Video ID retrieved: " + videoId);

                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, videoUrl);
                startActivity(Intent.createChooser(shareIntent, "Share Video URL"));
            } else {
                Toast.makeText(getContext(), "Failed to get video ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePlayPause() {
        if (binding == null || binding.videoView == null) return;

        binding.videoView.evaluateJavascript("player.getPlayerState();", value -> {
            if (value != null && binding != null) {
                if (Integer.parseInt(value) == 1) {
                    binding.videoView.evaluateJavascript("player.pauseVideo();", null);
                    binding.playIv.setImageResource(android.R.drawable.ic_media_play);
                    binding.liveTv.setText("To the Live");
                    binding.liveTv.setBackgroundResource(R.drawable.lgtransparentwhitestroke_bg);
                } else {
                    binding.videoView.evaluateJavascript("player.playVideo();", null);
                    binding.playIv.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
    }

    private void goToLive() {
        if (binding == null || binding.videoView == null) return;

        if ("To the Live".equals(binding.liveTv.getText().toString())) {
            binding.videoView.evaluateJavascript("player.getPlayerState();", value -> {
                if (value != null && binding != null) {
                    if (Integer.parseInt(value) == 1) {
                        binding.videoView.evaluateJavascript("player.pauseVideo();", null);
                        binding.playIv.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        binding.videoView.evaluateJavascript("player.playVideo();", null);
                        binding.playIv.setImageResource(android.R.drawable.ic_media_pause);
                        binding.liveTv.setText("Live");
                        binding.liveTv.setBackgroundResource(R.drawable.lgblackcircle_bg);
                        showControls();
                        handler.postDelayed(hideControlsRunnable, 10000);
                    }
                }
            });
        }
    }

    private void seekBackward() {
        if (binding == null || binding.videoView == null) return;

        binding.videoView.evaluateJavascript("seekBackward();", value ->
                Log.d("WebView", "seekBackward executed: " + value));
    }

    private void seekForward() {
        if (binding == null || binding.videoView == null) return;

        binding.videoView.evaluateJavascript("seekForward();", value ->
                Log.d("WebView", "seekForward executed: " + value));
    }

    private void openFullScreen() {
        if (binding == null) return;

        Bundle bundle = new Bundle();
        bundle.putString("VIDEO_URI", "android.resource://" + getActivity().getPackageName() + "/" + R.raw.videohz);
        navController.navigate(R.id.action_tvFragment_to_tvLandscapeActivity, bundle);
    }

    private void toggleControlsVisibility() {
        if (binding == null) return;

        if (binding.playIv.getVisibility() == View.VISIBLE) {
            hideControls();
            handler.removeCallbacks(hideControlsRunnable);
        } else {
            showControls();
            handler.removeCallbacks(hideControlsRunnable);
            handler.postDelayed(hideControlsRunnable, 10000);
        }
    }

    private void startProgressUpdate() {
        final int delay = 1000;
        progressHandler.postDelayed(progressRunnable, delay);
    }

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            // NULL CHECK - This prevents callbacks after view destruction
            if (binding == null || binding.videoView == null) {
                Log.d("TvFragment", "Binding is null in progressRunnable, stopping updates");
                return;
            }

            String js = "(function() { return { currentTime: player.getCurrentTime(), duration: player.getDuration() }; })()";

            binding.videoView.evaluateJavascript(js, value -> {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    double currentTime = jsonObject.getDouble("currentTime");
                    double duration = jsonObject.getDouble("duration");
                    updateProgressBarAndTiming(currentTime, duration);
                } catch (Exception ignored) {}
            });

            progressHandler.postDelayed(this, 1000);
        }
    };

    private void updateProgressBarAndTiming(double currentTime, double duration) {
        if (binding == null) return;

        if (duration > 0) {
            int progress = (int) ((currentTime / duration) * 100);
            binding.playerSBar.setProgress(progress);

            String formattedCurrentTime = formatTime(currentTime);
            String formattedDuration = formatTime(duration);
            binding.playerTimingTv.setText(String.format("%s / %s", formattedCurrentTime, formattedDuration));
        }
    }

    private String formatTime(double timeInSeconds) {
        int minutes = (int) (timeInSeconds / 60);
        int seconds = (int) (timeInSeconds % 60);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void showControls() {
        requireActivity().runOnUiThread(() -> {
            if (binding == null) return;

            Log.d("TvFragment", "Showing controls");
            binding.liveTv.setVisibility(View.VISIBLE);
            binding.playerTimingTv.setVisibility(View.VISIBLE);
            binding.minScreenIv.setVisibility(View.VISIBLE);
            binding.shareIv.setVisibility(View.VISIBLE);
            binding.settingsIv.setVisibility(View.VISIBLE);
            binding.fastBackwardRl.setVisibility(View.VISIBLE);
            binding.playIv.setVisibility(View.VISIBLE);
            binding.fastForwardRl.setVisibility(View.VISIBLE);
            binding.fullScreenIv.setVisibility(View.VISIBLE);
            binding.playerSBar.setVisibility(View.VISIBLE);
            binding.videoCl.setFocusable(true);
        });
    }

    private void hideControls() {
        requireActivity().runOnUiThread(() -> {
            if (binding == null) return;

            Log.d("TvFragment", "Hiding controls");
            binding.liveTv.setVisibility(View.GONE);
            binding.playerTimingTv.setVisibility(View.GONE);
            binding.minScreenIv.setVisibility(View.GONE);
            binding.shareIv.setVisibility(View.GONE);
            binding.settingsIv.setVisibility(View.GONE);
            binding.fastBackwardRl.setVisibility(View.GONE);
            binding.playIv.setVisibility(View.GONE);
            binding.fastForwardRl.setVisibility(View.GONE);
            binding.fullScreenIv.setVisibility(View.GONE);
            binding.playerSBar.setVisibility(View.GONE);
        });
    }

    private void initTvSelectionFragment() {
        if (binding == null) return;

        TvSelectionFragment tvSelectionFragment = new TvSelectionFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.tvFrameLayout, tvSelectionFragment);
        transaction.commit();
        Log.d("TvFragment", "TvSelectionFragment transaction committed");
    }

    private List<TvItems> getProgramsForTiming(int position) {
        return new ArrayList<>();
    }

    private void setupFragmentResultListener() {
        getParentFragmentManager().setFragmentResultListener("tv_landscape_result", this, (requestKey, result) -> {
            if (requestKey.equals("tv_landscape_result")) {
                String videoUri = result.getString("VIDEO_URI");
                int position = result.getInt("CURRENT_POSITION");

                Log.d("TvFragment", "Received result from landscape: " + videoUri + ", position: " + position);

                if (videoUri != null && binding != null && binding.videoView != null) {
                    // Update your WebView playback if needed
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(hideControlsRunnable);
        progressHandler.removeCallbacks(progressRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restart progress updates if needed
        if (binding != null && binding.videoView != null) {
            startProgressUpdate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove all callbacks first
        progressHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);

        // Clear the WebView to prevent memory leaks
        if (binding != null && binding.videoView != null) {
            binding.videoView.setWebViewClient(null);
            binding.videoView.setWebChromeClient(null);
            binding.videoView.loadUrl("about:blank");
            binding.videoView.destroy();
        }

        binding = null;
    }

    private void openSettingsDialog() {
        if (binding == null) return;

        final Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quality);

        View constraintLayout = dialog.findViewById(R.id.constraint);
        TextView qualityVal = dialog.findViewById(R.id.qualityVal);
        SeekBar qualitySbar = dialog.findViewById(R.id.qualitySeekbar);

        qualitySbar.setMax(100);

        qualitySbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String quality;
                if (progress < 25) {
                    quality = "small";
                    qualityVal.setText("Low (360p)");
                } else if (progress < 50) {
                    quality = "medium";
                    qualityVal.setText("Medium (480p)");
                } else if (progress < 75) {
                    quality = "large";
                    qualityVal.setText("High (720p)");
                } else {
                    quality = "hd1080";
                    qualityVal.setText("HD (1080p)");
                }
                if (binding != null && binding.videoView != null) {
                    binding.videoView.evaluateJavascript("setPlaybackQuality('" + quality + "')", null);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        GestureDetector gestureDetector = new GestureDetector(requireActivity(), new GestureDetector.SimpleOnGestureListener() {
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

    public class WebAppInterface {
        private Context activity;

        WebAppInterface(Context activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public void sendVideoId(String videoId) {
            Log.d("WebAppInterface", "sendVideoId called with videoId: " + videoId);
            String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, videoUrl);
            activity.startActivity(Intent.createChooser(shareIntent, "Share Video URL"));
        }
    }
}