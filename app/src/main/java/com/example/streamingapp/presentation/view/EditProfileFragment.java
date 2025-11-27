package com.example.streamingapp.presentation.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentEditProfileBinding;

import java.io.File;

public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEditProfileBinding.inflate(inflater, container, false);

        setupUi();
        loadUserData();

        return binding.getRoot();
    }

    private void setupUi() {
        binding.backIv.setOnClickListener(v ->
                Navigation.findNavController(v).popBackStack()
        );

        binding.cancelTv.setOnClickListener(v ->
                Navigation.findNavController(v).popBackStack()
        );

        binding.deleteTv.setOnClickListener(v -> {
            Toast.makeText(requireContext(),
                    "Your Account Deleted Successfully",
                    Toast.LENGTH_SHORT).show();

            NavController navController = Navigation.findNavController(v);
            navController.navigate(
                    R.id.loginActivity
            );
        });
    }

    private void loadUserData() {
        Bundle args = getArguments();
        if (args == null) return;

        String userGmail = args.getString("userGmail", "");
        String userImgPath = args.getString("userImgPath", "");

        binding.userGmailTv.setText(userGmail);

        if (!userImgPath.isEmpty()) {
            File file = new File(userImgPath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                binding.userIv.setImageBitmap(bitmap);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}