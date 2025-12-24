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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.CrewMember;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.FragmentActorScreenBinding;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ActorScreenFragment extends Fragment {

    private FragmentActorScreenBinding binding;
    private Boolean isMovie;
    private CastItems castItems;
    private ArrayList<MovieItems> movieItemsList;
    private ArrayList<SeriesItems> seriesItemsList;
    private StreamingViewModel viewModel;

    private String actorName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentActorScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        setupUI();
        setupTabs();
    }

    private void setupUI() {
        Bundle args = getArguments();
        if (args == null) return;
        isMovie = args.getBoolean("isMovie");
        castItems = args.getParcelable("castItem");
        actorName = castItems.getPersonName();

        Glide.with(requireContext())
                .load(castItems.getPersonImages().get(0))
                .into(binding.actorScreenIv);

        binding.actorNameTv.setText(actorName);
        binding.actorDescTv.setText(castItems.getPersonDesignation());

        binding.backIv.setOnClickListener(v -> requireActivity().onBackPressed());


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
                if (actorName != null) {
                    fragment = FilmographyFragment.newInstance(actorName, isMovie);
                }

                break;

            case 1: // Biography
                fragment = BiographyFragment.newInstance(castItems);

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