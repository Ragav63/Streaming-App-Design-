package com.example.streamingapp.presentation.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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
import com.example.streamingapp.databinding.FragmentBiographyBinding;
import com.example.streamingapp.presentation.adapter.BiographyPhotosRecItemAdapter;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;


public class BiographyFragment extends Fragment {

    private FragmentBiographyBinding binding;
    private StreamingViewModel vm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBiographyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        vm = new ViewModelProvider(requireActivity(),
                new StreamingViewModelFactory()).get(StreamingViewModel.class);

        setupActorName();
        setupPhotosRecycler();

        return view;
    }

    private void setupActorName() {
        if (getArguments() == null) return;

        String actorName = getArguments().getString("actorName");
        if (actorName == null || actorName.isEmpty()) return;

        String currentText = binding.actorDetailsTv.getText().toString();
        SpannableString ss = new SpannableString(actorName + " " + currentText);

        ss.setSpan(new ForegroundColorSpan(Color.WHITE),
                0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ss.setSpan(new RelativeSizeSpan(1.75f),
                0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.actorDetailsTv.setText(ss);
    }

    private void setupPhotosRecycler() {
        binding.recVBiographyPhotos.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        List<AboutPhotosItems> photos = vm.getPhotos();

        BiographyPhotosRecItemAdapter adapter = new BiographyPhotosRecItemAdapter(
                imageRes -> {
                    Bundle b = new Bundle();
                    b.putString("imageResource", imageRes);

                    Navigation.findNavController(requireActivity(),
                            requireParentFragment().getView().getId()
                    ).navigate(R.id.fullScreenImageActivity, b);
                }
        );

        binding.recVBiographyPhotos.setAdapter(adapter);
        adapter.differ.submitList(photos);
        binding.recVBiographyPhotos.setHasFixedSize(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}