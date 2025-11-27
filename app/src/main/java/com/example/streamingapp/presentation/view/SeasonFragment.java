package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSeasonBinding;
import com.example.streamingapp.domain.repository.EpisodeViewMode;
import com.example.streamingapp.presentation.adapter.SeasonEpRecItemAdapter;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class SeasonFragment extends Fragment {
    private FragmentSeasonBinding binding;

    private SeasonEpRecItemAdapter seasonEpRecItemAdapter;
    private List<SeasonItems> seasonItemsList;

    private List<SeriesItems> seriesItemsList;
    private boolean fromSeriesPlayerScreenActivity;
    private boolean fromSeriesLandscapePlayerScreenActivity;

    private StreamingViewModel vm;

    private final TrailersFragment trailersFragment = new TrailersFragment();

    public SeasonFragment() {}

    public static SeasonFragment newInstance(List<SeriesItems> seriesItemsList,
                                             boolean fromSeriesPlayerScreenActivity,
                                             boolean fromSeriesLandscapePlayerScreenActivity) {

        SeasonFragment fragment = new SeasonFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList("popularSeriesItemsList", new ArrayList<>(seriesItemsList));
        args.putBoolean("fromSeriesPlayerScreenActivity", fromSeriesPlayerScreenActivity);
        args.putBoolean("fromSeriesLandscapePlayerScreenActivity", fromSeriesLandscapePlayerScreenActivity);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            seriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");
            fromSeriesPlayerScreenActivity = getArguments().getBoolean("fromSeriesPlayerScreenActivity", false);
            fromSeriesLandscapePlayerScreenActivity = getArguments().getBoolean("fromSeriesLandscapePlayerScreenActivity", false);

            if (seriesItemsList == null || seriesItemsList.isEmpty()) {
                Log.d("SeasonFragment", "Series list empty or null");
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSeasonBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(requireActivity(),
                new StreamingViewModelFactory()).get(StreamingViewModel.class);

        setupRecyclerAndTabs();

        EpisodeViewMode mode;

        if (fromSeriesPlayerScreenActivity) {
            mode = EpisodeViewMode.PLAYER_PORTRAIT;
        } else if (fromSeriesLandscapePlayerScreenActivity) {
            mode = EpisodeViewMode.PLAYER_LANDSCAPE;
        } else {
            mode = EpisodeViewMode.NORMAL;
        }

        seasonEpRecItemAdapter = new SeasonEpRecItemAdapter(
                new ArrayList<>(),
                mode,
                (item, pos) -> {
                    if (mode != EpisodeViewMode.NORMAL) {
                        // handle episode click here directly
                        openPlayerForEpisode(item);
                    }
                },
                (item, pos) -> {
                    if (mode == EpisodeViewMode.NORMAL)
                        openDownloadDialog(item);
                }
        );

        binding.recVSeason.setAdapter(seasonEpRecItemAdapter);

// update current list async
        seasonEpRecItemAdapter.getDiffer().submitList(vm.getSeasonItems());

        return binding.getRoot();
    }

    private void openPlayerForEpisode(SeasonItems item) {
        // TODO open your player activity or fragment
    }


    private void openDownloadDialog(SeasonItems seasonItems) {

        final Dialog dialog = new Dialog(requireContext());
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

        Glide.with(requireContext()).load(seasonItems.getSeasonEpImg()).into(downloadIv);
        downloadTitle.setText(seasonItems.getSeasonEpTitle());

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
            Toast.makeText(dialog.getContext(), "Started to Download", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void setupRecyclerAndTabs() {

        if (fromSeriesLandscapePlayerScreenActivity) {
            // Landscape Mode → No Tabs
            binding.tabLayout.setVisibility(View.GONE);
            binding.seasonFrameLayout.setVisibility(View.GONE);

            binding.recVSeason.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            );

            setRecyclerMargin(binding.recVSeason, 10);

        } else {
            // Normal Mode
            binding.recVSeason.setLayoutManager(new LinearLayoutManager(requireContext()));

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(binding.seasonFrameLayout.getId(), trailersFragment)
                    .commit();

            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Fragment selectedFragment = null;

                    switch (tab.getPosition()) {
                        case 0:
                            selectedFragment = trailersFragment;
                            break;

                        case 1:
                            if (seriesItemsList != null && !seriesItemsList.isEmpty()) {
                                selectedFragment = MoreLikeThisFragment.newInstanceWithSeries(seriesItemsList);
                            }
                            break;

                        case 2:
                            selectedFragment = AboutFragment.newInstanceWithSeries(seriesItemsList);
                            break;
                    }

                    if (selectedFragment != null) {
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(binding.seasonFrameLayout.getId(), selectedFragment)
                                .commit();
                    }
                }

                @Override public void onTabUnselected(TabLayout.Tab tab) {}
                @Override public void onTabReselected(TabLayout.Tab tab) {}
            });

            setRecyclerMargin(binding.recVSeason, 5);
        }
    }

    private void setRecyclerMargin(View view, int dp) {
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.bottomMargin = px;
        view.setLayoutParams(params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}