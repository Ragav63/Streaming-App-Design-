package com.example.streamingapp.presentation.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.FragmentActorScreenBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ActorScreenFragment extends Fragment {

    private FragmentActorScreenBinding binding;
    private ArrayList<MovieItems> movieItemsList;
    private ArrayList<SeriesItems> seriesItemsList;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentActorScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupTabs();
    }

    private void setupUI() {
        Bundle args = getArguments();
        if (args == null) return;

        int imageResource = args.getInt("imageResource", -1);
        String actorName = args.getString("actorName");
        String actorDesc = args.getString("actorDesc");

        movieItemsList = args.getParcelableArrayList("movieList");
        seriesItemsList = args.getParcelableArrayList("seriesList");

        if (imageResource != -1) binding.actorScreenIv.setImageResource(imageResource);
        else Toast.makeText(requireContext(), "Actor Image Missing", Toast.LENGTH_SHORT).show();

        if (actorName != null) binding.actorNameTv.setText(actorName);
        if (actorDesc != null) binding.actorDescTv.setText(actorDesc);

        binding.backIv.setOnClickListener(v -> requireActivity().onBackPressed());

        // Load initial fragment
        loadFragment(0);
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadFragment(tab.getPosition());
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadFragment(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0: // Filmography
                if (movieItemsList != null) {
                    fragment = FilmographyFragment.newInstanceWithMovies(movieItemsList);
                } else if (seriesItemsList != null) {
                    fragment = FilmographyFragment.newInstanceWithSeries(seriesItemsList);
                } else {
                    Log.e("ActorScreen", "Both lists null – FIX YOUR NAVIGATION");
                    fragment = FilmographyFragment.newInstanceWithMovies(new ArrayList<>());
                }

                break;

            case 1: // Biography
                fragment = new BiographyFragment();
                Bundle data = new Bundle();
                data.putString("actorName", binding.actorNameTv.getText().toString());
                fragment.setArguments(data);
                break;
        }

        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(binding.framelayout.getId(), fragment, getFragmentTag(position))
                    .commit();
        }
    }

    private String getFragmentTag(int position) {
        switch (position) {
            case 0: return "filmography";
            case 1: return "biography";
            default: return "unknown";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}