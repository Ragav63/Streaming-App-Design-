package com.example.streamingapp.presentation.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.presentation.adapter.BiographyPhotosRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class BiographyFragment extends Fragment {
    TextView actorDetailsTv;
    private RecyclerView recVPhotos;
    private RecyclerView.LayoutManager photosLayoutManager;
    private BiographyPhotosRecItemAdapter biographyPhotosRecItemAdapter;
    private List<AboutPhotosItems> biographyPhotosItemsList;

    private StreamingViewModel vm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_biography, container, false);

        actorDetailsTv= view.findViewById(R.id.actorDetailsTv);
        recVPhotos = view.findViewById(R.id.recVBiographyPhotos);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        if (getArguments() != null) {
            String actorName = getArguments().getString("actorName");
            if (actorName != null && !actorName.isEmpty()) {
                String currentText = actorDetailsTv.getText().toString();

                // Create a SpannableString
                SpannableString spannableString = new SpannableString(actorName + " " + currentText);

                // Set the span for the first letter
                spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new RelativeSizeSpan(1.75f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                actorDetailsTv.setText(spannableString);
            }
        }

        photosLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVPhotos.setLayoutManager(photosLayoutManager);
        biographyPhotosItemsList = vm.getPhotos();
        biographyPhotosRecItemAdapter = new BiographyPhotosRecItemAdapter(getContext(), biographyPhotosItemsList);
        recVPhotos.setAdapter(biographyPhotosRecItemAdapter);
        recVPhotos.setHasFixedSize(true);

        return view;
    }
}