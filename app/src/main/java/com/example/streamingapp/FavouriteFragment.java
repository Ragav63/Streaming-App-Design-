package com.example.streamingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends Fragment {
    TextView downloadTv, favMoviesAndSeriesTv, favChannelsTv;
    private RecyclerView recVHistory;
    RecyclerView.LayoutManager historyLayoutManager;
    private List<HistoryItems> historyItemsList;
    private HistoryRecItemAdapter historyRecItemAdapter;



    private List<HistoryItems> generateHistoryItemList(){
        List<HistoryItems> itemsList = new ArrayList<>();
        itemsList.add(new HistoryItems("7.3","View 10.06.2024", R.drawable.venom3verticalnew));
        itemsList.add(new HistoryItems("7.0","View 06.06.2024", R.drawable.avatarthelastairbenderverticalnew));
        itemsList.add(new HistoryItems("8.0","View 10.05.2024", R.drawable.avengersverticalnew));
        itemsList.add(new HistoryItems("6.5","View 01.05.2024", R.drawable.avatarthewayofwaterverticalnew1));
        itemsList.add(new HistoryItems("7.2","View 28.04.2024", R.drawable.kalkiverticalnew));
        itemsList.add(new HistoryItems("7.3","View 25.04.2024", R.drawable.captainamericaverticalnew));

        return itemsList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

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
                fragmentTransaction.replace(R.id.container, downloadFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        historyLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVHistory.setLayoutManager(historyLayoutManager);
        historyItemsList = generateHistoryItemList();
        historyRecItemAdapter = new HistoryRecItemAdapter(getContext(), historyItemsList);
        recVHistory.setAdapter(historyRecItemAdapter);
        recVHistory.setHasFixedSize(true);


        return view;
    }
}