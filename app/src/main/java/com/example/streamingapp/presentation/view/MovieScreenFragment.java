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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.CrewMember;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.R;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.databinding.FragmentMovieScreenBinding;
import com.example.streamingapp.presentation.adapter.GenreFilterAdapter;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovieScreenFragment extends Fragment {
    private FragmentMovieScreenBinding binding;
    private boolean isDownloaded = false;
    private boolean isFavourite = false;
    private List<MovieItems> movieItemsList;
    private MovieItems currentItem;
    private StreamingViewModel viewModel;
    private GenreFilterAdapter genreFilterAdapter;
    private MoviePlayerScreenFragment fullscreenDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovieScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NewApi")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

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
                List<String> crewNames = currentCrew.stream()
                        .map(CrewMember::getName)
                        .map(String::trim)
                        .toList();

                // Filter movies having at least one matching crew member
                movieItemsList = fullList.stream()
                        .filter(movie -> movie.getCrew() != null)
                        .filter(movie -> movie.getCrew().stream()
                                .anyMatch(cm -> crewNames.contains(cm.getName().trim()))
                        )
                        .filter(movie -> !movie.getTitle().equals(currentItem.getTitle())) // remove same movie
                        .toList();
            }
        });


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
                new ArrayList<>(),
                true,   // assign-only mode
                null    // no selection callback needed
        );

        binding.recVGenre.setAdapter(genreFilterAdapter);

        genreFilterAdapter.submitList(
                mapGenresToPickItems(currentItem.getGenres())
        );

        binding.tvTimingGenre.setText(" · " +
                currentItem.getFormattedDuration()
                        + " · "
                        + currentItem.getGenresAsString()
        );
        binding.descriptionTv.setText(currentItem.getPlot());

        binding.backIv.setOnClickListener(v -> requireActivity().onBackPressed());

        int defaultTint = ContextCompat.getColor(requireContext(), R.color.white);
        int selectedTint = ContextCompat.getColor(requireContext(), R.color.bluemain);

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

        // Favourite button
        binding.favIv.setOnClickListener(v -> {
            if (isFavourite) {
                binding.favIv.setColorFilter(defaultTint, PorterDuff.Mode.SRC_IN);
                isFavourite = false;
            } else {
                binding.favIv.setColorFilter(selectedTint, PorterDuff.Mode.SRC_IN);
                isFavourite = true;
            }
        });

        // Share
        binding.shareIv.setOnClickListener(v -> {
            binding.shareIv.setColorFilter(selectedTint, PorterDuff.Mode.SRC_IN);
            Toast.makeText(requireContext(), "Share feature here", Toast.LENGTH_SHORT).show();
            binding.shareIv.postDelayed(() ->
                    binding.shareIv.setColorFilter(defaultTint, PorterDuff.Mode.SRC_IN), 800);
        });

        binding.playIv.setOnClickListener(v -> openFullScreen());


        initTabs();
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
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 25) qualityVal.setText("360p");
                else if (progress < 50) qualityVal.setText("480p");
                else if (progress < 75) qualityVal.setText("720p");
                else qualityVal.setText("1080p");
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
}