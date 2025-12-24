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
import com.example.streamingapp.data.model.CrewMember;
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
                                             boolean fromSeriesPlayerScreenActivity,
                                             boolean fromSeriesLandscapePlayerScreenActivity) {

        SeasonFragment fragment = new SeasonFragment();
        Bundle args = new Bundle();

        args.putInt("seasonNumber", seasonNumber);
        args.putInt("episodeNumber", episodeNumber);
        args.putParcelable("seriesItem", seriesItems);
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

        vm.getSeriesLiveData();
        vm.getSeriesLiveData().observe(getViewLifecycleOwner(), fullList -> {

            if (fullList == null || fullList.isEmpty()) {
                seriesItemsList = new ArrayList<>();
            } else {
                List<CrewMember> currentCrew = seriesItems.getCrew();

                if (currentCrew == null || currentCrew.isEmpty()) {
                    seriesItemsList = new ArrayList<>(fullList);
                } else {
                    List<String> crewNames = new ArrayList<>();
                    for (CrewMember c : currentCrew) {
                        if (c.getName() != null) crewNames.add(c.getName().trim());
                    }

                    seriesItemsList = new ArrayList<>();
                    for (SeriesItems s : fullList) {
                        if (s.getCrew() == null) continue;

                        for (CrewMember cm : s.getCrew()) {
                            if (crewNames.contains(cm.getName().trim())
                                    && !s.getTitle().equals(seriesItems.getTitle())) {
                                seriesItemsList.add(s);
                                break;
                            }
                        }
                    }
                }
            }
        });


        // Initialize adapter FIRST
        Log.d("Episodenumber", "The values " + currentEpisodeNumber);

        seasonEpRecItemAdapter = new SeasonEpRecItemAdapter(
                requireContext(),
                new ArrayList<>(),
                currentEpisodeNumber,
                (item, pos) -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("episode", item);
                    bundle.putParcelable("seriesItem", seriesItems);
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.seriesPlayerScreenActivity, bundle);
                }
        );

        binding.recVSeason.setAdapter(seasonEpRecItemAdapter);

        setupRecyclerAndTabs();


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
            // Normal Mode (Portrait)
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
                            replaceInnerFragment(MoreLikeThisFragment.newInstanceWithSeries(seriesItemsList));
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}