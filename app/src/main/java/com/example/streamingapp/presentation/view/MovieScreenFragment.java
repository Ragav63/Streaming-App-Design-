package com.example.streamingapp.presentation.view;

import static com.example.streamingapp.presentation.view.TrailersFragment.newInstanceWithMovies;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.ContentType;
import com.example.streamingapp.data.model.CrewMember;
import com.example.streamingapp.data.model.FavouriteItem;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.R;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.databinding.FragmentMovieScreenBinding;
import com.example.streamingapp.presentation.adapter.GenreFilterAdapter;
import com.example.streamingapp.presentation.utils.UtilFunctions;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovieScreenFragment extends Fragment {
    private FragmentMovieScreenBinding binding;
    private boolean isDownloaded = false;
    private List<MovieItems> movieItemsList;
    private MovieItems currentItem;
    private StreamingViewModel viewModel;
    private GenreFilterAdapter genreFilterAdapter;
    private MoviePlayerScreenFragment fullscreenDialog;
    private int defaultTint;
    private int selectedTint;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovieScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        defaultTint = ContextCompat.getColor(requireContext(), R.color.white);
        selectedTint = ContextCompat.getColor(requireContext(), R.color.bluemain);

        viewModel = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);
        // Get bundle
        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(requireContext(), "Missing data", Toast.LENGTH_SHORT).show();
            return;
        }

        currentItem = args.getParcelable("movieItem");

        viewModel.loadMovies();


        viewModel.getMovieLiveData().observe(getViewLifecycleOwner(), fullList -> {

            List<CrewMember> currentCrew = currentItem.getCrew();

            if (currentCrew == null || currentCrew.isEmpty()) {
                movieItemsList = fullList;  // fallback
            } else {

                // Extract crew names of current movie
                List<String> crewNames = new ArrayList<>();
                for (CrewMember member : currentCrew) {
                    if (member.getName() != null) {
                        crewNames.add(member.getName().trim());
                    }
                }


                // Filter movies having at least one matching crew member
                List<MovieItems> filtered = new ArrayList<>();

                for (MovieItems movie : fullList) {
                    if (movie.getCrew() == null) continue;
                    if (movie.getTitle().equals(currentItem.getTitle())) continue;

                    for (CrewMember cm : movie.getCrew()) {
                        if (cm.getName() != null &&
                                crewNames.contains(cm.getName().trim())) {
                            filtered.add(movie);
                            break;
                        }
                    }
                }

                movieItemsList = filtered;

            }
        });

        viewModel.getHistoryLiveData().observe(getViewLifecycleOwner(), this::applyHistoryToCurrentItem);


        // Set UI
        Glide.with(requireContext()).load(currentItem.getPoster()).into(binding.movieScreenIv);
        binding.titleTv.setText(currentItem.getTitle());
        float imdb = Float.parseFloat(currentItem.getImdb_rating()); // 0–10

        Drawable drawable = binding.starView.getDrawable();
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            Drawable progress = layerDrawable.findDrawableByLayerId(android.R.id.progress);

            if (progress instanceof ClipDrawable) {
                // ClipDrawable level range: 0–10000
                int level = (int) (imdb / 10f * 10000);
                ((ClipDrawable) progress).setLevel(level);
            }
        }

        binding.ratingTv.setText(String.valueOf(imdb));


        binding.yearTv.setText(currentItem.getYear());
        binding.originTv.setText(currentItem.getCountry());

        binding.recVGenre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        genreFilterAdapter = new GenreFilterAdapter(
                requireContext(),
                true,   // assign-only mode
                null    // no selection callback
        );


        binding.recVGenre.setAdapter(genreFilterAdapter);

        genreFilterAdapter.submitList(
                mapGenresToPickItems(currentItem.getGenres())
        );

        String genres = currentItem.getGenresAsString();

        binding.tvTimingGenre.setText(
                genres.isEmpty()
                        ? currentItem.getFormattedDuration()
                        : getString(
                        R.string.timing_genre_format,
                        currentItem.getFormattedDuration(),
                        genres
                )
        );

        binding.descriptionTv.setText(currentItem.getPlot());

        binding.backIv.setOnClickListener(v -> requireActivity().onBackPressed());



        // Download button
        binding.downloadIv.setOnClickListener(v -> {
            if (isDownloaded) {
                Toast.makeText(requireContext(), "Already downloaded", Toast.LENGTH_SHORT).show();
            } else {
                binding.downloadIv.setColorFilter(selectedTint, PorterDuff.Mode.SRC_IN);
                isDownloaded = true;
                openDownloadDialog(currentItem.getPoster(), currentItem.getTitle());
            }
        });


        binding.favIv.setOnClickListener(v -> {
            FavouriteItem favItem =
                    new FavouriteItem(ContentType.MOVIE, currentItem);

            if (viewModel.isFavourite(favItem)) {
                viewModel.removeFromFavourite(favItem);
            } else {
                viewModel.addToFavourite(favItem);
            }
        });

        viewModel.getFavouriteItems().observe(getViewLifecycleOwner(), list -> {
            FavouriteItem favItem =
                    new FavouriteItem(ContentType.MOVIE, currentItem);

            boolean fav = viewModel.isFavourite(favItem);
            updateFavouriteIcon(fav);
        });



        // Share
        binding.shareIv.setOnClickListener(v -> {
            binding.shareIv.setColorFilter(selectedTint, PorterDuff.Mode.SRC_IN);
            Toast.makeText(requireContext(), "Share feature here", Toast.LENGTH_SHORT).show();
            binding.shareIv.postDelayed(() ->
                    binding.shareIv.setColorFilter(defaultTint, PorterDuff.Mode.SRC_IN), 800);
        });

        binding.playIv.setOnClickListener(v -> openFullScreen());

        binding.btnContinueWatching.setOnClickListener(v->openFullScreen());


        initTabs();
    }

    private void updateFavouriteIcon(boolean isFavourite) {
        if (isFavourite) {
            binding.favIv.setImageResource(R.drawable.bookmarktintfull64px);
        } else {
            binding.favIv.setImageResource(R.drawable.bookmark64px);
        }
    }


    private void applyHistoryToCurrentItem(List<HistoryItems> historyList) {
        if (historyList == null || currentItem == null) {
            resetContinueUi();
            return;
        }

        HistoryItems history = null;

        for (HistoryItems h : historyList) {
            if (h.getTitle().equalsIgnoreCase(currentItem.getTitle())) {
                history = h;
                break;
            }
        }

        if (history == null) {
            resetContinueUi();
            return;
        }

        // ---- APPLY UI STATE ----
        binding.llViewed.setVisibility(View.VISIBLE);
        binding.playIv.setVisibility(View.GONE);

        long watchedMs = history.getWatchedMs();
        long totalMs = history.getDurationMs();

        if (totalMs > 0) {
            int progress = (int) ((watchedMs * 100f) / totalMs);
            binding.playerSBar.setProgress(progress);
        } else {
            binding.playerSBar.setProgress(0);
        }

        binding.tvContinueTiming.setText(UtilFunctions.formatTime(watchedMs));
    }

    private void resetContinueUi() {
        binding.llViewed.setVisibility(View.GONE);
        binding.playIv.setVisibility(View.VISIBLE);
        binding.playerSBar.setProgress(0);
        binding.tvContinueTiming.setText("");
    }



    private List<PickItem> mapGenresToPickItems(List<String> genres) {
        List<PickItem> list = new ArrayList<>();
        if (genres == null) return list;

        for (String genre : genres) {
            list.add(new PickItem(0, genre)); // img defaults to 0
        }
        return list;
    }

    private void openFullScreen() {
        if (currentItem == null) return;

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        if (fullscreenDialog == null) {
            fullscreenDialog = MoviePlayerScreenFragment.newInstance();
            fullscreenDialog.setMovieItem(currentItem);


            fullscreenDialog.setOnDismissListener(() -> {
                // restore portrait when dialog closes
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


            });
        }

        fullscreenDialog.show(requireActivity().getSupportFragmentManager(), "fullscreen_player");
    }


    private void initTabs() {

        // Default → TrailersFragment
        replaceInnerFragment(TrailersFragment.newInstanceWithMovies(currentItem));

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Trailers"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("More Like This"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("About"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
                        replaceInnerFragment (TrailersFragment.newInstanceWithMovies(currentItem));
                        break;

                    case 1:
                        replaceInnerFragment(MoreLikeThisFragment.newInstanceWithMovies(movieItemsList));
                        break;

                    case 2:
                        replaceInnerFragment(AboutFragment.newInstanceWithMovies(currentItem));
                        break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }


    private void replaceInnerFragment(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.framelayout.getId(), fragment)
                .commit();
    }


    @SuppressLint("InflateParams")
    private void openDownloadDialog(String posterIv, String name) {

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_download);

        ImageView poster = dialog.findViewById(R.id.downloadIv);
        TextView title = dialog.findViewById(R.id.downloadTitleTv);
        SeekBar qualitySeekbar = dialog.findViewById(R.id.qualitySeekbar);
        TextView qualityVal = dialog.findViewById(R.id.qualityValTv);

        Glide.with(requireContext()).load(posterIv).into(poster);
        title.setText(name);

        qualitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                qualityVal.setText(getQualityRes(progress));

            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.BOTTOM);
        }

        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @StringRes
    private int getQualityRes(int progress) {
        if (progress < 25) return R.string.quality_360p;
        if (progress < 50) return R.string.quality_480p;
        if (progress < 75) return R.string.quality_720p;
        return R.string.quality_1080p;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadHistory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}