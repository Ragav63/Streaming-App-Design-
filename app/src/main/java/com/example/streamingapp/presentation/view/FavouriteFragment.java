package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.streamingapp.data.model.ContentType;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.HistoryUiItem;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.FragmentFavouriteBinding;
import com.example.streamingapp.presentation.adapter.FavouriteAdapter;
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
    private FavouriteAdapter adapter;
    private StreamingViewModel vm;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(
                requireActivity(),
                new StreamingViewModelFactory()
        ).get(StreamingViewModel.class);

        binding.recVFavourites.setLayoutManager(
                new GridLayoutManager(requireContext(), 2)
        );
        adapter = new FavouriteAdapter(this::navigateToDetail);
        binding.recVFavourites.setAdapter(adapter);

        // Example source – adjust to your VM
        vm.getFavouriteItems().observe(getViewLifecycleOwner(), favs -> {
            Log.d("FavouriteVal","Favourite items are "+favs.toString());
            adapter.submitList(favs);
        });
        vm.loadFavourites();
    }

    private void navigateToDetail(Object item) {
        Bundle bundle = new Bundle();

        if (item instanceof MovieItems) {
            bundle.putParcelable("movieItem", (MovieItems) item);
            Navigation.findNavController(requireView())
                    .navigate(R.id.movieScreenActivity, bundle);

        } else if (item instanceof SeriesItems) {
            bundle.putParcelable("seriesItem", (SeriesItems) item);
            Navigation.findNavController(requireView())
                    .navigate(R.id.seriesScreenActivity, bundle);
        }
    }


}