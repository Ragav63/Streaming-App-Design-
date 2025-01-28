package com.example.streamingapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    EditText searchEdt;
    TextView cancelTv, searchTitleVideo, searchTitleSeries, searchTitleNowOnTv, searchTitleActors, filterTv, seeAllTv;
    private RecyclerView recVPopularMovies, recVPopularSeries, recVNowOnTv, recVAbout;
    private GridLayoutManager popularMoviesLayoutManager, popularSeriesLayoutManager, castLayoutManager;
    private LinearLayoutManager popularMoviesLinearLayoutManager, popularSeriesLinearLayoutManager, nowOnTvLinearLayoutManager, castLinearLayoutManager;
    private PopularMovieRecItemAdapter popularMovieRecItemAdapter;
    private List<PopularMovieItems> popularMovieItemsList;
    private List<PopularSeriesItems> popularSeriesItemsList;
    private PopularSeriesRecItemAdapter popularSeriesRecItemAdapter;
    private NowOnTvItemAdapter nowOnTvItemAdapter;
    private List<NowOnTvItems> nowOnTvItemsList;
    private CastRecItemAdapter castRecItemAdapter;
    private List<CastItems> castItemsList;

    private List<PopularMovieItems> generatePopularMovieItemList() {
        List<PopularMovieItems> itemList = new ArrayList<>();
        itemList.add(new PopularMovieItems("7.2", "Venom 3", "2018", "Fantasy", "USA", "2h 45m", "A failed reporter is bonded to an alien entity, one of many symbiotes who have invaded Earth. But the being takes a liking to Earth and decides to protect it.", R.drawable.venom3verticalnew));
        itemList.add(new PopularMovieItems("6.8", "Kalki", "2024", "History", "India", "2h 40m", "A modern-day avatar of Vishnu, a Hindu god, who is believed to have descended to earth to protect the world from evil forces.",R.drawable.kalkiverticalnew));
        itemList.add(new PopularMovieItems("8.0", "Avengers Endgame", "2019", "Action", "USA", "3h 05m", "After the devastating events of Avengers: Infinity War (2018), the universe is in ruins. With the help of remaining allies, the Avengers assemble once more in order to reverse Thanos' actions and restore balance to the universe.",R.drawable.avengersverticalnew));
        itemList.add(new PopularMovieItems("7.0", "Avatar The Last Airbender", "2023", "History", "USA", "2h 35m", "A young boy known as the Avatar must master the four elemental powers to save the world, and fight against an enemy bent on stopping him.",R.drawable.avatarthelastairbenderverticalnew));
        itemList.add(new PopularMovieItems("7.3", "Captain America", "2017", "Action", "USA", "2h 27m", "Political involvement in the Avengers' affairs causes a rift between Captain America and Iron Man.",R.drawable.captainamericaverticalnew));
        itemList.add(new PopularMovieItems("6.5", "Avatar The Way of Water", "2023", "Fantasy", "USA", "3h 45m", "Jake Sully lives with his newfound family on the extrasolar moon Pandora. Once a familiar threat returns to finish what was previously started. Jake must work with Neytiri and the army of the Navi race to protect their home.",R.drawable.avatarthewayofwaterverticalnew1));

        return itemList;
    }

    private List<PopularSeriesItems> generatePopularSeriesItemList() {
        List<PopularSeriesItems> itemList = new ArrayList<>();
        itemList.add(new PopularSeriesItems("7.2", "Game of thrones", "2011", "Action", "USA", "8", "Nine noble families fight for control over the lands of Westeros, while an ancient enemy returns after being dormant for millennia.", R.drawable.got));
        itemList.add(new PopularSeriesItems("6.8", "Dark", "2017", "Crime", "USA", "3", "A family saga with a supernatural twist, set in a German town where the disappearance of two young children exposes the relationships among four families.", R.drawable.dark));
        itemList.add(new PopularSeriesItems("8.0", "The Boys", "2019", "Dark Comedy", "USA", "4", "A group of vigilantes set out to take down corrupt superheroes who abuse their superpowers.", R.drawable.theboys));
        itemList.add(new PopularSeriesItems("7.0", "The 100", "2014", "Scifi - Drama", "USA", "7", "Set 97 years after a nuclear war destroyed civilization, when a spaceship housing humanity's lone survivors sends 100 juvenile delinquents back to Earth, hoping to repopulate the planet.", R.drawable.the100));
        itemList.add(new PopularSeriesItems("7.3", "Breaking Bad", "2008", "Crime", "USA", "5", "A chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine with a former student in order to secure his family's future.", R.drawable.brbanew));
        itemList.add(new PopularSeriesItems("6.5", "Prison Break", "2005", "Prison Drama", "USA", "5", "A structural engineer installs himself in a prison he helped design, in order to save his falsely accused brother from a death sentence by breaking themselves out from the inside.", R.drawable.prisonbreakverticalnew));

        return itemList;
    }

    private List<NowOnTvItems> generateNowOnTvList() {
        List<NowOnTvItems> itemList = new ArrayList<>();
        itemList.add(new NowOnTvItems("ESPN", "NBA Playoff Game-2","11.35-12.50",R.drawable.spart));
        itemList.add(new NowOnTvItems("FOX", "Stranger Things","12.35-01.50",R.drawable.strthings));
        itemList.add(new NowOnTvItems("SPORTS 18", "IND VS BAN","11.35-12.50",R.drawable.scifi1));

        return itemList;
    }

    private List<CastItems> generateCastList() {
        List<CastItems> itemList = new ArrayList<>();
        itemList.add(new CastItems("Sam Worthington", "Actor",R.drawable.samworthington));
        itemList.add(new CastItems("Zoe Saldana", "Actor",R.drawable.zoesaldana));
        itemList.add(new CastItems("Michele Rodriguez", "Actor",R.drawable.michelerodriguez));
        itemList.add(new CastItems("Sigourney Weaver", "Actor",R.drawable.sigourneyweaver));
        itemList.add(new CastItems("Stephen Lang", "Actor",R.drawable.stephenlang));

        return itemList;
    }


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
        popularMovieItemsList = generatePopularMovieItemList();
        popularMovieRecItemAdapter = new PopularMovieRecItemAdapter(getContext(), popularMovieItemsList);
        recVPopularMovies.setAdapter(popularMovieRecItemAdapter);
        recVPopularMovies.setHasFixedSize(true);

        recVPopularSeries.setLayoutManager(popularSeriesLayoutManager);
        popularSeriesItemsList = generatePopularSeriesItemList();
        popularSeriesRecItemAdapter = new PopularSeriesRecItemAdapter(getContext(), popularSeriesItemsList);
        recVPopularSeries.setAdapter(popularSeriesRecItemAdapter);
        recVPopularSeries.setHasFixedSize(true);

        nowOnTvLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recVNowOnTv.setLayoutManager(nowOnTvLinearLayoutManager);
        nowOnTvItemsList = generateNowOnTvList();
        nowOnTvItemAdapter = new NowOnTvItemAdapter(getContext(), nowOnTvItemsList);
        recVNowOnTv.setAdapter(nowOnTvItemAdapter);
        recVNowOnTv.setHasFixedSize(true);

        recVAbout.setLayoutManager(castLayoutManager);
        castItemsList = generateCastList();
        castRecItemAdapter = new CastRecItemAdapter(getContext(), castItemsList, popularMovieItemsList);
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