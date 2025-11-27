package com.example.streamingapp.presentation.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.loginTv.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.loginActivity));


        binding.signUpTv.setOnClickListener(v -> {
            if (validateInputs()) {

                Bundle bundle = new Bundle();
                bundle.putString("login", "login");

                Navigation.findNavController(requireView())
                        .navigate(R.id.pickVideoTypeActivity, bundle);
            }
        });
    }

    private boolean validateInputs() {
        String firstName = binding.fNameEdt.getText().toString().trim();
        String lastName = binding.lNameEdt.getText().toString().trim();
        String email = binding.emailEdt.getText().toString().trim();
        String password = binding.passwordEdt.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            binding.fNameEdt.setError("First Name is required");
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            binding.lNameEdt.setError("Last Name is required");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            binding.emailEdt.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEdt.setError("Enter a valid email address");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEdt.setError("Password is required");
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;    // prevent memory leaks
    }
}