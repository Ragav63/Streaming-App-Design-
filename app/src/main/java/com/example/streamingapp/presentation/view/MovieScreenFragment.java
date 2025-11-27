package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
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

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentMovieScreenBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

public class MovieScreenFragment extends Fragment {
    private FragmentMovieScreenBinding binding;
    private boolean isDownloaded = false;
    private boolean isFavourite = false;

    private List<MovieItems> movieItemsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovieScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Get bundle
        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(requireContext(), "Missing data", Toast.LENGTH_SHORT).show();
            return;
        }

        int image = args.getInt("imageResource");
        String title = args.getString("title");
        String rating = args.getString("rating");
        String year = args.getString("year");
        String country = args.getString("country");
        String genre = args.getString("genre");
        String duration = args.getString("duration");
        String description = args.getString("description");

        movieItemsList = args.getParcelableArrayList("popularMovieItemsList");

        // Set UI
        Glide.with(requireContext()).load(image).into(binding.movieScreenIv);
        binding.titleTv.setText(title);
        binding.ratingTv.setText(rating);
        binding.yearTv.setText(year);
        binding.originTv.setText(country);
        binding.genreTv.setText(genre);
        binding.durationTv.setText(duration);
        binding.descriptionTv.setText(description);

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
                openDownloadDialog(binding.movieScreenIv, title);
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

        initTabs();
    }


    private void initTabs() {

        // Default → TrailersFragment
        replaceInnerFragment(new TrailersFragment());

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Trailers"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("More Like This"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("About"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
                        replaceInnerFragment(new TrailersFragment());
                        break;

                    case 1:
                        replaceInnerFragment(MoreLikeThisFragment.newInstanceWithMovies(movieItemsList));
                        break;

                    case 2:
                        replaceInnerFragment(AboutFragment.newInstanceWithMovies(movieItemsList));
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
    private void openDownloadDialog(ImageView posterIv, String name) {

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_download);

        ImageView poster = dialog.findViewById(R.id.downloadIv);
        TextView title = dialog.findViewById(R.id.downloadTitleTv);
        SeekBar qualitySeekbar = dialog.findViewById(R.id.qualitySeekbar);
        TextView qualityVal = dialog.findViewById(R.id.qualityValTv);

        poster.setImageDrawable(posterIv.getDrawable());
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
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}