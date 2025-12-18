package com.example.streamingapp.presentation.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.streamingapp.presentation.view.AvatorFragment;
import com.example.streamingapp.presentation.view.PickGenresFragment;
import com.example.streamingapp.presentation.view.PickVideoTypeFragment;

public class AvRecomPagerAdapter extends FragmentStateAdapter {


    public AvRecomPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AvatorFragment();
            case 1:
                return new PickVideoTypeFragment();
            case 2:
                return new PickGenresFragment();
            default:
                throw new IllegalStateException("Invalid position " + position);
        }
    }
}
