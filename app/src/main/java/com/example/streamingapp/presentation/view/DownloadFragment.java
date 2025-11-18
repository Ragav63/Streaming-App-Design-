package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.streamingapp.data.model.DownloadItems;
import com.example.streamingapp.presentation.adapter.DownloadRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class DownloadFragment extends Fragment {
    ImageView backIv;
    private RecyclerView recVDownload;
    RecyclerView.LayoutManager downloadLayoutManager;
    private List<DownloadItems> downloadItemsList;
    private DownloadRecItemAdapter downloadRecItemAdapter;

    private StreamingViewModel vm;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        backIv = view.findViewById(R.id.backIv);
        recVDownload = view.findViewById(R.id.recVDownload);

        backIv.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });


        downloadLayoutManager = new LinearLayoutManager(getActivity());
        recVDownload.setLayoutManager(downloadLayoutManager);
        downloadItemsList = vm.getDownloadItems();
        downloadRecItemAdapter = new DownloadRecItemAdapter(getActivity(), downloadItemsList);
        recVDownload.setAdapter(downloadRecItemAdapter);
        recVDownload.setHasFixedSize(true);

        return view;
    }
}