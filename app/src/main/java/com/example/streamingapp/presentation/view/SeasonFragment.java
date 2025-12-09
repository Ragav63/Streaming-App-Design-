package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
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
import com.example.streamingapp.data.model.Episode;
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
    private int seasonNumber = 1;
    private int currentEpisodeNumber = -1;


    private List<SeriesItems> seriesItemsList;
    private SeriesItems seriesItems;
    private List<SeasonItems> seasonItemsList;

    private boolean fromSeriesPlayerScreenActivity;
    private boolean fromSeriesLandscapePlayerScreenActivity;

    private StreamingViewModel vm;


    public SeasonFragment() {}

    public static SeasonFragment newInstance(int seasonNumber,
                                             int episodeNumber,
                                             SeriesItems seriesItems,
                                             List<SeriesItems> seriesItemsList,
                                             boolean fromSeriesPlayerScreenActivity,
                                             boolean fromSeriesLandscapePlayerScreenActivity) {

        SeasonFragment fragment = new SeasonFragment();
        Bundle args = new Bundle();

        args.putInt("seasonNumber", seasonNumber);
        args.putInt("episodeNumber", episodeNumber);
        args.putParcelable("seriesItem", seriesItems);
        args.putParcelableArrayList("seriesItemsList", (ArrayList<? extends Parcelable>) seriesItemsList);
        args.putBoolean("fromSeriesPlayerScreenActivity", fromSeriesPlayerScreenActivity);
        args.putBoolean("fromSeriesLandscapePlayerScreenActivity", fromSeriesLandscapePlayerScreenActivity);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            seasonNumber = getArguments().getInt("seasonNumber", 1);
            currentEpisodeNumber = getArguments().getInt("episodeNumber", -1);
            seriesItems = getArguments().getParcelable("seriesItem");
            seriesItemsList = getArguments().getParcelableArrayList("seriesItemsList");
            fromSeriesPlayerScreenActivity = getArguments().getBoolean("fromSeriesPlayerScreenActivity", false);
            fromSeriesLandscapePlayerScreenActivity = getArguments().getBoolean("fromSeriesLandscapePlayerScreenActivity", false);

            if (seriesItems == null) {
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

        EpisodeViewMode mode= null;


        Log.d("Episodenumber","The values "+currentEpisodeNumber);

        seasonEpRecItemAdapter = new SeasonEpRecItemAdapter(
                requireContext(),
                new ArrayList<>(),
                mode,
                currentEpisodeNumber,
                (item, pos) -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("episode",item);
                    bundle.putParcelable("seriesItem", seriesItems);
                    bundle.putParcelableArrayList("popularSeriesItemsList",
                            (ArrayList<? extends Parcelable>) seriesItemsList);

                    NavHostFragment.findNavController(this)
                            .navigate(R.id.seriesPlayerScreenActivity, bundle);
                }
        );

        binding.recVSeason.setAdapter(seasonEpRecItemAdapter);

        List<Episode> selectedSeasonEpisodes = new ArrayList<>();

        try {
            selectedSeasonEpisodes = seriesItems
                    .getSeasons()
                    .get(seasonNumber - 1)
                    .getEpisodes();
        } catch (Exception e) {
            Log.e("SeasonFragment", "Invalid season number: " + seasonNumber);
        }

        seasonEpRecItemAdapter.updateList(selectedSeasonEpisodes, currentEpisodeNumber);

        return binding.getRoot();
    }



    private void openDownloadDialog(Episode seasonItems) {

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

        Glide.with(requireContext()).load(seasonItems.getImages().get(0)).into(downloadIv);
        downloadTitle.setText(seasonItems.getEpisodeTitle());

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
            binding.view1.setVisibility(View.GONE);

            binding.recVSeason.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            );


        } else {
            // Normal Mode
            binding.recVSeason.setLayoutManager(new LinearLayoutManager(requireContext()));

            replaceInnerFragment(TrailersFragment.newInstanceWithSeries(seriesItems));


            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            replaceInnerFragment(TrailersFragment.newInstanceWithSeries(seriesItems));
                            break;

                        case 1:
                            if (seriesItemsList != null && !seriesItemsList.isEmpty()) {
                                replaceInnerFragment(MoreLikeThisFragment.newInstanceWithSeries(seriesItemsList));
                            }
                            break;

                        case 2:
                            replaceInnerFragment(AboutFragment.newInstanceWithSeries(seriesItems));
                            break;
                    }
                }

                @Override public void onTabUnselected(TabLayout.Tab tab) {}
                @Override public void onTabReselected(TabLayout.Tab tab) {}
            });

        }
    }

    private void replaceInnerFragment(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.seasonFrameLayout.getId(), fragment)
                .commit();
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