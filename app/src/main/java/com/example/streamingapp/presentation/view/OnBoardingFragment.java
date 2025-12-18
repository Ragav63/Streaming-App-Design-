package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.OnBoardItem;
import com.example.streamingapp.databinding.FragmentOnBoardingBinding;
import com.example.streamingapp.presentation.adapter.OnBoardAdapter;

import java.util.Arrays;
import java.util.List;


public class OnBoardingFragment extends Fragment {


    private FragmentOnBoardingBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Data for pages
        List<OnBoardItem> list = Arrays.asList(
                new OnBoardItem("Welcome", "This is your streaming home."),
                new OnBoardItem("Discover", "Explore thousands of movies & shows."),
                new OnBoardItem("Start Watching", "Enjoy content instantly.")
        );

        OnBoardAdapter adapter = new OnBoardAdapter(list);
        binding.onbViewPager.setAdapter(adapter);

        binding.onbViewPager.setOffscreenPageLimit(3);

        setupDotIndicator(list.size());

        // Change dot on page change
        binding.onbViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDotIndicator(position);
            }
        });

        binding.btnLoginRegister.setOnClickListener(v->{
            Navigation.findNavController(getView())
                    .navigate(R.id.selectLoginActivity);
        });

        binding.btnTryGuest.setOnClickListener(v->{
            Navigation.findNavController(getView())
                    .navigate(R.id.homeFragment);
        });
    }

    private void setupDotIndicator(int count) {
        binding.llDotIndicator.removeAllViews();

        for (int i = 0; i < count; i++) {
            View dot = new View(requireContext());
            dot.setBackgroundResource(R.drawable.dot_unselected); // default inactive dot
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dp(8), dp(8)
            );
            params.setMargins(dp(4), 0, dp(4), 0);
            dot.setLayoutParams(params);
            binding.llDotIndicator.addView(dot);
        }

        // Set first dot as active
        updateDotIndicator(0);
    }

    private void updateDotIndicator(int position) {
        int count = binding.llDotIndicator.getChildCount();
        for (int i = 0; i < count; i++) {
            View dot = binding.llDotIndicator.getChildAt(i);
            if (i == position) {
                dot.setBackgroundResource(R.drawable.dot_selected);
            } else {
                dot.setBackgroundResource(R.drawable.dot_unselected);
            }
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

}