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


        binding.btnSignIn.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.loginActivity));

        binding.ivGoogleSigUp.setOnClickListener(v ->{
            Bundle bundle = new Bundle();
            bundle.putString("login", "login");
            Navigation.findNavController(requireView()).navigate(R.id.avatorRecommendationFragment, bundle);
    });


        binding.btnSignUp.setOnClickListener(v -> {
            if (validateInputs()) {
                String userName = binding.nameEdt.getText().toString().trim();
                String email = binding.emailEdt.getText().toString().trim();
                String password = binding.passwordEdt.getText().toString().trim();
                LocalManager.saveLogin(userName, email, password);

                Bundle bundle = new Bundle();
                bundle.putString("login", "login");

                Navigation.findNavController(requireView())
                        .navigate(R.id.avatorRecommendationFragment, bundle);
            }
        });
    }

    private boolean validateInputs() {

        String userName = binding.nameEdt.getText().toString().trim();
        String email = binding.emailEdt.getText().toString().trim();
        String password = binding.passwordEdt.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            binding.nameEdt.setError("UserName is required");
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