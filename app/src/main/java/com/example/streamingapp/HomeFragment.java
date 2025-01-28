package com.example.streamingapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeFragment extends Fragment {
    ViewPager2 viewPager2;
    LinearLayout dotIndicator;
    private int dotCount;
    private HomeStartPagerAdapter homeStartPagerAdapter;
    private List<HomeStartItems> homeStartItemsList;
    private HomeStartCardRecItemAdapter homeStartCardRecItemAdapter;
    private List<HomeStartCardListItems> homeStartCardListItems;
    private Handler sliderHandler;
    private Runnable sliderRunnable;
    private int currentPage = 0;
//    CardView mainCv, firstCv, secondCv, thirdCv, fourthCv, fifthCv;
//    ImageView mainIv, firstIv, secondIv, thirdIv, fourthIv, fifthIv;
    private RecyclerView recVHomeStartCardItems, recVPopularMovies, recVContinueWatching, recVCategoryHome, recVNowOnTv, recVPopularMovies1, recVPopularSeries;
    RecyclerView.LayoutManager homeStartCardItemsLayoutManager, popularMoviesLayoutManager, continueWatchingLayoutManager, nowOnTvLayoutManager, popularMovies1LayoutManager, popularSeriesLayoutManager;
    private GridLayoutManager categoryHomeLayoutManager;
    TextView seeAllPopularMoviesTv, seeAllContinueWatchingTv, seeAllNowOnTv, seeAllPopularMoviesTv1, seeAllPopularSeriesTv;
    private PopularMovieRecItemAdapter popularMovieRecItemAdapter;
    private List<PopularMovieItems> popularMovieItemsList;
    private ContinueWatchingItemAdapter continueWatchingItemAdapter;
    private List<ContinueWatchingItems> continueWatchingItemsList;
    private CategoryHomeRecItemAdapter categoryHomeRecItemAdapter;
    private List<CategoryHomeItems> categoryHomeItemsList;
    private NowOnTvItemAdapter nowOnTvItemAdapter;
    private List<NowOnTvItems> nowOnTvItemsList;
    private List<PopularSeriesItems> popularSeriesItemsList;
    private PopularSeriesRecItemAdapter popularSeriesRecItemAdapter;
//    FragmentManager fragmentManager;
//    FragmentTransaction fragmentTransaction;
//    Bundle bundle = new Bundle();

    private List<HomeStartItems> generateHomeStartItems() {
        List<HomeStartItems> itemList = new ArrayList<>();
        itemList.add(new HomeStartItems("7.3","Venom 3", R.drawable.venom3verticalnew));
        itemList.add(new HomeStartItems("7.0","Avatar The Last Airbender", R.drawable.avatarthelastairbenderverticalnew));
        itemList.add(new HomeStartItems("8.0","Avengers : Endgame", R.drawable.avengersverticalnew));
        itemList.add(new HomeStartItems("6.5","Avatar The Way of Water", R.drawable.avatarthewayofwaterverticalnew2));
        itemList.add(new HomeStartItems("7.2","Kalki - 1", R.drawable.kalkiverticalnew));
        itemList.add(new HomeStartItems("7.3","Captain Americe", R.drawable.captainamericaverticalnew));

        return itemList;
    }

    private List<HomeStartCardListItems> generateHomeStartCardListItems() {
        List<HomeStartCardListItems> itemsList = new ArrayList<>();
        itemsList.add(new HomeStartCardListItems(R.drawable.venom3));
        itemsList.add(new HomeStartCardListItems(R.drawable.avatarthelastairbender));
        itemsList.add(new HomeStartCardListItems(R.drawable.avengers));
        itemsList.add(new HomeStartCardListItems(R.drawable.avatarthewayofwater));
        itemsList.add(new HomeStartCardListItems(R.drawable.kalki));
        itemsList.add(new HomeStartCardListItems(R.drawable.captainamerica));
        return itemsList;
    }

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

    private List<ContinueWatchingItems> generateContinueWatchingItemList() {
        List<ContinueWatchingItems> itemList = new ArrayList<>();
        itemList.add(new ContinueWatchingItems("Venom 3", "",R.drawable.venom3));
        itemList.add(new ContinueWatchingItems("Stranger Things - Season 1","Episode 1 Winter is Coming",R.drawable.strangerthings1));

        return itemList;
    }

    private List<CategoryHomeItems> generateCategoryHomeItemList() {
        List<CategoryHomeItems> itemList = new ArrayList<>();
        itemList.add(new CategoryHomeItems("TV CHANNELS", R.drawable.strthings));
        itemList.add(new CategoryHomeItems("MOVIES", R.drawable.spartans));
        itemList.add(new CategoryHomeItems("CARTOONS", R.drawable.anime));
        itemList.add(new CategoryHomeItems("SCI-FI", R.drawable.scifi));
        itemList.add(new CategoryHomeItems("SPORT", R.drawable.sports));
        itemList.add(new CategoryHomeItems("SERIES", R.drawable.strthings));
        itemList.add(new CategoryHomeItems("TV SHOWS", R.drawable.tvshows));

        return itemList;
    }

    private List<NowOnTvItems> generateNowOnTvList() {
        List<NowOnTvItems> itemList = new ArrayList<>();
        itemList.add(new NowOnTvItems("ESPN", "NBA Playoff Game-2","11.35-12.50",R.drawable.spart));
        itemList.add(new NowOnTvItems("FOX", "Stranger Things","12.35-01.50",R.drawable.strthings));
        itemList.add(new NowOnTvItems("SPORTS 18", "IND VS BAN","11.35-12.50",R.drawable.scifi1));

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager2=view.findViewById(R.id.homeStartViewPager);
        dotIndicator=view.findViewById(R.id.dotIndicator);

//        mainCv = view.findViewById(R.id.mainCv);
//        firstCv = view.findViewById(R.id.firstCv);
//        secondCv = view.findViewById(R.id.secondCv);
//        thirdCv = view.findViewById(R.id.thirdCv);
//        fourthCv = view.findViewById(R.id.fourthCv);
//        fifthCv = view.findViewById(R.id.fifthCv);
//
//        mainIv = view.findViewById(R.id.mainImg);
//        firstIv = view.findViewById(R.id.firstImg);
//        secondIv = view.findViewById(R.id.secondImg);
//        thirdIv = view.findViewById(R.id.thirdImg);
//        fourthIv = view.findViewById(R.id.fourthImg);
//        fifthIv = view.findViewById(R.id.fifthImg);
        recVHomeStartCardItems = view.findViewById(R.id.homeStartCardItems);

        seeAllPopularMoviesTv = view.findViewById(R.id.seeAllPopularMoviesTv);
        recVPopularMovies = view.findViewById(R.id.recVPopularMovies);

        seeAllContinueWatchingTv = view.findViewById(R.id.seeAllContinueWatchingTv);
        recVContinueWatching = view.findViewById(R.id.recVContinueWatching);

        recVCategoryHome = view.findViewById(R.id.recVCategories);

        seeAllNowOnTv = view.findViewById(R.id.seeAllNowOnTv);
        recVNowOnTv = view.findViewById(R.id.recVNowonTv);

        seeAllPopularMoviesTv1 = view.findViewById(R.id.seeAllPopularMovies1Tv);
        recVPopularMovies1 = view.findViewById(R.id.recVPopularMovies1);

        seeAllPopularSeriesTv = view.findViewById(R.id.seeAllPopularSeriesTv);
        recVPopularSeries = view.findViewById(R.id.recVPopularSeries);

        homeStartItemsList = generateHomeStartItems();
        homeStartPagerAdapter = new HomeStartPagerAdapter(homeStartItemsList);
        viewPager2.setAdapter(homeStartPagerAdapter);

        homeStartCardItemsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVHomeStartCardItems.setLayoutManager(homeStartCardItemsLayoutManager);
        homeStartCardListItems = generateHomeStartCardListItems();
        homeStartCardRecItemAdapter = new HomeStartCardRecItemAdapter(homeStartCardListItems, position -> {
            viewPager2.setCurrentItem(position, true);
        });
        recVHomeStartCardItems.setAdapter(homeStartCardRecItemAdapter);
        recVHomeStartCardItems.setHasFixedSize(true);

        // Add LinearSnapHelper
//        LinearSnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recVHomeStartCardItems);

        // Add PagerSnapHelper
        PagerSnapHelper snapHelper1 = new PagerSnapHelper();
        snapHelper1.attachToRecyclerView(recVHomeStartCardItems);

        homeStartCardRecItemAdapter.notifyDataSetChanged();

//        mainIv.setImageResource(R.drawable.venom3);
//        firstIv.setImageResource(R.drawable.avatarthelastairbender);
//        secondIv.setImageResource(R.drawable.avengers);
//        thirdIv.setImageResource(R.drawable.avatarthewayofwater);
//        fourthIv.setImageResource(R.drawable.kalki);
//        fifthIv.setImageResource(R.drawable.captainamerica);

//        sliderHandler = new Handler(Looper.getMainLooper());
//        sliderRunnable = new Runnable() {
//            @Override
//            public void run() {
//                currentPage++;
//                if (currentPage >= homeStartPagerAdapter.getItemCount()) {
//                    currentPage = 0;
//                }
//                viewPager2.setCurrentItem(currentPage);
//                sliderHandler.postDelayed(this, 3000);
//            }
//        };

        // RecyclerView Page Change Listener
        recVHomeStartCardItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Implement logic to update the dot indicator based on current position
                // Example: updateDotIndicator(getCurrentPosition());
            }
        });

        // Set initial state for CardViews
//        updateCardViewBackground(0);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDotIndicator(position);
                // Update the selected position in the adapter
                homeStartCardRecItemAdapter.updateSelectedPosition(position);
                scrollToPosition(position); // Sync RecyclerView with ViewPager2
//                updateCardViewBackground(position);
                currentPage = position;
            }
        });

        sliderHandler = new Handler(Looper.getMainLooper());
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int nextPage = (currentPage + 1) % dotCount;
                viewPager2.setCurrentItem(nextPage, true);
                scrollToPosition(nextPage); // Scroll RecyclerView to match ViewPager2
                sliderHandler.postDelayed(this, 5000);
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 5000);

        dotCount = homeStartItemsList.size();
        setupDotIndicator();

        // Set click listeners on CardViews
//        setCardViewClickListeners();


        seeAllPopularMoviesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopularMoviesFragment popularMoviesFragment = new PopularMoviesFragment();

                // Prepare data to pass to the PopularMoviesFragment
                List<PopularMovieItems> popularMovieItemsList = generatePopularMovieItemList();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("popularMovieItems", (ArrayList<? extends Parcelable>) new ArrayList<>(popularMovieItemsList));
                popularMoviesFragment.setArguments(bundle);

                // Transition to ContinueWatchingFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, popularMoviesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        popularMoviesLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVPopularMovies.setLayoutManager(popularMoviesLayoutManager);
        popularMovieItemsList = generatePopularMovieItemList();
        popularMovieRecItemAdapter = new PopularMovieRecItemAdapter(getContext(), popularMovieItemsList);
        recVPopularMovies.setAdapter(popularMovieRecItemAdapter);
        recVPopularMovies.setHasFixedSize(true);

        seeAllContinueWatchingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContinueWatchingFragment continueWatchingFragment = new ContinueWatchingFragment();

                // Prepare data to pass to the ContinueWatchingFragment
                List<ContinueWatchingItems> continueWatchingItemsList = generateContinueWatchingItemList();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("continueWatchingItems", (ArrayList<? extends Parcelable>) new ArrayList<>(continueWatchingItemsList));
                continueWatchingFragment.setArguments(bundle);

                // Transition to ContinueWatchingFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, continueWatchingFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        continueWatchingLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVContinueWatching.setLayoutManager(continueWatchingLayoutManager);
        continueWatchingItemsList = generateContinueWatchingItemList();
        continueWatchingItemAdapter = new ContinueWatchingItemAdapter(getContext(), continueWatchingItemsList);
        recVContinueWatching.setAdapter(continueWatchingItemAdapter);
        recVContinueWatching.setHasFixedSize(true);

        categoryHomeLayoutManager=new GridLayoutManager(getContext(), 2);
        recVCategoryHome.setLayoutManager(categoryHomeLayoutManager);
        categoryHomeItemsList = generateCategoryHomeItemList();
        categoryHomeRecItemAdapter = new CategoryHomeRecItemAdapter(getContext(), categoryHomeItemsList);
        recVCategoryHome.setAdapter(categoryHomeRecItemAdapter);
        recVCategoryHome.setHasFixedSize(true);

        seeAllNowOnTv.setOnClickListener(v -> {
            TvFragment tvFragment = new TvFragment();

            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, tvFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        nowOnTvLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVNowOnTv.setLayoutManager(nowOnTvLayoutManager);
        nowOnTvItemsList = generateNowOnTvList();
        nowOnTvItemAdapter = new NowOnTvItemAdapter(getContext(), nowOnTvItemsList);
        recVNowOnTv.setAdapter(nowOnTvItemAdapter);
        recVNowOnTv.setHasFixedSize(true);

        seeAllPopularMoviesTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopularMoviesFragment popularMoviesFragment = new PopularMoviesFragment();

                // Prepare data to pass to the PopularMoviesFragment
                List<PopularMovieItems> popularMovieItemsList = generatePopularMovieItemList();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("popularMovieItems", (ArrayList<? extends Parcelable>) new ArrayList<>(popularMovieItemsList));
                popularMoviesFragment.setArguments(bundle);

                // Transition to ContinueWatchingFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, popularMoviesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        popularMovies1LayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVPopularMovies1.setLayoutManager(popularMovies1LayoutManager);
        recVPopularMovies1.setAdapter(popularMovieRecItemAdapter);
        recVPopularMovies1.setHasFixedSize(true);

        seeAllPopularSeriesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopularSeriesFragment popularSeriesFragment = new PopularSeriesFragment();

                List<PopularSeriesItems> popularSeriesItemsList = generatePopularSeriesItemList();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("popularSeriesItems", (ArrayList<? extends Parcelable>) new ArrayList<>(popularSeriesItemsList));
                popularSeriesFragment.setArguments(bundle);


                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, popularSeriesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        popularSeriesLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVPopularSeries.setLayoutManager(popularSeriesLayoutManager);
        popularSeriesItemsList = generatePopularSeriesItemList();
        popularSeriesRecItemAdapter = new PopularSeriesRecItemAdapter(getContext(), popularSeriesItemsList);
        recVPopularSeries.setAdapter(popularSeriesRecItemAdapter);
        recVPopularSeries.setHasFixedSize(true);

        return view;
    }

    private void setupDotIndicator() {
        for (int i = 0; i < dotCount; i++) {
            ImageView dot = new ImageView(getActivity());
            dot.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
            dot.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dot_selector));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotIndicator.addView(dot, params);
        }

        updateDotIndicator(0); // Set the first dot as active
    }

    private void updateDotIndicator(int index) {
        for (int i = 0; i < dotCount; i++) {
            ImageView dot = (ImageView) dotIndicator.getChildAt(i);
            if (i == index) {
                dot.setSelected(true);
            } else {
                dot.setSelected(false);
            }
        }
    }

    private void scrollToPosition(int position) {
        // Ensure RecyclerView and Adapter are initialized
        if (recVHomeStartCardItems != null && homeStartCardRecItemAdapter != null) {
            recVHomeStartCardItems.smoothScrollToPosition(position);
        }
    }

    public void getCardViewDimensions(View view, OnDimensionsReadyListener listener) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to prevent repeated calls
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Get the width and height of the view
                int width = view.getWidth();
                int height = view.getHeight();

                // Notify listener with the dimensions
                listener.onDimensionsReady(width, height);
            }
        });
    }

    public interface OnDimensionsReadyListener {
        void onDimensionsReady(int width, int height);
    }
//    private void updateCardViewBackground(int position) {
//        CardView[] cardViews = {mainCv, firstCv, secondCv, thirdCv, fourthCv, fifthCv};
//        for (int i = 0; i < cardViews.length; i++) {
//            if (i == position) {
//                cardViews[i].setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
//            } else {
//                cardViews[i].setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.dim_color));
//            }
//        }
//    }

//    private void setCardViewClickListeners() {
//        mainCv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewPager2.setCurrentItem(0, true);
//            }
//        });
//        firstCv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewPager2.setCurrentItem(1, true);
//            }
//        });
//        secondCv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewPager2.setCurrentItem(2, true);
//            }
//        });
//        thirdCv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewPager2.setCurrentItem(3, true);
//            }
//        });
//        fourthCv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewPager2.setCurrentItem(4, true);
//            }
//        });
//        fifthCv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewPager2.setCurrentItem(5, true);
//            }
//        });
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }
}