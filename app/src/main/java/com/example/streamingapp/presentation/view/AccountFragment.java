package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.databinding.FragmentAccountBinding;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private StreamingViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        String userGmail = binding.userGmailTv.getText().toString();
        NavController navController = Navigation.findNavController(requireView());


        binding.tvUserName.setText(LocalManager.loadUserName());
        binding.userGmailTv.setText(LocalManager.loadEmail());

        PickItem savedAvatar = LocalManager.loadAvatar();

        if (savedAvatar != null && savedAvatar.getItemImg() != 0) {
            // ✅ Avatar already selected
            loadAvatarIntoViews(savedAvatar);
        } else {
            // ❌ No avatar selected → load default
            vm.loadAvators();

            vm.getAvatorLiveData().observe(getViewLifecycleOwner(), items -> {
                if (items == null || items.isEmpty()) return;

                PickItem defaultAvatar = items.get(0);

                // Save default so this runs only once
                LocalManager.saveAvatar(defaultAvatar);

                loadAvatarIntoViews(defaultAvatar);
            });
        }


        binding.btnMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;

            if (checkedId == R.id.btnDark) {
               /* AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                );*/
            } else if (checkedId == R.id.btnLight) {
              /*  AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );*/
            }
        });




        binding.userIv.setOnClickListener(v -> {
           if (LocalManager.isLoggedIn()){
               // Navigate using NavController
               navController.navigate(R.id.editProfileActivity);
           } else {
               navController.navigate(R.id.loginActivity);
           }


        });

        binding.llSettings.setOnClickListener(v ->{
                    navController.navigate(R.id.settingsActivity);

                }
        );

        binding.llAbout.setOnClickListener(v ->{
                    navController.navigate(R.id.appAboutFragment);

                }
        );

        binding.llContactSupport.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("userGmail", userGmail);
            navController
                    .navigate(R.id.contactActivity, bundle);
        });


        binding.logout.setOnClickListener(v ->{
            LocalManager.clearLogin();
                    navController
                            .navigate(R.id.loginActivity);
                }
        );
    }

    private void loadAvatarIntoViews(PickItem avatar) {
        Glide.with(requireContext())
                .load(avatar.getItemImg())
                .into(binding.userIv1);

        Glide.with(requireContext())
                .load(avatar.getItemImg())
                .into(binding.userIv);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}