package com.example.streamingapp.presentation.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamingapp.R;
import com.example.streamingapp.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupClicks();
    }

    private void setupClicks() {

        // Back button
        binding.backIv.setOnClickListener(v ->
                requireActivity().onBackPressed()
        );

        // Navigate to PickVideoTypeFragment
        binding.reChooseInterestTv.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        R.id.pickVideoTypeActivity
                )
        );

        // Parental Control Switch
        binding.parentalControlSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(requireContext(),
                        "Parental Control Enabled,\nGo to Contact Btv Support in Account to View or Manage",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(),
                        "Parental Control Disabled",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Download via Wifi Switch
        binding.downloadWifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(requireContext(), "Download Via Wifi Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Download Via Wifi Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // AutoPlay Switch
        binding.autoPlaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(requireContext(), "Auto Play Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Auto Play Disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leaks
    }
}