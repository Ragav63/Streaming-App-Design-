package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.presentation.adapter.CastRecItemAdapter;
import com.example.streamingapp.presentation.adapter.NowOnTvItemAdapter;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.presentation.adapter.PopularMovieRecItemAdapter;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.presentation.adapter.PopularSeriesRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.List;


public class SearchFragment extends Fragment {
    EditText searchEdt;
    TextView cancelTv, searchTitleVideo, searchTitleSeries, searchTitleNowOnTv, searchTitleActors, filterTv, seeAllTv;
    private RecyclerView recVPopularMovies, recVPopularSeries, recVNowOnTv, recVAbout;
    private GridLayoutManager popularMoviesLayoutManager, popularSeriesLayoutManager, castLayoutManager;
    private LinearLayoutManager popularMoviesLinearLayoutManager, popularSeriesLinearLayoutManager, nowOnTvLinearLayoutManager, castLinearLayoutManager;
    private PopularMovieRecItemAdapter popularMovieRecItemAdapter;
    private List<MovieItems> movieItemsList;
    private List<SeriesItems> seriesItemsList;
    private PopularSeriesRecItemAdapter popularSeriesRecItemAdapter;
    private NowOnTvItemAdapter nowOnTvItemAdapter;
    private List<TvItems> nowOnTvItemsList;
    private CastRecItemAdapter castRecItemAdapter;
    private List<CastItems> castItemsList;

    private StreamingViewModel vm;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            popularMovieItemsList = getArguments().getParcelableArrayList("popularMovieItems");
//        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        searchEdt = view.findViewById(R.id.searchEdt);
        cancelTv = view.findViewById(R.id.cancelTv);
        searchTitleVideo = view.findViewById(R.id.searchTitleVideo);
        searchTitleSeries = view.findViewById(R.id.searchTitleSeries);
        searchTitleNowOnTv = view.findViewById(R.id.searchTitleNowOnTv);
        searchTitleActors = view.findViewById(R.id.searchTitleActors);
        filterTv = view.findViewById(R.id.filterTv);
        seeAllTv = view.findViewById(R.id.seeAllTv);

        recVPopularMovies = view.findViewById(R.id.recVPopularMovies);
        recVPopularSeries = view.findViewById(R.id.recVPopularSeries);
        recVNowOnTv = view.findViewById(R.id.recVNowonTv);
        recVAbout = view.findViewById(R.id.recVCast);

        recVNowOnTv.setVisibility(View.GONE);
        recVAbout.setVisibility(View.GONE);

        // Initialize layout managers
        popularMoviesLayoutManager = new GridLayoutManager(getContext(), 2);
        popularSeriesLayoutManager = new GridLayoutManager(getContext(), 2);
        castLayoutManager = new GridLayoutManager(getContext(), 2);

        popularMoviesLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        popularSeriesLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        castLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recVPopularMovies.setLayoutManager(popularMoviesLayoutManager);
        recVPopularSeries.setLayoutManager(popularSeriesLayoutManager);
        recVAbout.setLayoutManager(castLayoutManager);

        recVPopularMovies.setLayoutManager(popularMoviesLayoutManager);
        movieItemsList = vm.getMovies();
        popularMovieRecItemAdapter = new PopularMovieRecItemAdapter(getContext(), movieItemsList);
        recVPopularMovies.setAdapter(popularMovieRecItemAdapter);
        recVPopularMovies.setHasFixedSize(true);

        recVPopularSeries.setLayoutManager(popularSeriesLayoutManager);
        seriesItemsList = vm.getSeries();
        popularSeriesRecItemAdapter = new PopularSeriesRecItemAdapter(getContext(), seriesItemsList);
        recVPopularSeries.setAdapter(popularSeriesRecItemAdapter);
        recVPopularSeries.setHasFixedSize(true);

        nowOnTvLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recVNowOnTv.setLayoutManager(nowOnTvLinearLayoutManager);
        nowOnTvItemsList =vm.getNowOnTvItems();
        nowOnTvItemAdapter = new NowOnTvItemAdapter(getContext(), nowOnTvItemsList);
        recVNowOnTv.setAdapter(nowOnTvItemAdapter);
        recVNowOnTv.setHasFixedSize(true);

        recVAbout.setLayoutManager(castLayoutManager);
        castItemsList = vm.getCast();
        castRecItemAdapter = new CastRecItemAdapter(getContext(), castItemsList, movieItemsList);
        recVAbout.setAdapter(castRecItemAdapter);
        recVAbout.setHasFixedSize(true);

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    ViewGroup.LayoutParams params = searchEdt.getLayoutParams();
                    params.width = dpToPx(300); // Set the width to 300dp
                    searchEdt.setLayoutParams(params);
                    cancelTv.setVisibility(View.VISIBLE);
                    seeAllTv.setVisibility(View.VISIBLE);

//                    searchTitleVideo.setVisibility(View.VISIBLE);
//                    searchTitleSeries.setVisibility(View.VISIBLE);
//                    searchTitleNowOnTv.setVisibility(View.VISIBLE);
//                    searchTitleActors.setVisibility(View.VISIBLE);
//
//                    searchTitleVideo.setText("Video");
//                    searchTitleSeries.setText("Series");
//                    searchTitleNowOnTv.setText("Now On Tv");
//                    searchTitleActors.setText("Actors");

                    recVPopularMovies.setLayoutManager(popularMoviesLinearLayoutManager);
                    recVPopularSeries.setLayoutManager(popularSeriesLinearLayoutManager);
                    recVAbout.setLayoutManager(castLinearLayoutManager);

                    recVNowOnTv.setVisibility(View.VISIBLE);
                    recVAbout.setVisibility(View.VISIBLE);

                    popularMovieRecItemAdapter.getFilter().filter(s);
                    popularSeriesRecItemAdapter.getFilter().filter(s);
                    nowOnTvItemAdapter.getFilter().filter(s);
                    castRecItemAdapter.getFilter().filter(s);

                    updateSearchTitleVisibility();

                } else {
                    ViewGroup.LayoutParams params = searchEdt.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Set the width back to match_parent
                    searchEdt.setLayoutParams(params);
                    cancelTv.setVisibility(View.GONE);

                    searchTitleVideo.setText("What search last");

                    seeAllTv.setVisibility(View.GONE);
                    searchTitleVideo.setVisibility(View.VISIBLE);
                    searchTitleSeries.setVisibility(View.GONE);
                    searchTitleNowOnTv.setVisibility(View.GONE);
                    searchTitleActors.setVisibility(View.GONE);

                    recVNowOnTv.setVisibility(View.GONE);
                    recVAbout.setVisibility(View.GONE);
                    // Reset Layout Managers to GridLayoutManager
                    recVPopularMovies.setLayoutManager(popularMoviesLayoutManager);
                    recVPopularSeries.setLayoutManager(popularSeriesLayoutManager);
                    recVAbout.setLayoutManager(castLayoutManager);

                    popularMovieRecItemAdapter.getFilter().filter("");
                    popularSeriesRecItemAdapter.getFilter().filter("");
                    nowOnTvItemAdapter.getFilter().filter("");
                    castRecItemAdapter.getFilter().filter("");

                    updateSearchTitleVisibility();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cancelTv.setOnClickListener(v -> {
            searchEdt.setText("");
        });

        filterTv.setOnClickListener(v -> {
            FiltersFragment filtersFragment = new FiltersFragment();

            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, filtersFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        seeAllTv.setOnClickListener(v -> {
            Log.d("SearchFragment", "See All clicked");

            // Clear the search input
            searchEdt.setText("");

            searchTitleVideo.setVisibility(View.VISIBLE);
            searchTitleSeries.setVisibility(View.VISIBLE);
            searchTitleNowOnTv.setVisibility(View.VISIBLE);
            searchTitleActors.setVisibility(View.VISIBLE);

            searchTitleVideo.setText("Video");
            searchTitleSeries.setText("Series");
            searchTitleNowOnTv.setText("Now On Tv");
            searchTitleActors.setText("Actors");

            recVPopularMovies.setVisibility(View.VISIBLE);
            recVPopularSeries.setVisibility(View.VISIBLE);
            recVNowOnTv.setVisibility(View.VISIBLE);
            recVAbout.setVisibility(View.VISIBLE);

            recVPopularMovies.setLayoutManager(popularMoviesLayoutManager);
            recVPopularSeries.setLayoutManager(popularSeriesLayoutManager);
            recVNowOnTv.setLayoutManager(nowOnTvLinearLayoutManager);
            recVAbout.setLayoutManager(castLayoutManager);

            // Notify adapters
            popularMovieRecItemAdapter.notifyDataSetChanged();
            popularSeriesRecItemAdapter.notifyDataSetChanged();
            nowOnTvItemAdapter.notifyDataSetChanged();
            castRecItemAdapter.notifyDataSetChanged();

//            // Debug logging
//            Log.d("SearchFragment", "PopularMovies items count: " + popularMovieItemsList.size());
//            Log.d("SearchFragment", "PopularSeries items count: " + popularSeriesItemsList.size());
//            Log.d("SearchFragment", "NowOnTv items count: " + nowOnTvItemsList.size());
//            Log.d("SearchFragment", "Cast items count: " + castItemsList.size());
        });

        return view;
    }

    private void updateSearchTitleVisibility() {
        boolean isPopularMoviesEmpty = popularMovieRecItemAdapter.isDataEmpty();
        boolean isPopularSeriesEmpty = popularSeriesRecItemAdapter.isDataEmpty();
        boolean isNowOnTvEmpty = nowOnTvItemAdapter.isDataEmpty();
        boolean isCastEmpty = castRecItemAdapter.isDataEmpty();

        Log.d("SearchFragment", "Popular Movies Empty: " + isPopularMoviesEmpty);
        Log.d("SearchFragment", "Popular Series Empty: " + isPopularSeriesEmpty);
        Log.d("SearchFragment", "Now On TV Empty: " + isNowOnTvEmpty);
        Log.d("SearchFragment", "Cast Empty: " + isCastEmpty);

        if (isPopularMoviesEmpty) {
            searchTitleVideo.setText("No data found");
            searchTitleVideo.setVisibility(View.VISIBLE);
        } else {
            searchTitleVideo.setText("Video");
            searchTitleVideo.setVisibility(View.VISIBLE);
        }

        if (isPopularSeriesEmpty) {
            searchTitleSeries.setVisibility(View.GONE);
        } else {
            searchTitleSeries.setVisibility(View.VISIBLE);
            searchTitleSeries.setText("Series");
        }

        if (isNowOnTvEmpty) {
            searchTitleNowOnTv.setVisibility(View.GONE);
        } else {
            searchTitleNowOnTv.setVisibility(View.VISIBLE);
            searchTitleNowOnTv.setText("Now On TV");
        }

        if (isCastEmpty) {
            searchTitleActors.setVisibility(View.GONE);
        } else {
            searchTitleActors.setVisibility(View.VISIBLE);
            searchTitleActors.setText("Actors");
        }
    }


    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

}