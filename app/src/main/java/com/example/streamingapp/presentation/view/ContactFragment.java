package com.example.streamingapp.presentation.view;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.streamingapp.databinding.FragmentContactBinding;

import java.util.Arrays;
import java.util.List;

public class ContactFragment extends Fragment {
    private FragmentContactBinding binding;
    private Uri imageUri;

    // Proper result launcher (DO NOT use onActivityResult anymore)
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    binding.imgIv.setImageURI(uri);
                    binding.attachFileTv.setError(null);
                }
            });

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentContactBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupSpinners();
        setupListeners();
    }


    private void setupUI() {
        // Read bundle argument safely
        String userGmail = getArguments() != null
                ? getArguments().getString("userGmail", "")
                : "";

        if (!userGmail.isEmpty()) {
            binding.emailEdt.setText(userGmail);
        }
    }


    private void setupSpinners() {
        List<String> mobileTypeList = Arrays.asList("Android", "iOS", "Other");
        List<String> purposeTypeList = Arrays.asList("Feedback", "Support", "General Inquiry");

        ArrayAdapter<String> mobileTypeAdapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, mobileTypeList);
        mobileTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> purposeTypeAdapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, purposeTypeList);
        purposeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.mobileType.setAdapter(mobileTypeAdapter);
        binding.purposeType.setAdapter(purposeTypeAdapter);
    }


    private void setupListeners() {
        binding.backIv.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.attachFileTv.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        binding.submitTv.setOnClickListener(v -> {
            if (validateInputs()) {
                Toast.makeText(requireContext(), "Details Submitted Successfully!", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }


    private boolean validateInputs() {
        boolean valid = true;

        String name = binding.nameEdt.getText().toString().trim();
        String email = binding.emailEdt.getText().toString().trim();
        String feedback = binding.feedbackEdt.getText().toString().trim();

        if (name.isEmpty()) {
            binding.nameEdt.setError("Name is required");
            valid = false;
        }

        if (email.isEmpty()) {
            binding.emailEdt.setError("Email is required");
            valid = false;
        }

        if (feedback.isEmpty()) {
            binding.feedbackEdt.setError("Feedback is required");
            valid = false;
        }

        if (imageUri == null) {
            binding.attachFileTv.setError("Image is required");
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