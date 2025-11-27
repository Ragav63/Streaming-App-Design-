package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.databinding.FragmentTvSelectionBinding;
import com.example.streamingapp.presentation.adapter.TvSelectionRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class TvSelectionFragment extends Fragment {
    private FragmentTvSelectionBinding binding;
    private StreamingViewModel vm;
    private TvSelectionRecItemAdapter tvSelectionRecItemAdapter;
    private List<TvItems> tvItemsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTvSelectionBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory())
                .get(StreamingViewModel.class);

        binding.recVTvNames.setLayoutManager(new LinearLayoutManager(requireContext()));

        tvItemsList = vm.getNowOnTvItems();
        tvSelectionRecItemAdapter =  new TvSelectionRecItemAdapter(
                item -> {
                    TvProgramFragment frag = new TvProgramFragment();
                    Bundle b = new Bundle();
                    b.putString("tvName", item.getTvName());
                    frag.setArguments(b);

                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.tvFrameLayout, frag)
                            .addToBackStack(null)
                            .commit();
                },
                item -> {
                    item.setFavorite(!item.isFavorite());

                    // force UI refresh
                    tvSelectionRecItemAdapter.submitList(new ArrayList<>(tvSelectionRecItemAdapter.differ.getCurrentList()));
                }
        );

        binding.recVTvNames.setAdapter(tvSelectionRecItemAdapter);
        tvSelectionRecItemAdapter.submitList(tvItemsList);
        binding.recVTvNames.setHasFixedSize(true);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leaks
    }
}