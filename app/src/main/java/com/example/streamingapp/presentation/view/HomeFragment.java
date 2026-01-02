package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.databinding.FragmentHomeBinding;
import com.example.streamingapp.presentation.adapter.CategoryHomeRecItemAdapter;
import com.example.streamingapp.presentation.adapter.ContinueWatchingItemAdapter;
import com.example.streamingapp.presentation.adapter.HomeStartCardRecItemAdapter;
import com.example.streamingapp.presentation.adapter.HomeStartPagerAdapter;
import com.example.streamingapp.presentation.adapter.NowOnTvItemAdapter;
import com.example.streamingapp.presentation.adapter.PopularMovieRecItemAdapter;
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
    private NowOnTvItemAdapter nowOnTvItemAdapter;

    private StreamingViewModel vm;
    private Handler sliderHandler;
    private Runnable sliderRunnable;
    private int dotCount;
    private int currentPage = 0;
    private boolean backPressedOnce = false;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        // Initialize adapters first
        initializeAdapters();

        // Setup UI components
        setupUI();

        // Observe data
        observeViewModelData();

        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {
                        if (backPressedOnce) {
                            showExitDialog();
                        } else {
                            backPressedOnce = true;

                            Toast.makeText(
                                    requireContext(),
                                    "Press back again to exit",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // reset after 2 seconds
                            new Handler(Looper.getMainLooper()).postDelayed(
                                    () -> backPressedOnce = false,
                                    2000
                            );
                        }
                    }
                });

        return binding.getRoot();
    }

    private void showExitDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    requireActivity().finishAffinity();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }


    private void initializeAdapters() {
        // Home Start Pager Adapter
        homeStartPagerAdapter = new HomeStartPagerAdapter(requireContext(), (item, pos, actionType, isFavorite) -> {
            switch (actionType) {
                case ITEM_CLICK:
                    // handle item click
                    break;
                case WATCH_NOW_CLICK:
                    // handle watch now
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("movieItem", item);
                    Navigation.findNavController(requireView()).navigate(R.id.movieScreenActivity, bundle);
                    break;
                case FAVORITE_CLICK:
                    // handle favorite toggle
                    break;
            }
        });

        // Home Start Card Adapter
        homeStartCardRecItemAdapter = new HomeStartCardRecItemAdapter(requireContext(),
                pos -> binding.homeStartViewPager.setCurrentItem(pos, true));

        // Popular Movie Adapter
        popularMovieRecItemAdapter = new PopularMovieRecItemAdapter(requireContext(),
                new ArrayList<>(), // Initialize with empty list
                (movie, pos) -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("movieItem", movie);
                    Navigation.findNavController(requireView()).navigate(R.id.movieScreenActivity, bundle);
                });

        // Continue Watching Adapter
        continueWatchingItemAdapter = new ContinueWatchingItemAdapter((item, actionType) -> {
            switch (actionType) {
                case PLAY:
                    // Handle play click
                    break;
                case REMOVE:
                    // Handle remove click
                    List<HistoryItems> currentList = new ArrayList<>(continueWatchingItemAdapter.differ.getCurrentList());
                    currentList.remove(item);
                    vm.removeHistory(item);
                    continueWatchingItemAdapter.submitList(currentList);
                    break;
            }
        });

        // Category Adapter
        categoryHomeRecItemAdapter = new CategoryHomeRecItemAdapter(item -> {
            // Handle category click
        });

        // Popular Series Adapter
        popularSeriesRecItemAdapter = new PopularSeriesRecItemAdapter(
                requireContext(),
                new ArrayList<>(), // Initialize with empty list
                (item, pos) -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("seriesItem", item);
                    Navigation.findNavController(requireView()).navigate(R.id.seriesScreenActivity, bundle);
                });


        nowOnTvItemAdapter = new NowOnTvItemAdapter(
                getContext(),
                new ArrayList<>(),
                (channel, programme, pos) -> {

                    Bundle bundle = new Bundle();
                    bundle.putInt("channelIndex", pos); // ✅ ONLY THIS
                    bundle.putString("channelName", channel.getChannelName());
                    bundle.putString("programmeUrl", programme.getUrl());

                    Navigation.findNavController(requireView())
                            .navigate(R.id.tvFragment, bundle);
                }
        );


    }



    private void setupUI() {
        // 1. Setup ViewPager
        binding.homeStartViewPager.setAdapter(homeStartPagerAdapter);
        binding.homeStartViewPager.setOffscreenPageLimit(3);
        binding.homeStartViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDotIndicator(position);
                scrollCardListTo(position);
                currentPage = position;
            }
        });

        // 2. Setup Carousel RecyclerView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            setupCarouselRecyclerView();
        } else {
            setupLegacyCarouselRecyclerView();
        }

        // 3. Setup Popular Movies RecyclerView
        binding.recVPopularMovies.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        binding.recVPopularMovies.setAdapter(popularMovieRecItemAdapter);

        // 4. Setup Continue Watching RecyclerView
        binding.recVContinueWatching.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        binding.recVContinueWatching.setAdapter(continueWatchingItemAdapter);

        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

// IMPORTANT → prevents column swapping

        binding.recVCategories.setLayoutManager(sglm);
        binding.recVCategories.setAdapter(categoryHomeRecItemAdapter);


        // 6. Setup Popular Movies 1 RecyclerView (duplicate section)
        binding.recVPopularMovies1.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        binding.recVPopularMovies1.setAdapter(popularMovieRecItemAdapter); // Same adapter as recVPopularMovies

        // 7. Setup Popular Series RecyclerView
        binding.recVPopularSeries.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        binding.recVPopularSeries.setAdapter(popularSeriesRecItemAdapter);

        binding.recVTv.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));

        binding.recVTv.setAdapter(nowOnTvItemAdapter);

        // 8. Setup See All Clicks
        setupSeeAllClicks();
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void setupCarouselRecyclerView() {
        CarouselLayoutManager layoutManager = new CarouselLayoutManager(
                CarouselLayoutManager.HORIZONTAL, true);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(3);
        binding.homeStartCardItems.setLayoutManager(layoutManager);
        binding.homeStartCardItems.setHasFixedSize(true);
        binding.homeStartCardItems.addOnScrollListener(new CenterScrollListener());
        binding.homeStartCardItems.setAdapter(homeStartCardRecItemAdapter);
    }

    private void setupLegacyCarouselRecyclerView() {
        // Fallback for older versions
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false);
        binding.homeStartCardItems.setLayoutManager(layoutManager);
        binding.homeStartCardItems.setAdapter(homeStartCardRecItemAdapter);
    }

    @SuppressLint("NewApi")
    private void observeViewModelData() {
        // Observe movies data
        vm.getMovieLiveData().observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty()) {
                // Show empty state if needed
                return;
            }

            // Process for home start (top 5 by rating)
            List<MovieItems> homeStartItemsList = items.stream()
                    .filter(item -> item.getImdb_rating() != null && !item.getImdb_rating().isEmpty())
                    .sorted((a, b) -> {
                        try {
                            float r1 = Float.parseFloat(a.getImdb_rating());
                            float r2 = Float.parseFloat(b.getImdb_rating());
                            return Float.compare(r2, r1); // descending
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .limit(5)
                    .toList();

            // Submit to home start adapters
            homeStartPagerAdapter.submitList(homeStartItemsList);
            homeStartCardRecItemAdapter.submitList(homeStartItemsList);

            // Setup dot indicator
            setupDotIndicator(homeStartItemsList.size());

            // Start auto slider if we have items
            if (homeStartItemsList.size() > 1) {
                startAutoSliderSafely();
            }

            // Submit all movies to popular movie adapter (or filtered if needed)
            popularMovieRecItemAdapter.differ.submitList(items);
        });

        // Observe continue watching data
        vm.getContinueWatchingLiveData().observe(getViewLifecycleOwner(), items -> {
            boolean hasItems = items != null && !items.isEmpty();

            binding.llContinueWatching.setVisibility(
                    hasItems ? View.VISIBLE : View.GONE
            );
            binding.recVContinueWatching.setVisibility(
                    hasItems ? View.VISIBLE : View.GONE
            );

            if (hasItems) {
                continueWatchingItemAdapter.submitList(items);
            }
        });


        // Observe categories data
        vm.getCategoryLiveData().observe(getViewLifecycleOwner(), items -> {


                categoryHomeRecItemAdapter.submitList(items);
        });

        // Observe series data
        vm.getSeriesLiveData().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                popularSeriesRecItemAdapter.differ.submitList(items);
            }
        });

        // Observe series data
        vm.getTvLiveData().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                nowOnTvItemAdapter.submitList(items);
            }
        });

        // Load data
        vm.loadMovies(); // Make sure this method exists in your ViewModel
        vm.loadContinueWatching();
        vm.loadCategories();
        vm.loadSeries();
        vm.loadTvItems();
    }

    private void setupDotIndicator(int count) {
        binding.dotIndicator.removeAllViews();
        dotCount = count;

        if (count <= 1) {
            binding.dotIndicator.setVisibility(View.GONE);
            return;
        }

        binding.dotIndicator.setVisibility(View.VISIBLE);

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(requireContext());
            dot.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
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
        if (dotCount <= 1) return;

        for (int i = 0; i < dotCount; i++) {
            ImageView dot = (ImageView) binding.dotIndicator.getChildAt(i);
            if (dot != null) {
                dot.setSelected(i == index);
            }
        }
    }

    private void startAutoSliderSafely() {
        if (dotCount <= 1) return;

        if (sliderHandler != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }

        sliderHandler = new Handler(Looper.getMainLooper());
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (dotCount > 0) {
                    int next = (currentPage + 1) % dotCount;
                    binding.homeStartViewPager.setCurrentItem(next, true);
                    sliderHandler.postDelayed(this, 5000);
                }
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 5000);
    }

    private void scrollCardListTo(int pos) {
        if (binding.homeStartCardItems.getLayoutManager() != null) {
            binding.homeStartCardItems.smoothScrollToPosition(pos);
            homeStartCardRecItemAdapter.updateSelectedPosition(pos);
        }
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

        binding.seeAllTv.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.tvFragment));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sliderHandler != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dotCount > 1) {
            startAutoSliderSafely();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sliderHandler != null) {
            sliderHandler.removeCallbacksAndMessages(null);
        }
        binding = null;
    }
}