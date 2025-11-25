package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.presentation.adapter.HistoryRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends Fragment {
    TextView downloadTv, favMoviesAndSeriesTv, favChannelsTv;
    private RecyclerView recVHistory;
    RecyclerView.LayoutManager historyLayoutManager;
    private List<HistoryItems> historyItemsList;
    private HistoryRecItemAdapter historyRecItemAdapter;


    private StreamingViewModel vm;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        downloadTv = view.findViewById(R.id.downloadTv);
        favMoviesAndSeriesTv = view.findViewById(R.id.favMoviesAndSeriesTv);
        favChannelsTv = view.findViewById(R.id.favChannelsTv);
        recVHistory = view.findViewById(R.id.recVHistory);

        downloadTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFragment downloadFragment = new DownloadFragment();

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, downloadFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        historyLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVHistory.setLayoutManager(historyLayoutManager);
        historyItemsList = vm.getHistoryItems();
        historyRecItemAdapter = new HistoryRecItemAdapter(getContext(), historyItemsList);
        recVHistory.setAdapter(historyRecItemAdapter);
        recVHistory.setHasFixedSize(true);


        return view;
    }
}