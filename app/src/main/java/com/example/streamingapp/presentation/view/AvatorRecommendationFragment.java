package com.example.streamingapp.presentation.view;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentAvatorRecommendationBinding;
import com.example.streamingapp.presentation.adapter.AvRecomPagerAdapter;
import com.example.streamingapp.presentation.viewmodel.AvRecomPagerViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;


public class AvatorRecommendationFragment extends Fragment {

    private FragmentAvatorRecommendationBinding binding;

    private AvRecomPagerViewModel pagerVM;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAvatorRecommendationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pagerVM = new ViewModelProvider(requireActivity())
                .get(AvRecomPagerViewModel.class);

        binding.viewPager.setAdapter(
                new AvRecomPagerAdapter(requireActivity())
        );

        new TabLayoutMediator(
                binding.tabLayout,
                binding.viewPager,
                (tab, position) -> {
                    View dot = LayoutInflater.from(requireContext())
                            .inflate(R.layout.tab_dot, null);

                    tab.setCustomView(dot);
                }
        ).attach();

        TabLayout.Tab firstTab = binding.tabLayout.getTabAt(0);
        if (firstTab != null && firstTab.getCustomView() != null) {
            firstTab.getCustomView().setBackgroundResource(R.drawable.dot_selected);
        }

        pagerVM.getStepValidity().observe(getViewLifecycleOwner(), states -> {
            int current = binding.viewPager.getCurrentItem();
            boolean enabled = states != null && states[current];

            updateNextButton(enabled);
            updatePagerSwipe(enabled);
        });


        binding.viewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        boolean[] states = pagerVM.getStepValidity().getValue();
                        boolean enabled = states != null && states[position];
                        updateNextButton(enabled);
                        updatePagerSwipe(enabled);
                    }
                }
        );




        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View dot = tab.getCustomView();
                if (dot != null) {
                    dot.setBackgroundResource(R.drawable.dot_selected);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View dot = tab.getCustomView();
                if (dot != null) {
                    dot.setBackgroundResource(R.drawable.dot_unselected);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });



        // 🔥 Observe pager move
        pagerVM.getNextPage().observe(getViewLifecycleOwner(), page -> {
            if (page != null) {
                binding.viewPager.setCurrentItem(page, true);
            }
        });

        // ✅ Next button logic
        binding.btnNext.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            int lastIndex = binding.viewPager.getAdapter().getItemCount() - 1;

            boolean[] states = pagerVM.getStepValidity().getValue();
            if (states == null || !states[current]) {
                Toast.makeText(requireContext(), "Select at least one", Toast.LENGTH_SHORT).show();
                return;
            }


            if (current == lastIndex) {
                String origin = getArguments().getString("login");
                if (Objects.equals(origin, "login") || Objects.equals(origin, "googleLogin")){
                    Navigation.findNavController(requireView())
                            .navigate(R.id.homeFragment);
                } else {
                    Navigation.findNavController(requireView())
                            .navigateUp();
                }
            } else {
                pagerVM.moveToPage(current + 1);
            }
        });



        binding.btnSkip.setOnClickListener(v->{
            Navigation.findNavController(requireView()).navigate(R.id.homeFragment);
        });

        binding.viewPager.setUserInputEnabled(false);
    }

    private void updatePagerSwipe(boolean enabled) {
        binding.viewPager.setUserInputEnabled(enabled);
    }

    private void updateNextButton(boolean enabled) {
        binding.btnNext.setEnabled(enabled);

        if (enabled) {
            binding.btnNext.setBackgroundTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.bluemain)
                    )
            );
        } else {
            binding.btnNext.setBackgroundTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
                    )
            );
        }
    }
}