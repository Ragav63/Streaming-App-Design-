package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.streamingapp.presentation.adapter.ContinueWatchingFragmentItemAdapter;
import com.example.streamingapp.data.model.ContinueWatchingItems;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.List;


public class ContinueWatchingFragment extends Fragment {
    ImageView backIv;
    private RecyclerView recVContinueWatching;
    private ContinueWatchingFragmentItemAdapter continueWatchingFragmentItemAdapter;
    private List<ContinueWatchingItems> continueWatchingItemsList;

    private StreamingViewModel vm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_continue_watching, container, false);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        recVContinueWatching = view.findViewById(R.id.recVContinueWatching);
        backIv = view.findViewById(R.id.backIv);

        backIv.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        continueWatchingItemsList = vm.getContinueWatchingItems();

        continueWatchingFragmentItemAdapter = new ContinueWatchingFragmentItemAdapter(getContext(), continueWatchingItemsList);
        recVContinueWatching.setAdapter(continueWatchingFragmentItemAdapter);

        return view;
    }
}