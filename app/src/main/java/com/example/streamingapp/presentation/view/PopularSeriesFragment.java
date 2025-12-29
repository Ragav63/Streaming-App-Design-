package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
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

import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.databinding.FragmentPopularSeriesBinding;
import com.example.streamingapp.presentation.adapter.PopularSeriesRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class PopularSeriesFragment extends Fragment {
    private FragmentPopularSeriesBinding binding;
    private PopularSeriesRecItemAdapter adapter;
    private StreamingViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPopularSeriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);


        binding.backIv.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        vm.loadSeries();

        binding.recVPopularSeries.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter =  new PopularSeriesRecItemAdapter(
                requireContext(),
                new ArrayList<>(),
                (item, pos) -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("seriesItem",item);
                    // Navigate using NavController
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.seriesScreenActivity, bundle);

                }
        );
        binding.recVPopularSeries.setAdapter(adapter);
        vm.getSeriesLiveData().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}