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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.streamingapp.R;
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.databinding.FragmentAccountBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userGmail = binding.userGmailTv.getText().toString();
        NavController navController = Navigation.findNavController(requireView());

        LocalManager prefs = new LocalManager(requireContext());

        binding.editProfileTv.setOnClickListener(v -> {
            Drawable drawable = binding.userIv.getDrawable();
            if (drawable != null) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                File imageFile = saveBitmapToFile(bitmap);

                if (imageFile != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userGmail", userGmail);
                    bundle.putString("userImgPath", imageFile.getAbsolutePath());
                    // Navigate using NavController
                    navController.navigate(R.id.editProfileActivity, bundle);
                }
            }
        });

        binding.settingsTv.setOnClickListener(v ->{
                    navController.navigate(R.id.settingsActivity);

                }
        );

        binding.helpCenterTv.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("userGmail", userGmail);
            navController
                    .navigate(R.id.contactActivity, bundle);
        });

        binding.contactBtvTv.setOnClickListener(v ->{
            navController
                    .navigate(R.id.parentalControlActivity);
        }
        );

        binding.logout.setOnClickListener(v ->{
            prefs.clearAllPrefs();
                    navController
                            .navigate(R.id.loginActivity);
                }
        );
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File directory = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ProfilePictures");
        if (!directory.exists()) directory.mkdirs();

        File imageFile = new File(directory, "user_profile.png");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}