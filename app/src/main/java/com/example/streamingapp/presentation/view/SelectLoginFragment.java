package com.example.streamingapp.presentation.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSelectLoginBinding;

public class SelectLoginFragment extends Fragment {
    private FragmentSelectLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentSelectLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.loginTv.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.loginActivity));

        binding.loginFbTv.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.loginActivity));

        binding.loginGoogleTv.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.loginActivity));

        binding.signUpTv.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.signUpActivity));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // prevent memory leaks
    }
}