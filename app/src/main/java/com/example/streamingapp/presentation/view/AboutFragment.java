package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.databinding.FragmentAboutBinding;
import com.example.streamingapp.presentation.adapter.AboutPhotosRecItemAdapter;
import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.presentation.adapter.CastRecItemAdapter;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;
    private CastRecItemAdapter castRecItemAdapter;
    private AboutPhotosRecItemAdapter aboutPhotosRecItemAdapter;

    private List<CastItems> castItemsList = new ArrayList<>();
    private List<AboutPhotosItems> aboutPhotosItemsList = new ArrayList<>();
    private List<MovieItems> movieItemsList;
    private List<SeriesItems> seriesItemsList;
    private boolean isMovieList;

    private StreamingViewModel vm;

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstanceWithMovies(List<MovieItems> movieItemsList) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularMovieItemsList", new ArrayList<>(movieItemsList != null ? movieItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", true);
        fragment.setArguments(args);
        return fragment;
    }

    public static AboutFragment newInstanceWithSeries(List<SeriesItems> seriesItemsList) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("popularSeriesItemsList", new ArrayList<>(seriesItemsList != null ? seriesItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        // Initialize with empty lists to avoid null pointers
        castItemsList = new ArrayList<>();
        aboutPhotosItemsList = new ArrayList<>();

        if (getArguments() != null) {
            isMovieList = getArguments().getBoolean("isMovieList", true);
            if (isMovieList) {
                movieItemsList = getArguments().getParcelableArrayList("popularMovieItemsList");
                Log.d("AboutFragment", "Received movie items: " + (movieItemsList != null ? movieItemsList.size() : "null"));
            } else {
                seriesItemsList = getArguments().getParcelableArrayList("popularSeriesItemsList");
                Log.d("AboutFragment", "Received series items: " + (seriesItemsList != null ? seriesItemsList.size() : "null"));
            }
        } else {
            Log.d("AboutFragment", "getArguments() is null");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAdapters();
        loadData();
    }

    private void setupAdapters() {
        // Setup Cast RecyclerView
        binding.recVCast.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        castRecItemAdapter = new CastRecItemAdapter(castItem -> {
            Bundle bundle = new Bundle();
            bundle.putInt("imageResource", castItem.getPersonImg());
            bundle.putString("actorName", castItem.getPersonName());
            bundle.putString("actorDesc", castItem.getPersonDesignation());

            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.actorScreenActivity, bundle);
        });
        binding.recVCast.setAdapter(castRecItemAdapter);

        // Setup Photos RecyclerView
        binding.recVPhotos.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        aboutPhotosRecItemAdapter = new AboutPhotosRecItemAdapter(src -> {
            Bundle b = new Bundle();
            b.putString("imageSource", src);
            Navigation.findNavController(requireView()).navigate(R.id.fullScreenImageActivity, b);
        });
        binding.recVPhotos.setAdapter(aboutPhotosRecItemAdapter);
    }



    private void loadData() {
        // Get data directly from UseCases via ViewModel
        List<CastItems> currentCast = vm.getCast();
        List<AboutPhotosItems> currentPhotos = vm.getPhotos();

        Log.d("AboutFragment", "Current cast from VM: " + (currentCast != null ? currentCast.size() : "null"));
        Log.d("AboutFragment", "Current photos from VM: " + (currentPhotos != null ? currentPhotos.size() : "null"));

        // Debug each cast item
        if (currentCast != null) {
            for (int i = 0; i < currentCast.size(); i++) {
                CastItems item = currentCast.get(i);
                Log.d("AboutFragment", "Cast item " + i + ": " + item.getPersonName() + ", img: " + item.getPersonImg());
            }
        }

        // Update cast data
        if (currentCast != null && !currentCast.isEmpty()) {
            castItemsList = new ArrayList<>(currentCast); // Create a new list to ensure updates
            castRecItemAdapter.submitList(castItemsList);

            // Force update the RecyclerView
            castRecItemAdapter.notifyDataSetChanged();
            Log.d("AboutFragment", "Cast data set successfully. Count: " + castItemsList.size());
        } else {
            Log.d("AboutFragment", "No cast data available");
        }

        // Update photos data
        if (currentPhotos != null && !currentPhotos.isEmpty()) {
            aboutPhotosItemsList = new ArrayList<>(currentPhotos);
            aboutPhotosRecItemAdapter.differ.submitList(aboutPhotosItemsList);
            Log.d("AboutFragment", "Photos data set successfully. Count: " + aboutPhotosItemsList.size());
        } else {
            Log.d("AboutFragment", "No photos data available");
        }
    }

    private void setupDummyData() {
        Log.d("AboutFragment", "Setting up dummy data for testing");

        // Create dummy cast data
        List<CastItems> dummyCast = new ArrayList<>();
        dummyCast.add(new CastItems("Actor One", "Lead Role", R.drawable.samworthington));
        dummyCast.add(new CastItems("Actor Two", "Supporting Role", R.drawable.samworthington));
        dummyCast.add(new CastItems("Actor Three", "Villain", R.drawable.samworthington));

        castItemsList = dummyCast;
        castRecItemAdapter.submitList(castItemsList);

        // Create dummy photos data
        List<AboutPhotosItems> dummyPhotos = new ArrayList<>();
        dummyPhotos.add(new AboutPhotosItems(R.drawable.avatarthelastairbendervertical));
        dummyPhotos.add(new AboutPhotosItems(R.drawable.avengersvertical));
        dummyPhotos.add(new AboutPhotosItems(R.drawable.captainamerica));

        aboutPhotosItemsList = dummyPhotos;
        aboutPhotosRecItemAdapter.submitList(aboutPhotosItemsList);

        Log.d("AboutFragment", "Dummy data set - Cast: " + castItemsList.size() + ", Photos: " + aboutPhotosItemsList.size());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}