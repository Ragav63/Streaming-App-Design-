package com.example.streamingapp.presentation.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.data.model.ContentType;
import com.example.streamingapp.data.model.CrewMember;
import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.data.model.TvChannelUiItem;
import com.example.streamingapp.databinding.FragmentMoviePlayerScreenBinding;
import com.example.streamingapp.databinding.LandscapeTvPlayerScreenBinding;
import com.example.streamingapp.presentation.adapter.MovieLandItemAdapter;
import com.example.streamingapp.presentation.adapter.PopularMovieRecItemAdapter;
import com.example.streamingapp.presentation.utils.PlayerController;
import com.example.streamingapp.presentation.utils.PlayerUIHelper;
import com.example.streamingapp.presentation.utils.PopupMenuHelper;
import com.example.streamingapp.presentation.viewmodel.PlayerViewModel;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MoviePlayerScreenFragment extends DialogFragment {

    private FragmentMoviePlayerScreenBinding binding;
    private MovieItems currentItem;
    private List<MovieItems> movieItemsList;
    private MovieLandItemAdapter movieAdapter;
    private StreamingViewModel vm;
    private PlayerController playerController;
    private PlayerViewModel playerViewModel;
    private PlayerUIHelper uiHelper;
    private Runnable onDismissCallback;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isControlsVisible = false;
    private boolean isCurrentlyPlaying = false;
    private Player.Listener playerStateListener;


    public static MoviePlayerScreenFragment newInstance() {
        return new MoviePlayerScreenFragment();
    }

    public void setMovieItem(MovieItems movieItem) {
        this.currentItem = movieItem;
    }

    public void setOnDismissListener(Runnable cb) {
        this.onDismissCallback = cb;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            binding = FragmentMoviePlayerScreenBinding.inflate(inflater, container, false);

            // Initialize ViewModels
            vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);
            playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

            // Initialize UI Helper
            uiHelper = new PlayerUIHelper(requireContext(), getViewLifecycleOwner(), true);

            // Setup player controller
            setupPlayerController();

            // Setup UI listeners
            setupClickListeners();

        } catch (Exception ex) {
            Toast.makeText(requireContext(), "Error initializing player", Toast.LENGTH_LONG).show();
            dismissAllowingStateLoss();
            return null;
        }
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dlg = getDialog();
        if (dlg != null && dlg.getWindow() != null) {
            dlg.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dlg.getWindow().setBackgroundDrawable(null);
            dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        if (playerController != null && binding != null) {
            // Attach player to video view
            binding.videoView.setPlayer(playerController.getPlayer());
            binding.videoView.setUseController(false);

            // Start seekbar updates
            uiHelper.startSeekBarUpdates(binding, playerController, playerViewModel);
            uiHelper.scheduleHideControls(binding, 5000);

            // Load movie data
            loadMovieData();


        }
    }

    private void setupPlayerController() {
        // Reuse player if already in ViewModel
        ExoPlayer existing = playerViewModel.getExoPlayer();
        if (existing != null) {
            playerController = new PlayerController(requireContext(), existing);
        } else {
            playerController = new PlayerController(requireContext());
            playerViewModel.setExoPlayer(playerController.getPlayer());
        }

        // Setup player state listener
        setupPlayerStateListener();

        // Load and play current movie
        if (currentItem != null && currentItem.getUrl() != null) {
            playerController.setMediaItem(MediaItem.fromUri(Uri.parse(currentItem.getUrl())));
            playerController.prepare();
            playerController.play();
            isCurrentlyPlaying = true;
            uiHelper.setCurrentPlayState(true);

            // Update ViewModel state
            playerViewModel.updateState(new PlayerViewModel.PlayerState(
                    currentItem.getTitle(),
                    currentItem.getImdb_rating(),
                    1,
                    1,
                    true,
                    0,
                    0,
                    1.0f,
                    false,
                    false
            ));
        }
    }

    private void setupPlayerStateListener() {
        playerStateListener = new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                requireActivity().runOnUiThread(() -> {
                    if (state == Player.STATE_READY) {
                        uiHelper.startSeekBarUpdates(binding, playerController, playerViewModel);
                        uiHelper.updatePlayButtonImmediate(binding, playerController.isPlaying());
                        isCurrentlyPlaying = playerController.isPlaying();
                    } else if (state == Player.STATE_ENDED) {
                        uiHelper.updatePlayButton(binding, false);
                        playerViewModel.updatePlaying(false);
                        isCurrentlyPlaying = false;
                    }
                });
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                requireActivity().runOnUiThread(() -> {
                    isCurrentlyPlaying = isPlaying;
                    uiHelper.setCurrentPlayState(isPlaying);
                    uiHelper.updatePlayButton(binding, isPlaying);
                    playerViewModel.updatePlaying(isPlaying);
                });
            }
        };

        playerController.addPlayerListener(playerStateListener);
    }

    private void setupClickListeners() {
        binding.linearFrame.setVisibility(View.GONE);

        // Play/Pause
        binding.playIv.setOnClickListener(v -> {
            if (playerController != null) {
                isCurrentlyPlaying = !isCurrentlyPlaying;
                uiHelper.setCurrentPlayState(isCurrentlyPlaying);
                uiHelper.updatePlayButtonImmediate(binding, isCurrentlyPlaying);
                playerController.togglePlayPause();
                playerViewModel.updatePlaying(playerController.isPlaying());
                uiHelper.scheduleHideControls(binding, 5000);
            }
        });

        // Seek backward
        binding.backwardIv.setOnClickListener(v -> {
            if (playerController != null) {
                uiHelper.setSeeking(true);
                playerController.seekBackward(10000);
                handler.postDelayed(() -> {
                    uiHelper.restorePlayButtonAfterSeek(binding);
                }, 300);
            }
            showControlsImmediate();
            uiHelper.scheduleHideControls(binding, 5000);
        });

        // Seek forward
        binding.forwardIv.setOnClickListener(v -> {
            if (playerController != null) {
                uiHelper.setSeeking(true);
                playerController.seekForward(10000);
                handler.postDelayed(() -> {
                    uiHelper.restorePlayButtonAfterSeek(binding);
                }, 300);
            }
            showControlsImmediate();
            uiHelper.scheduleHideControls(binding, 5000);
        });

        // Exit fullscreen
        binding.fullScreenIv.setOnClickListener(v -> dismiss());

        // PIP
        binding.minScreenIv.setOnClickListener(v -> {
            uiHelper.hideControls(binding);
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).enterPictureInPictureMode();
            }
        });

        // Settings
        binding.settingsIv.setOnClickListener(v -> {
            PopupMenuHelper.showPlayerSettingsMenu(binding.settingsIv, requireContext(), playerController.getPlayer());
            uiHelper.scheduleHideControls(binding, 5000);
        });


        // List mode
        binding.listMode.setOnClickListener(v -> {
            if (currentItem == null) return;

            if (uiHelper.areControlsVisible(binding)) {
                uiHelper.hideControls(binding);
                uiHelper.cancelHideControls();
            }

            binding.linearFrame.setVisibility(View.VISIBLE);
            if (binding.touchOverlay != null) {
                binding.touchOverlay.setClickable(false);
            }
        });



        binding.rlClose.setOnClickListener(v -> {
            binding.linearFrame.setVisibility(View.GONE);
            if (binding.touchOverlay != null) {
                binding.touchOverlay.setClickable(true);
            }
            showControlsImmediate();
        });

        // Touch overlay
        binding.touchOverlay.setOnClickListener(v -> {
            if (uiHelper.areControlsVisible(binding)) {
                uiHelper.hideControls(binding);
                uiHelper.cancelHideControls();
            } else {
                uiHelper.showControls(binding);
                uiHelper.scheduleHideControls(binding, 5000);
            }
        });

        // SeekBar listener
        binding.playerSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && playerController != null) {
                    long duration = playerController.getDuration();
                    long position = (progress * duration) / 100;
                    playerController.seekTo(position);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                uiHelper.setSeeking(true);
                uiHelper.cancelHideControls();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.postDelayed(() -> {
                    uiHelper.restorePlayButtonAfterSeek(binding);
                }, 300);
                uiHelper.scheduleHideControls(binding, 5000);
            }
        });
    }

    private void loadMovieData() {
        if (currentItem != null) {
            binding.titleTv.setText(currentItem.getTitle());

            // Load related movies
            vm.loadMovies();
            vm.getMovieLiveData().observe(getViewLifecycleOwner(), fullList -> {
                List<CrewMember> currentCrew = currentItem.getCrew();

                if (currentCrew == null || currentCrew.isEmpty()) {
                    movieItemsList = fullList;  // fallback
                } else {
                    // Extract crew names of current movie
                    List<String> crewNames = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        crewNames = currentCrew.stream()
                                .map(CrewMember::getName)
                                .map(String::trim)
                                .toList();
                    }

                    // Filter movies having at least one matching crew member
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        List<String> finalCrewNames = crewNames;
                        movieItemsList = fullList.stream()
                                .filter(movie -> movie.getCrew() != null)
                                .filter(movie -> movie.getCrew().stream()
                                        .anyMatch(cm -> finalCrewNames.contains(cm.getName().trim()))
                                )
                                .filter(movie -> !movie.getTitle().equals(currentItem.getTitle()))
                                .toList();
                    }
                }

                // Initialize tabs AFTER data is loaded
                initMovies();
            });
        }
    }

    private void initMovies() {
        // Set initial fragment
        if (currentItem == null) {
            return;
        }

        binding.recVMovies.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        if (movieItemsList != null && !movieItemsList.isEmpty()) {
            movieAdapter = new MovieLandItemAdapter(requireContext(), movieItemsList, (movie, pos) -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("movieItem",movie);
                // Navigate using NavController
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.movieScreenActivity, bundle);
            });
            binding.recVMovies.setAdapter(movieAdapter);
            movieAdapter.submitList(movieItemsList);
        }

    }



    private void showControlsImmediate() {
        requireActivity().runOnUiThread(() -> {
            if (binding == null) return;

            isControlsVisible = true;
            uiHelper.showControls(binding);
            uiHelper.restorePlayButtonAfterSeek(binding);
        });
    }

    private void hideControlsImmediate() {
        requireActivity().runOnUiThread(() -> {
            if (binding == null) return;

            isControlsVisible = false;
            uiHelper.hideControls(binding);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.cancelAll();
        if (playerController != null) {
            playerViewModel.updatePlaybackState(
                    playerController.isPlaying(),
                    playerController.getCurrentPosition(),
                    playerController.getDuration()
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (playerController != null) {
            isCurrentlyPlaying = playerController.isPlaying();
            uiHelper.setCurrentPlayState(isCurrentlyPlaying);
            uiHelper.updatePlayButtonImmediate(binding, isCurrentlyPlaying);
            uiHelper.startSeekBarUpdates(binding, playerController, playerViewModel);
            uiHelper.scheduleHideControls(binding, 5000);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if (playerController != null && currentItem != null) {

            long watched = playerController.getCurrentPosition();
            long duration = playerController.getDuration();

            boolean fullyWatched = duration > 0 && watched >= (duration * 0.95);

            HistoryItems historyItem = new HistoryItems(
                    currentItem.getId(),
                    currentItem.getTitle(),
                    getCurrentTime(),
                    currentItem.getPoster(),
                    currentItem.getUrl(),
                    duration,
                    watched,
                    fullyWatched,
                    ContentType.MOVIE
            );
            vm.saveHistory(historyItem);
        }

        if (binding != null) {
            binding.videoView.setPlayer(null);
        }

        if (playerController != null && playerStateListener != null) {
            playerController.removePlayerListener(playerStateListener);
            playerStateListener = null;
        }

        if (onDismissCallback != null) {
            onDismissCallback.run();
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        uiHelper.cleanup();
        binding = null;
    }
}