package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.adapter.SeasonEpRecItemAdapter;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SeasonFragment extends Fragment {
    private RecyclerView recVSeason;
    private SeasonEpRecItemAdapter seasonEpRecItemAdapter;
    private List<SeasonItems> seasonItemsList;
    TabLayout tabLayout;
    FrameLayout frameLayout;
    Fragment fragment=null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TrailersFragment trailersFragment = new TrailersFragment();
    private List<SeriesItems> seriesItemsList;
    private boolean fromSeriesPlayerScreenActivity;
    private boolean fromSeriesLandscapePlayerScreenActivity;

    private StreamingViewModel vm;

    public SeasonFragment() {
        // Required empty public constructor
    }

    public static SeasonFragment newInstance(List<SeriesItems> seriesItemsList, boolean fromSeriesPlayerScreenActivity, boolean fromSeriesLandscapePlayerScreenActivity) {
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

            if (seriesItemsList == null) {
                Log.d("SeasonFragment", "popularSeriesItemsList is null");
            } else if (seriesItemsList.isEmpty()) {
                Log.d("SeasonFragment", "popularSeriesItemsList is empty");
            } else {
                for (SeriesItems item : seriesItemsList) {
                    Log.d("SeasonFragment", "Item from list: " + item.toString());
                }
            }
        } else {
            Log.d("SeasonFragment", "getArguments() is null");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_season, container, false);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        recVSeason = view.findViewById(R.id.recVSeason);
        tabLayout = view.findViewById(R.id.tab_layout);
        frameLayout = view.findViewById(R.id.seasonFrameLayout);

        // Conditionally set visibility and layout manager based on the source activity
        if (fromSeriesLandscapePlayerScreenActivity) {
            tabLayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
            recVSeason.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            // Set margin for recVSeason
            setRecyclerViewMargin(recVSeason, 10);

        } else {
            recVSeason.setLayoutManager(new LinearLayoutManager(getContext()));
            fragmentManager = getChildFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.seasonFrameLayout, trailersFragment);
            fragmentTransaction.commit();

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position = tab.getPosition();
                    Objects.requireNonNull(tabLayout.getTabAt(tab.getPosition()));

                    String tag = "android:switcher:" + R.id.seasonFrameLayout + ":" + position;
                    fragment = getChildFragmentManager().findFragmentByTag(tag);
                    if (fragment == null) {
                        switch (position) {
                            case 0:
                                fragment = trailersFragment;
                                break;
                            case 1:
                                if (seriesItemsList != null && !seriesItemsList.isEmpty()) {
                                    fragment = MoreLikeThisFragment.newInstanceWithSeries(seriesItemsList);
                                } else {
                                    Log.d("SeasonFragment", "popularSeriesItemsList is empty or null in Morelikethis");
                                    return;
                                }
                                break;
                            case 2:
                                fragment = AboutFragment.newInstanceWithSeries(seriesItemsList);
                                break;
                        }

                        if (fragment != null) {
                            getChildFragmentManager().beginTransaction().replace(R.id.seasonFrameLayout, fragment, tag).commit();
                        }
                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    tabLayout.getTabAt(tab.getPosition()).setCustomView(null);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            tabLayout.getTabAt(0);

            // Set margin for recVSeason
            setRecyclerViewMargin(recVSeason, 5);
        }

        seasonItemsList = vm.getSeasonItems();
        seasonEpRecItemAdapter = new SeasonEpRecItemAdapter(getContext(), seasonItemsList, fromSeriesPlayerScreenActivity ,fromSeriesLandscapePlayerScreenActivity);
        recVSeason.setAdapter(seasonEpRecItemAdapter);
        recVSeason.setHasFixedSize(true);

        return view;
    }

    private void setRecyclerViewMargin(View view, int dpMargin) {
        int marginInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpMargin, getResources().getDisplayMetrics());
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.bottomMargin = marginInPixels;
        view.setLayoutParams(layoutParams);
    }
}