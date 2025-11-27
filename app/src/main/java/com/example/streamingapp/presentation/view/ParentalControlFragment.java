package com.example.streamingapp.presentation.view;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamingapp.databinding.FragmentParentalControlBinding;


public class ParentalControlFragment extends Fragment {
    private FragmentParentalControlBinding binding;
    private Uri imageUri;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    binding.imgIv.setImageURI(uri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentParentalControlBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // back button → navigateUp()
        binding.backIv.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        // pick image
        binding.imgIv.setOnClickListener(v ->
                pickImageLauncher.launch("image/*")
        );

        // submit
        binding.enterTv.setOnClickListener(v -> {
            if (validateInputs()) {

                // If you want to send something back, use Bundle:
                Bundle bundle = new Bundle();
                bundle.putString("selectedImage", imageUri.toString());
                bundle.putString("parentCode", binding.codeEdt.getText().toString());

                // Example navigation (replace with your actual destination)
                Navigation.findNavController(v).navigateUp();

                Toast.makeText(requireContext(), "Control Enabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        boolean valid = true;

        String code = binding.codeEdt.getText().toString().trim();

        if (code.isEmpty()) {
            binding.codeEdt.setError("Code is required");
            valid = false;
        }

        if (imageUri == null) {
            Toast.makeText(requireContext(), "Image is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}