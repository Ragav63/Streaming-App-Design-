package com.example.streamingapp.presentation.view;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamingapp.data.local.LocalManager;
import com.example.streamingapp.databinding.FragmentParentalControlBinding;
import com.example.streamingapp.databinding.ParentControlPinBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class ParentalControlFragment extends BottomSheetDialogFragment {

    private FragmentParentalControlBinding binding;

    private static final String[] SCREEN_TIME_OPTIONS = {
            "30 mins",
            "1 hour",
            "2 hours",
            "Unlimited"
    };


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentParentalControlBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.backIv.setOnClickListener(v -> dismiss());

        binding.rlChangePin.setOnClickListener(v -> showChangePinDialog(false));

        ArrayAdapter<String> screenTimeAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        SCREEN_TIME_OPTIONS
                );

        screenTimeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        binding.screenTimeSpinner.setAdapter(screenTimeAdapter);

        int savedIndex = LocalManager.getScreenTimeIndex();
        binding.screenTimeSpinner.setSelection(savedIndex);

        binding.screenTimeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        LocalManager.setScreenTimeIndex(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );


        boolean enabled = LocalManager.isParentalEnabled();
        binding.parentalControlSwitch.setChecked(enabled);
        binding.maturitySlider.setEnabled(enabled);
        binding.screenTimeSpinner.setEnabled(enabled);


        binding.maturitySlider.setValue(
                LocalManager.getMaturityLevel()
        );

        // Load saved state
        binding.parentalControlSwitch.setChecked(
                LocalManager.isParentalEnabled()
        );

        // Save when toggled
        binding.parentalControlSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                // User is trying to enable
                if (LocalManager.hasParentalPin()) {
                    // PIN already exists → allow enable
                    LocalManager.setParentalEnabled(true);
                    binding.maturitySlider.setEnabled(true);
                    binding.screenTimeSpinner.setEnabled(true);

                } else {
                    // No PIN → force PIN setup
                    binding.parentalControlSwitch.setChecked(false);
                    binding.maturitySlider.setEnabled(false);
                    showChangePinDialog(true); // force mode
                }
            } else {
                // User disabled parental control
                LocalManager.setParentalEnabled(false);
                binding.maturitySlider.setEnabled(false);
            }
        });


        // Restore saved value
        int savedLevel = LocalManager.getMaturityLevel();
        binding.maturitySlider.setValue(savedLevel);

// Save when changed
        binding.maturitySlider.addOnChangeListener((slider, value, fromUser) -> {
            if (!fromUser) return;

            int level = (int) value;
            LocalManager.setMaturityLevel(level);
        });


    }

    private void showChangePinDialog(boolean isMandatory) {

        ParentControlPinBinding dialogBinding =
                ParentControlPinBinding.inflate(LayoutInflater.from(requireContext()));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(!isMandatory)
                .create();

        dialogBinding.enterTv.setOnClickListener(v -> {
            String pin = dialogBinding.codeEdt.getText().toString().trim();

            if (pin.length() < 4) {
                dialogBinding.codeEdt.setError("Minimum 4-digit PIN");
                return;
            }

            LocalManager.setParentalPin(pin);
            LocalManager.setParentalEnabled(true);

            binding.parentalControlSwitch.setChecked(true);
            binding.maturitySlider.setEnabled(true);
            binding.screenTimeSpinner.setEnabled(true);

            Toast.makeText(requireContext(), "PIN Set Successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialogBinding.ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (isMandatory) {
            dialog.setOnCancelListener(d -> {
                // User backed out → parental stays OFF
                LocalManager.setParentalEnabled(false);
                binding.parentalControlSwitch.setChecked(false);
                binding.maturitySlider.setEnabled(false);
                binding.screenTimeSpinner.setEnabled(false);
            });
        }

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior.from(parent)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
