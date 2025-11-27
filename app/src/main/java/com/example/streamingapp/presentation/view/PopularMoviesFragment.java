package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.databinding.FragmentPopularMoviesBinding;
import com.example.streamingapp.presentation.adapter.PopularMovieRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class PopularMoviesFragment extends Fragment {
    private FragmentPopularMoviesBinding binding;
    private PopularMovieRecItemAdapter adapter;
    private List<MovieItems> movieItemsList;

    private StreamingViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPopularMoviesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Back -> navController.navigateUp()
        binding.backIv.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        movieItemsList = vm.getMovies();
        if (movieItemsList != null && !movieItemsList.isEmpty()) {
            binding.recVPopularMovies.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            adapter = new PopularMovieRecItemAdapter(requireContext(), movieItemsList, (movie, pos) -> {
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
                        new ArrayList<>(adapter.getCurrentList())
                );
                // Navigate using NavController
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.movieScreenActivity, bundle);
            });
            binding.recVPopularMovies.setAdapter(adapter);
            binding.recVPopularMovies.setHasFixedSize(true);
        }
    }

    public void updatePopularMovies(List<MovieItems> items) {
        if (adapter == null) return;
        movieItemsList.clear();
        movieItemsList.addAll(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}