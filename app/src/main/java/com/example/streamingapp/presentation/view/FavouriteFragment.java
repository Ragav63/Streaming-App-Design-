package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.HistoryUiItem;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.databinding.FragmentFavouriteBinding;
import com.example.streamingapp.presentation.adapter.HistoryRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavouriteFragment extends Fragment {

    private FragmentFavouriteBinding binding;

    private HistoryRecItemAdapter historyRecItemAdapter;
    private List<HistoryItems> historyItemsList;
    private List<HistoryItems> historyCache;
    private List<MovieItems> movieCache;


    private StreamingViewModel vm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        setupUI();

        return view;
    }

    private void setupUI() {

        binding.recVHistory.setLayoutManager(
                new GridLayoutManager(requireContext(), 2)
        );

        historyRecItemAdapter = new HistoryRecItemAdapter(
                requireContext(),
                src -> {
                    Bundle b = new Bundle();
                    b.putString("imageResource", src);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.fullScreenImageActivity, b);
                }
        );

        binding.recVHistory.setAdapter(historyRecItemAdapter);

        vm.getHistoryLiveData().observe(getViewLifecycleOwner(), history -> {
            historyCache = history;
            tryBuildUiList();
        });

        vm.getMovieLiveData().observe(getViewLifecycleOwner(), movies -> {
            movieCache = movies;
            tryBuildUiList();
        });

        vm.loadHistory();
        vm.loadMovies();
    }

    private void tryBuildUiList() {
        if (historyCache == null || movieCache == null) return;

        Map<String, MovieItems> movieMap = new HashMap<>();
        for (MovieItems movie : movieCache) {
            movieMap.put(movie.getTitle(), movie);
        }

        List<HistoryUiItem> uiList = new ArrayList<>();

        for (HistoryItems h : historyCache) {
            MovieItems movie = movieMap.get(h.getHistoryTitle());
            if (movie == null) continue;

            uiList.add(new HistoryUiItem(
                    h.getId(),
                    h.getHistoryTitle(),
                    h.getHistoryTiming(),
                    movie.getImdb_rating(),
                    movie.getPoster()
            ));
        }

        historyRecItemAdapter.submitList(uiList);
    }






    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}