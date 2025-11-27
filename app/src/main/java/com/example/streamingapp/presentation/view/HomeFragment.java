package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
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
import com.example.streamingapp.databinding.FragmentHomeBinding;
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
import com.mig35.carousellayoutmanager.CarouselLayoutManager;
import com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.mig35.carousellayoutmanager.CenterScrollListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    private HomeStartPagerAdapter homeStartPagerAdapter;
    private HomeStartCardRecItemAdapter homeStartCardRecItemAdapter;

    private PopularMovieRecItemAdapter popularMovieRecItemAdapter;
    private PopularSeriesRecItemAdapter popularSeriesRecItemAdapter;

    private ContinueWatchingItemAdapter continueWatchingItemAdapter;
    private CategoryHomeRecItemAdapter categoryHomeRecItemAdapter;
    private List<MovieItems> homeStartItemsList;
    private List<MovieItems> movieItemsList;
    private List<MovieItems> homeStartCardListItems;
    private List<ContinueWatchingItems> continueWatchingItemsList;
    private List<CategoryItems> categoryHomeItemsList;
    private List<SeriesItems> seriesItemsList;

    private int dotCount;
    private int currentPage = 0;

    private Handler sliderHandler;
    private Runnable sliderRunnable;

    private StreamingViewModel vm;

    private Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private Runnable autoScrollRunnable;
    private boolean isAutoScrollEnabled = true;
    private CarouselLayoutManager carouselLayoutManager;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        setupViewPager();
        setupHomeStartCardRecycler();
        setupAutoSlider();
        setupPopularMoviesSection();
        setupContinueWatchingSection();
        setupCategoriesSection();
        setupPopularMovies1Section();
        setupPopularSeriesSection();
        setupSeeAllClicks();

        return binding.getRoot();
    }

    private void setupViewPager() {
        homeStartItemsList = vm.getMovies();
        homeStartPagerAdapter = new HomeStartPagerAdapter((item, pos, actionType, isFavorite) -> {
            switch (actionType) {
                case ITEM_CLICK:
                    // handle item click
                    break;
                case WATCH_NOW_CLICK:
                    // handle watch now
                    break;
                case FAVORITE_CLICK:
                    // handle favorite toggle
                    break;
            }
        });


        homeStartPagerAdapter.submitList(homeStartItemsList);
        binding.homeStartViewPager.setAdapter(homeStartPagerAdapter);

        dotCount = homeStartItemsList.size();
        setupDotIndicator();

        binding.homeStartViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDotIndicator(position);
                scrollCardListTo(position);
                currentPage = position;
            }
        });
    }

    private void setupHomeStartCardRecycler() {

        // 1) Set up proper carousel (horizontal + circular)
        CarouselLayoutManager layoutManager =
                new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);

        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(3);

        binding.homeStartCardItems.setLayoutManager(layoutManager);
        binding.homeStartCardItems.setHasFixedSize(true);
        binding.homeStartCardItems.addOnScrollListener(new CenterScrollListener());

        // 2) Adapter
        homeStartCardRecItemAdapter = new HomeStartCardRecItemAdapter(pos ->
                binding.homeStartViewPager.setCurrentItem(pos, true)
        );

        List<MovieItems> cardList = vm.getMovies();
        homeStartCardRecItemAdapter.submitList(cardList);
        binding.homeStartCardItems.setAdapter(homeStartCardRecItemAdapter);

        // 3) Sync ViewPager → Recycler
        binding.homeStartViewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        binding.homeStartCardItems.smoothScrollToPosition(position);
                        homeStartCardRecItemAdapter.updateSelectedPosition(position);
                    }
                }
        );
    }

    private void pauseAutoScroll() {
        isAutoScrollEnabled = false;
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    private void resumeAutoScroll() {
        isAutoScrollEnabled = true;
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
    }



    private void setupAutoSlider() {
        sliderHandler = new Handler(Looper.getMainLooper());
        sliderRunnable = () -> {
            int next = (currentPage + 1) % dotCount;
            binding.homeStartViewPager.setCurrentItem(next);
            sliderHandler.postDelayed(sliderRunnable, 5000);
        };
        sliderHandler.postDelayed(sliderRunnable, 5000);
    }

    private void setupPopularMoviesSection() {
        binding.recVPopularMovies.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        movieItemsList = vm.getMovies();
        popularMovieRecItemAdapter = new PopularMovieRecItemAdapter(requireContext(), movieItemsList, (movie, pos) -> {
            Bundle bundle = new Bundle();
            bundle.putInt("imageResource", movie.getImage());
            bundle.putString("rating", movie.getImdbRating());
            bundle.putString("title", movie.getTitle());
            bundle.putString("year", movie.getYear());
            bundle.putString("genre", movie.getGenre());
            bundle.putString("country", movie.getCountry());
            bundle.putString("duration", movie.getDuration());
            bundle.putString("description", movie.getDescription());
            bundle.putParcelableArrayList(
                    "popularMovieItemsList",
                    new ArrayList<>(popularMovieRecItemAdapter.getCurrentList())
            );
            // Navigate using NavController
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.movieScreenActivity, bundle);
        });
        binding.recVPopularMovies.setAdapter(popularMovieRecItemAdapter);
    }

    private void setupContinueWatchingSection() {
        binding.recVContinueWatching.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        continueWatchingItemsList = vm.getContinueWatchingItems();
        continueWatchingItemAdapter = new ContinueWatchingItemAdapter((item, actionType) -> {
            switch (actionType) {
                case PLAY:
                    // Handle play click
                    break;
                case REMOVE:
                    // Handle remove click
                    List<ContinueWatchingItems> currentList = new ArrayList<>(continueWatchingItemAdapter.differ.getCurrentList());
                    currentList.remove(item);
                    continueWatchingItemAdapter.submitList(currentList);
                    break;
            }
        });
        binding.recVContinueWatching.setAdapter(continueWatchingItemAdapter);
        continueWatchingItemAdapter.submitList(continueWatchingItemsList);
    }

    private void setupCategoriesSection() {
        binding.recVCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        categoryHomeItemsList = vm.getCategories();
        categoryHomeRecItemAdapter = new CategoryHomeRecItemAdapter(item ->{

        });
        categoryHomeRecItemAdapter.submitList(categoryHomeItemsList);
        binding.recVCategories.setAdapter(categoryHomeRecItemAdapter);
    }


    private void setupPopularMovies1Section() {
        binding.recVPopularMovies1.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recVPopularMovies1.setAdapter(popularMovieRecItemAdapter);
    }

    private void setupPopularSeriesSection() {
        binding.recVPopularSeries.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        seriesItemsList = vm.getSeries();
        popularSeriesRecItemAdapter = new PopularSeriesRecItemAdapter(
                requireContext(),
                seriesItemsList,
                (item, pos) -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("imageResource", item.getImage());
                    bundle.putString("rating", item.getImdbRating());
                    bundle.putString("title", item.getTitle());
                    bundle.putString("year", item.getYear());
                    bundle.putString("genre", item.getGenre());
                    bundle.putString("country", item.getCountry());
                    bundle.putString("seasons", item.getSeasons());
                    bundle.putString("description", item.getDescription());
                    bundle.putParcelableArrayList(
                            "popularSeriesItemsList",
                            new ArrayList<>(popularSeriesRecItemAdapter.getCurrentList())
                    );
                    // Navigate using NavController
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.seriesScreenActivity, bundle);

                }
        );
        binding.recVPopularSeries.setAdapter(popularSeriesRecItemAdapter);
    }

    private void setupSeeAllClicks() {
        binding.seeAllPopularMoviesTv.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.popularMoviesFragment));

        binding.seeAllContinueWatchingTv.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.continueWatchingFragment));





        binding.seeAllPopularMovies1Tv.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.popularMoviesFragment));

        binding.seeAllPopularSeriesTv.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.popularSeriesFragment));
    }


    private void setupDotIndicator() {
        binding.dotIndicator.removeAllViews();
        for (int i = 0; i < dotCount; i++) {
            ImageView dot = new ImageView(requireContext());
            dot.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
            dot.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dot_selector));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            binding.dotIndicator.addView(dot, params);
        }
        updateDotIndicator(0);
    }

    private void updateDotIndicator(int index) {
        for (int i = 0; i < dotCount; i++) {
            ImageView dot = (ImageView) binding.dotIndicator.getChildAt(i);
            dot.setSelected(i == index);
        }
    }

    private void scrollCardListTo(int pos) {
        binding.homeStartCardItems.smoothScrollToPosition(pos);
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (homeStartCardListItems != null && homeStartCardListItems.size() > 1) {
            resumeAutoScroll();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoScrollHandler.removeCallbacksAndMessages(null);
        if (sliderHandler != null) sliderHandler.removeCallbacks(sliderRunnable);
        binding = null;
    }
}