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
import com.example.streamingapp.presentation.adapter.TvSelectionRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class TvSelectionFragment extends Fragment {
    private RecyclerView recVTvSelection;
    private List<TvItems> tvItemsList;
    private TvSelectionRecItemAdapter tvSelectionRecItemAdapter;

    private StreamingViewModel vm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tv_selection, container, false);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);
        recVTvSelection = view.findViewById(R.id.recVTvNames);

        recVTvSelection.setLayoutManager(new LinearLayoutManager(getActivity()));
        tvItemsList = vm.getNowOnTvItems();
        tvSelectionRecItemAdapter = new TvSelectionRecItemAdapter(this, tvItemsList);
        recVTvSelection.setAdapter(tvSelectionRecItemAdapter);
        recVTvSelection.setHasFixedSize(true);



        return view;
    }
}