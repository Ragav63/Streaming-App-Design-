package com.example.streamingapp.presentation.view;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.ContentType;
import com.example.streamingapp.data.model.CrewMember;
import com.example.streamingapp.data.model.Episode;
import com.example.streamingapp.data.model.FavouriteItem;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSeriesScreenBinding;
import com.example.streamingapp.presentation.adapter.GenreFilterAdapter;
import com.example.streamingapp.presentation.utils.UtilFunctions;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SeriesScreenFragment extends Fragment  {
    private FragmentSeriesScreenBinding binding;

    private static final int DEFAULT_TINT_COLOR = R.color.white;
    private static final int SELECTED_TINT_COLOR = R.color.bluemain;
    private SeriesItems seriesItems;
    private GenreFilterAdapter genreFilterAdapter;

    private boolean isDownloading = false;
    private boolean isDownloaded = false;
    private long currentDownloadId = -1;


    private String imageResource;
    private String rating, title, year, genre, country, description;
    private List<SeasonItems> seasonList;
    private Episode episode;
    private SeasonFragment seasonFragment;
    private StreamingViewModel viewModel;
    private static final String TAG = "SeriesScreen";
    private int defaultTint;
    private int selectedTint;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSeriesScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        defaultTint = ContextCompat.getColor(requireContext(), R.color.white);
        selectedTint = ContextCompat.getColor(requireContext(), R.color.bluemain);

        getData();
        setupUI();
        setupListeners();
        loadSeasonFragment();
    }

    private void getData() {
        if (getArguments() == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            seriesItems = getArguments().getParcelable(
                    "seriesItem",
                    SeriesItems.class
            );
        } else {
            seriesItems = getArguments().getParcelable("seriesItem");
        }

        if (seriesItems == null) return;

        imageResource = seriesItems.getPoster();
        rating = seriesItems.getImdb_rating();
        title = seriesItems.getTitle();
        year = seriesItems.getYear();
        country = seriesItems.getCountry();
        genre = TextUtils.join(" • ", seriesItems.getGenres());
        seasonList = seriesItems.getSeasons();
        description = seriesItems.getPlot();
        setupTabs(seasonList);
    }


    private void setupUI() {

        Glide.with(requireContext()).load(imageResource).into(binding.seriesScreenIv);

        binding.titleTv.setText(title);
        float imdb = Float.parseFloat(rating); // 0–10

        Drawable drawable = binding.starView.getDrawable();
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            Drawable progress = layerDrawable.findDrawableByLayerId(android.R.id.progress);

            if (progress instanceof ClipDrawable) {
                // ClipDrawable level range: 0–10000
                int level = (int) (imdb / 10f * 10000);
                ((ClipDrawable) progress).setLevel(level);
            }
        }

        binding.ratingTv.setText(String.valueOf(imdb));
        binding.yearTv.setText(year);
        binding.originTv.setText(country);
        binding.recVGenre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        genreFilterAdapter = new GenreFilterAdapter(
                requireContext(),
                true,   // assign-only mode
                null    // no selection callback
        );

        binding.recVGenre.setAdapter(genreFilterAdapter);

        genreFilterAdapter.submitList(
                mapGenresToPickItems(seriesItems.getGenres())
        );
        int totalSeasons = seasonList.size();

        binding.tvTimingGenre.setText(
                getResources().getQuantityString(
                        R.plurals.series_genre_seasons,
                        totalSeasons,
                        genre,
                        totalSeasons
                )
        );

        binding.descriptionTv.setText(description);


        viewModel.getHistoryLiveData().observe(getViewLifecycleOwner(), this::applyHistoryToCurrentItem);

    }

    private void applyHistoryToCurrentItem(List<HistoryItems> historyList) {
        if (historyList == null || seriesItems == null) {
            resetContinueUi();
            return;
        }

        HistoryItems history = null;

        for (HistoryItems h : historyList) {
            if (h.getTitle().equalsIgnoreCase(seriesItems.getTitle())) {
                history = h;
                break;
            }
        }

        if (history == null) {
            resetContinueUi();
            return;
        }

        // ---- APPLY UI STATE ----
        binding.llViewed.setVisibility(View.VISIBLE);
        binding.playIv.setVisibility(View.GONE);

        long watchedMs = history.getWatchedMs();
        long totalMs = history.getDurationMs();

        if (totalMs > 0) {
            int progress = (int) ((watchedMs * 100f) / totalMs);
            binding.playerSBar.setProgress(progress);
        } else {
            binding.playerSBar.setProgress(0);
        }

        binding.tvContinueTiming.setText(UtilFunctions.formatTime(watchedMs));
    }

    private void resetContinueUi() {
        binding.llViewed.setVisibility(View.GONE);
        binding.playIv.setVisibility(View.VISIBLE);
        binding.playerSBar.setProgress(0);
        binding.tvContinueTiming.setText("");
    }

    private List<PickItem> mapGenresToPickItems(List<String> genres) {
        List<PickItem> list = new ArrayList<>();
        if (genres == null) return list;

        for (String genre : genres) {
            list.add(new PickItem(0, genre)); // img defaults to 0
        }
        return list;
    }

    private void setupListeners() {

        // Back
        binding.backIv.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack()
        );

        // Watch Now → Go to series player
        binding.playIv.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("episode",seriesItems.getSeasons().get(0).episodes.get(0));
            bundle.putParcelable("seriesItem", seriesItems);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.seriesPlayerScreenActivity, bundle);
        });

        binding.btnContinueWatching.setOnClickListener(v-> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("episode",seriesItems.getSeasons().get(0).episodes.get(0));
            bundle.putParcelable("seriesItem", seriesItems);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.seriesPlayerScreenActivity, bundle);
        });

        // Download
        binding.downloadIv.setOnClickListener(v -> {

            if (isDownloading) {
                Toast.makeText(requireContext(), "Download in progress...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isDownloaded) {
                Toast.makeText(requireContext(), "Already downloaded", Toast.LENGTH_SHORT).show();
                return;
            }

            // Start download → blue tint
            binding.downloadIv.setColorFilter(
                    ContextCompat.getColor(requireContext(), SELECTED_TINT_COLOR)
            );

            isDownloading = true;   // <-- FIXED
            isDownloaded = false;

            episode = seriesItems.getSeasons().get(0).episodes.get(0);
            String url = episode.getUrl();

            String fileName = seriesItems.getTitle() + "_" +
                    seriesItems.getSeasons().get(0).getSeasonTitle() + "_" +
                    episode.getEpisodeTitle() + ".mp4";

            fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(episode.getEpisodeTitle());
            request.setDescription("Downloading...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager dm = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
            long id = dm.enqueue(request);

            currentDownloadId = id;
        });

        binding.favIv.setOnClickListener(v -> {
            FavouriteItem favItem =
                    new FavouriteItem(ContentType.SERIES, seriesItems);

            if (viewModel.isFavourite(favItem)) {
                viewModel.removeFromFavourite(favItem);
            } else {
                viewModel.addToFavourite(favItem);
            }
        });

        viewModel.getFavouriteItems().observe(getViewLifecycleOwner(), list -> {
            FavouriteItem favItem =
                    new FavouriteItem(ContentType.SERIES, seriesItems);

            boolean fav = viewModel.isFavourite(favItem);
            updateFavouriteIcon(fav);
        });

        // Share
        binding.shareIv.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this series!");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    private void updateFavouriteIcon(boolean isFavourite) {
        if (isFavourite) {
            binding.favIv.setImageResource(R.drawable.bookmarktintfull64px);
        } else {
            binding.favIv.setImageResource(R.drawable.bookmark64px);
        }
    }

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (id == currentDownloadId) {

                // Download finished → green tint
                binding.downloadIv.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.dkgreen)
                );

                Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show();

                isDownloading = false;   // <-- IMPORTANT
                isDownloaded = true;     // <-- Only now it becomes TRUE
            }
        }
    };



    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            ContextCompat.registerReceiver(
                    requireContext(),
                    downloadReceiver,
                    filter,
                    ContextCompat.RECEIVER_NOT_EXPORTED
            );
        } else {
            ContextCompat.registerReceiver(requireContext(), downloadReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }

        viewModel.loadHistory();
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            requireContext().unregisterReceiver(downloadReceiver);
        } catch (Exception ignored) {}
    }



    private void loadSeasonFragment() {
        seasonFragment = SeasonFragment.newInstance(1, 0,seriesItems,false, false);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout, seasonFragment)
                .commit();
    }

    private void setupTabs(List<SeasonItems> seasons) {

        // Remove all previous tabs
        binding.tabLayout.clearOnTabSelectedListeners();
        binding.tabLayout.removeAllTabs();

        // Add new tabs based on seasons
        for (SeasonItems item : seasons) {
            binding.tabLayout.addTab(
                    binding.tabLayout.newTab()
                            .setText("Season " + item.getSeasonNumber())
            );
        }

        // Listener
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = getFragmentForTab(tab.getPosition());

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.framelayout, fragment)
                        .commit();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Select first tab
        if (binding.tabLayout.getTabCount() > 0) {
            binding.tabLayout.getTabAt(0).select();
        }
    }



    private Fragment getFragmentForTab(int position) {
        int seasonNumber = position + 1; // seasons start from 1
        return SeasonFragment.newInstance(seasonNumber, 0,seriesItems, false, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}