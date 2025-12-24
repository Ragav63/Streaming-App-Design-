package com.example.streamingapp.presentation.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.data.model.CastItems;
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
    private CastItems castItem;   // <-- store cast item


    public static Fragment newInstance(CastItems item) {
        BiographyFragment fragment = new BiographyFragment();
        Bundle args = new Bundle();
        args.putParcelable("cast_item", item);
        fragment.setArguments(args);
        return fragment;
    }

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

        castItem = getArguments().getParcelable("cast_item");
        if (castItem == null) return;

        String actorName = castItem.getCastName();   // <-- use CastItems
        String currentText = castItem.getCastAbout();

        SpannableString ss = new SpannableString(actorName + " " + currentText);

        ss.setSpan(
                new ForegroundColorSpan(Color.WHITE),
                0, actorName.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ss.setSpan(
                new RelativeSizeSpan(1.75f),
                0, actorName.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        binding.actorDetailsTv.setText(ss);
    }


    private void setupPhotosRecycler() {
        binding.recVBiographyPhotos.setLayoutManager(
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false)
        );

        // Your CastItems returns List<String>, so convert it
        List<String> imageUrls = castItem.getPersonImages();
        List<AboutPhotosItems> photos = new ArrayList<>();

        if (imageUrls != null) {
            for (String url : imageUrls) {
                photos.add(new AboutPhotosItems(url));   // convert here
            }
        }

        BiographyPhotosRecItemAdapter adapter =
                new BiographyPhotosRecItemAdapter(requireContext(), imageRes -> {
                    Bundle b = new Bundle();
                    b.putString("imageResource", imageRes);
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.fullScreenImageActivity, b);
                });

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