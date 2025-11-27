package com.example.streamingapp.presentation.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSeriesScreenBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SeriesScreenFragment extends Fragment  {
    private FragmentSeriesScreenBinding binding;

    private static final int DEFAULT_TINT_COLOR = R.color.white;
    private static final int SELECTED_TINT_COLOR = R.color.bluemain;

    private List<SeriesItems> seriesItemsList;
    private boolean isDownloaded = false;
    private boolean isFavourite = false;

    private int imageResource;
    private String rating, title, year, genre, country, seasons, description;

    private SeasonFragment seasonFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSeriesScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        getData();
        setupUI();
        setupListeners();
        loadSeasonFragment();
        setupTabs();
    }

    private void getData() {
        if (getArguments() == null) return;

        imageResource = getArguments().getInt("imageResource", -1);
        rating = getArguments().getString("rating");
        title = getArguments().getString("title");
        year = getArguments().getString("year");
        country = getArguments().getString("country");
        genre = getArguments().getString("genre");
        seasons = getArguments().getString("seasons");
        description = getArguments().getString("description");

        seriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");

        if (seriesItemsList == null || seriesItemsList.isEmpty()) {
            Toast.makeText(requireContext(), "Series List Missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupUI() {
        if (imageResource != -1) {
            binding.seriesScreenIv.setImageResource(imageResource);
        }

        binding.titleTv.setText(title);
        binding.ratingTv.setText(rating);
        binding.yearTv.setText(year);
        binding.originTv.setText(country);
        binding.genreTv.setText(genre);
        binding.seasonsTv.setText("PG-13 " + seasons + " Seasons");
        binding.descriptionTv.setText(description);
    }

    private void setupListeners() {

        // Back
        binding.backIv.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack()
        );

        // Watch Now → Go to series player
        binding.watchNowTv.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("imageResource", imageResource);
            bundle.putString("title", title);
            bundle.putString("rating", rating);
            bundle.putParcelableArrayList("popularSeriesItemsList",
                    (ArrayList<? extends Parcelable>) seriesItemsList);

            NavHostFragment.findNavController(this)
                    .navigate(R.id.seriesPlayerScreenActivity, bundle);
        });

        // Download
        binding.downloadIv.setOnClickListener(v -> {
            if (isDownloaded) {
                Toast.makeText(requireContext(), "Already added to Download", Toast.LENGTH_SHORT).show();
            } else {
                binding.downloadIv.setColorFilter(ContextCompat.getColor(requireContext(), SELECTED_TINT_COLOR));
                openDownloadDialog();
                isDownloaded = true;
            }
        });

        // Favourite
        binding.favIv.setOnClickListener(v -> {
            isFavourite = !isFavourite;
            binding.favIv.setColorFilter(
                    ContextCompat.getColor(requireContext(),
                            isFavourite ? SELECTED_TINT_COLOR : DEFAULT_TINT_COLOR)
            );
        });

        // Share
        binding.shareIv.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this series!");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    private void loadSeasonFragment() {
        seasonFragment = SeasonFragment.newInstance(seriesItemsList, false, false);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout, seasonFragment)
                .commit();
    }

    private void setupTabs() {

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int pos = tab.getPosition();

                Fragment fragment = getFragmentForTab(pos);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.framelayout, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.tabLayout.getTabAt(0);
    }

    private Fragment getFragmentForTab(int position) {
        // you only have SeasonFragment now, so return same one
        return SeasonFragment.newInstance(seriesItemsList, false, false);
    }

    private void openDownloadDialog() {

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_download);

        ImageView dialogImage = dialog.findViewById(R.id.downloadIv);
        TextView dialogTitle = dialog.findViewById(R.id.downloadTitleTv);
        SeekBar qualitySeekBar = dialog.findViewById(R.id.qualitySeekbar);
        TextView qualityVal = dialog.findViewById(R.id.qualityValTv);
        TextView downloadTv = dialog.findViewById(R.id.downloadTv);

        dialogImage.setImageDrawable(binding.seriesScreenIv.getDrawable());
        dialogTitle.setText(title);

        qualitySeekBar.setMax(100);
        qualitySeekBar.setProgress(25);

        qualitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 25) qualityVal.setText("Low (360p)");
                else if (progress < 50) qualityVal.setText("Medium (480p)");
                else if (progress < 75) qualityVal.setText("High (720p)");
                else qualityVal.setText("HD (1080p)");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        downloadTv.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putInt("imageResource", imageResource);
            bundle.putString("title", title);
            bundle.putString("rating", rating);
            bundle.putParcelableArrayList("popularSeriesItemsList",
                    (ArrayList<? extends Parcelable>) seriesItemsList);

            NavHostFragment.findNavController(this)
                    .navigate(R.id.seriesPlayerScreenActivity, bundle);

            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}