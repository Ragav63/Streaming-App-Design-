package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import com.example.streamingapp.data.model.CategoryItems;
import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.presentation.adapter.CategoryHomeRecItemAdapter;
import com.example.streamingapp.presentation.adapter.ContinueWatchingItemAdapter;
import com.example.streamingapp.data.model.ContinueWatchingItems;
import com.example.streamingapp.presentation.adapter.HomeStartCardRecItemAdapter;
import com.example.streamingapp.presentation.adapter.HomeStartPagerAdapter;
import com.example.streamingapp.presentation.adapter.NowOnTvItemAdapter;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.presentation.adapter.PopularMovieRecItemAdapter;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.presentation.adapter.PopularSeriesRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    ViewPager2 viewPager2;
    LinearLayout dotIndicator;
    private int dotCount;
    private HomeStartPagerAdapter homeStartPagerAdapter;
    private List<MovieItems> homeStartItemsList;
    private HomeStartCardRecItemAdapter homeStartCardRecItemAdapter;
    private List<MovieItems> homeStartCardListItems;
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
    private List<MovieItems> movieItemsList;
    private ContinueWatchingItemAdapter continueWatchingItemAdapter;
    private List<ContinueWatchingItems> continueWatchingItemsList;
    private CategoryHomeRecItemAdapter categoryHomeRecItemAdapter;
    private List<CategoryItems> categoryHomeItemsList;
    private NowOnTvItemAdapter nowOnTvItemAdapter;
    private List<TvItems> nowOnTvItemsList;
    private List<SeriesItems> seriesItemsList;
    private PopularSeriesRecItemAdapter popularSeriesRecItemAdapter;
//    FragmentManager fragmentManager;
//    FragmentTransaction fragmentTransaction;
//    Bundle bundle = new Bundle();
    private StreamingViewModel vm;


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
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

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

        homeStartItemsList = vm.getMovies();
        homeStartPagerAdapter = new HomeStartPagerAdapter(homeStartItemsList);
        viewPager2.setAdapter(homeStartPagerAdapter);

        homeStartCardItemsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVHomeStartCardItems.setLayoutManager(homeStartCardItemsLayoutManager);
        homeStartCardListItems = vm.getMovies();
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
                Navigation.findNavController(v)
                        .navigate(R.id.popularMoviesFragment);
            }
        });

        popularMoviesLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVPopularMovies.setLayoutManager(popularMoviesLayoutManager);
        movieItemsList = vm.getMovies();
        popularMovieRecItemAdapter = new PopularMovieRecItemAdapter(getContext(), movieItemsList);
        recVPopularMovies.setAdapter(popularMovieRecItemAdapter);
        recVPopularMovies.setHasFixedSize(true);

        seeAllContinueWatchingTv.setOnClickListener(v -> {

            Navigation.findNavController(v)
                    .navigate(R.id.continueWatchingFragment);
        });


        continueWatchingLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVContinueWatching.setLayoutManager(continueWatchingLayoutManager);
        continueWatchingItemsList = vm.getContinueWatchingItems();
        continueWatchingItemAdapter = new ContinueWatchingItemAdapter(getContext(), continueWatchingItemsList);
        recVContinueWatching.setAdapter(continueWatchingItemAdapter);
        recVContinueWatching.setHasFixedSize(true);

        categoryHomeLayoutManager=new GridLayoutManager(getContext(), 2);
        recVCategoryHome.setLayoutManager(categoryHomeLayoutManager);
        categoryHomeItemsList = vm.getCategories();
        categoryHomeRecItemAdapter = new CategoryHomeRecItemAdapter(getContext(), categoryHomeItemsList);
        recVCategoryHome.setAdapter(categoryHomeRecItemAdapter);
        recVCategoryHome.setHasFixedSize(true);

        seeAllNowOnTv.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.tvFragment);
        });

        nowOnTvLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVNowOnTv.setLayoutManager(nowOnTvLayoutManager);
        nowOnTvItemsList = vm.getNowOnTvItems();
        nowOnTvItemAdapter = new NowOnTvItemAdapter(getContext(), nowOnTvItemsList);
        recVNowOnTv.setAdapter(nowOnTvItemAdapter);
        recVNowOnTv.setHasFixedSize(true);

        seeAllPopularMoviesTv1.setOnClickListener(v->{
            Navigation.findNavController(v)
                    .navigate(R.id.popularMoviesFragment);
        });

        popularMovies1LayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVPopularMovies1.setLayoutManager(popularMovies1LayoutManager);
        recVPopularMovies1.setAdapter(popularMovieRecItemAdapter);
        recVPopularMovies1.setHasFixedSize(true);

        seeAllPopularSeriesTv.setOnClickListener(v->{
            Navigation.findNavController(v)
                    .navigate(R.id.popularSeriesFragment);
        });

        popularSeriesLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVPopularSeries.setLayoutManager(popularSeriesLayoutManager);
        seriesItemsList = vm.getSeries();
        popularSeriesRecItemAdapter = new PopularSeriesRecItemAdapter(getContext(), seriesItemsList);
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