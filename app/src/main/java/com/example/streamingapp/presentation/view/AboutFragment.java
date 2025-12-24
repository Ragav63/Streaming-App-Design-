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
import com.example.streamingapp.data.model.CrewMember;
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
    private boolean isMovie;
    private MovieItems movieItem;
    private SeriesItems seriesItem;

    private StreamingViewModel vm;

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstanceWithMovies(MovieItems item) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putParcelable("movie_item", item);
        args.putBoolean("isMovie", true);
        fragment.setArguments(args);
        return fragment;
    }


    public static AboutFragment newInstanceWithSeries(SeriesItems seriesItem) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putParcelable("seriesItem", seriesItem);
        args.putBoolean("isMovie", false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);


        if (getArguments() != null) {
            isMovie = getArguments().getBoolean("isMovie");
            if (isMovie) {
                movieItem = getArguments().getParcelable("movie_item");
            } else {
                seriesItem = getArguments().getParcelable("seriesItem");
                Log.d("AboutFragment", "Received series items: " + (seriesItem != null));
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
        castRecItemAdapter = new CastRecItemAdapter(requireContext(),castItem -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isMovie",isMovie);
            bundle.putParcelable("castItem",castItem);
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.actorScreenActivity, bundle);
        });
        binding.recVCast.setAdapter(castRecItemAdapter);

        // Setup Photos RecyclerView
        binding.recVPhotos.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        aboutPhotosRecItemAdapter = new AboutPhotosRecItemAdapter(requireContext(),src -> {
            Bundle b = new Bundle();
            b.putString("imageSource", src);
            Navigation.findNavController(requireView()).navigate(R.id.fullScreenImageActivity, b);
        });
        binding.recVPhotos.setAdapter(aboutPhotosRecItemAdapter);
    }



    private void loadData() {

        if (movieItem != null) {
            // ---------------- MOVIE DATA ----------------
            binding.audioTrackValTv.setText(movieItem.getLanguage());
            binding.subtitleValTv.setText(movieItem.getLanguage());

            // CAST
            loadCast(movieItem.getCrew());

            // PHOTOS
            loadPhotos(movieItem.getImages());

        } else if (seriesItem != null) {
            // ---------------- SERIES DATA ----------------
            binding.audioTrackValTv.setText(seriesItem.getLanguage());
            binding.subtitleValTv.setText(seriesItem.getLanguage());

            // CAST
            loadCast(seriesItem.getCrew());
            Log.d("AboutFragment", "crew values "+seriesItem.getCrew().toString());

            // PHOTOS
            loadPhotos(seriesItem.getImages());
            Log.d("AboutFragment", "images values "+seriesItem.getImages().toString());
        } else {
            Log.e("AboutFragment", "Both movieItem and seriesItem are NULL. Something is wrong.");
        }
    }

    private void loadCast(List<CrewMember> crewList) {
        List<CastItems> cast = new ArrayList<>();

        if (crewList != null) {
            for (CrewMember c : crewList) {
                cast.add(new CastItems(
                        c.getName(),
                        c.getDesignation(),
                        c.getImages()
                ));
            }
        }

        castRecItemAdapter.submitList(cast);
    }

    private void loadPhotos(List<String> imageUrls) {
        List<AboutPhotosItems> photos = new ArrayList<>();

        if (imageUrls != null) {
            for (String url : imageUrls) {
                photos.add(new AboutPhotosItems(url));
            }
        }

        aboutPhotosRecItemAdapter.differ.submitList(photos);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}