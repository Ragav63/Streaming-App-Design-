package com.example.streamingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class DownloadFragment extends Fragment {
    ImageView backIv;
    private RecyclerView recVDownload;
    RecyclerView.LayoutManager downloadLayoutManager;
    private List<DownloadItems> downloadItemsList;
    private DownloadRecItemAdapter downloadRecItemAdapter;

    private List<DownloadItems> generateDownloadItemsList(){
        List<DownloadItems> itemsList = new ArrayList<>();
        itemsList.add(new DownloadItems("Avatar: The Way of Water", "","1h 34min / 7.2 gb / 720p", R.drawable.avatarthewayofwatervertical));
        itemsList.add(new DownloadItems("Game of Thrones, Season 1", "Episode 1, Winter is Coming","1h 34min / 7.2 gb / 720p", R.drawable.got));

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
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        backIv = view.findViewById(R.id.backIv);
        recVDownload = view.findViewById(R.id.recVDownload);

        backIv.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });


        downloadLayoutManager = new LinearLayoutManager(getActivity());
        recVDownload.setLayoutManager(downloadLayoutManager);
        downloadItemsList = generateDownloadItemsList();
        downloadRecItemAdapter = new DownloadRecItemAdapter(getActivity(), downloadItemsList);
        recVDownload.setAdapter(downloadRecItemAdapter);
        recVDownload.setHasFixedSize(true);

        return view;
    }
}