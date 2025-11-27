package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.databinding.FragmentFilmographyBinding;
import com.example.streamingapp.presentation.adapter.FilmographyRecItemAdapter;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class FilmographyFragment extends Fragment {

    private FragmentFilmographyBinding binding;

    private FilmographyRecItemAdapter<Parcelable> filmographyRecItemAdapter;
    private List<Parcelable> itemList;
    private boolean isMovieList;
    private StreamingViewModel vm;

    public FilmographyFragment() {}

    public static FilmographyFragment newInstanceWithMovies(List<MovieItems> movieItemsList) {
        FilmographyFragment fragment = new FilmographyFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("itemList", new ArrayList<>(movieItemsList != null ? movieItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", true);
        fragment.setArguments(args);
        return fragment;
    }

    public static FilmographyFragment newInstanceWithSeries(List<SeriesItems> seriesItemsList) {
        FilmographyFragment fragment = new FilmographyFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("itemList", new ArrayList<>(seriesItemsList != null ? seriesItemsList : new ArrayList<>()));
        args.putBoolean("isMovieList", false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            Log.d("FilmographyFragment", "Arguments are null");
            itemList = new ArrayList<>();
            return;
        }

        isMovieList = args.getBoolean("isMovieList");
        // Cast safely
        if (isMovieList) {
            itemList = new ArrayList<>(args.getParcelableArrayList("itemList"));
        } else {
            itemList = new ArrayList<>(args.getParcelableArrayList("itemList"));
        }
        if (itemList == null) itemList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFilmographyBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        setupRecycler();
        return binding.getRoot();
    }

    private void setupRecycler() {
        binding.recVFilmography.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.recVFilmography.setHasFixedSize(true);



        filmographyRecItemAdapter = new FilmographyRecItemAdapter<Parcelable>(item -> {
            Bundle bundle = new Bundle();

            if (item instanceof MovieItems) {
                MovieItems movie = (MovieItems) item;
                bundle.putInt("imageResource", movie.getImage());
                bundle.putString("title", movie.getTitle());
                bundle.putString("rating", movie.getImdbRating());
                bundle.putString("year", movie.getYear());
                bundle.putString("genre", movie.getGenre());
                bundle.putString("country", movie.getCountry());
                bundle.putString("duration", movie.getDuration());
                bundle.putString("description", movie.getDescription());
                bundle.putParcelableArrayList("popularMovieItemsList", new ArrayList<>(itemList));

                Navigation.findNavController(requireView())
                        .navigate(R.id.movieScreenActivity, bundle);

            } else if (item instanceof SeriesItems) {
                SeriesItems series = (SeriesItems) item;
                bundle.putInt("imageResource", series.getImage());
                bundle.putString("title", series.getTitle());
                bundle.putString("rating", series.getImdbRating());
                bundle.putString("year", series.getYear());
                bundle.putString("genre", series.getGenre());
                bundle.putString("country", series.getCountry());
                bundle.putString("seasons", series.getSeasons());
                bundle.putString("description", series.getDescription());
                bundle.putParcelableArrayList("popularSeriesItemsList", new ArrayList<>(itemList));

                Navigation.findNavController(requireView())
                        .navigate(R.id.seriesScreenActivity, bundle);
            }
        });

        binding.recVFilmography.setAdapter(filmographyRecItemAdapter);


        if (itemList.isEmpty()) {
            Log.d("FilmographyFragment", "Item list is empty");
            filmographyRecItemAdapter.submitList(vm.getMovies());
        } else  {
            Log.d("FilmographyFragment", itemList.toString());

            filmographyRecItemAdapter.submitList(itemList);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}