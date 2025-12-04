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
import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalManager localManager = new LocalManager(requireContext());


        binding.loginTv.setOnClickListener(v -> {
            if (validateInputs()) {

                String email = binding.emailEdt.getText().toString().trim();
                String password = binding.passwordEdt.getText().toString().trim();

                localManager.saveLoginCredentials(email, password);

                Bundle bundle = new Bundle();
                bundle.putString("login", "login");
                Navigation.findNavController(requireView()).navigate(R.id.pickVideoTypeActivity, bundle);
            }
        });


        binding.signUpTv.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.loginActivity));

    }

    private boolean validateInputs() {
        String email = binding.emailEdt.getText().toString().trim();
        String password = binding.passwordEdt.getText().toString().trim();

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
        binding = null;  // avoid memory leaks
    }
}