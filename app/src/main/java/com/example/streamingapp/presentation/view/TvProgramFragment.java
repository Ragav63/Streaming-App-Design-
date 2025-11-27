package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.databinding.FragmentTvProgramBinding;
import com.example.streamingapp.domain.repository.OnTimeSelectedListener;
import com.example.streamingapp.domain.repository.OnTimingSelectedListener;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.adapter.TvProgramRecItemAdapter;
import com.example.streamingapp.data.model.TvProgramTimingItems;
import com.example.streamingapp.presentation.adapter.TvProgramTimingRecItemAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class TvProgramFragment extends Fragment  {
    private FragmentTvProgramBinding binding;

    private String tvname;

    private List<TvProgramTimingItems> tvProgramTimingItemsList = new ArrayList<>();
    private List<TvItems> tvProgramItemsList = new ArrayList<>();

    private TvProgramTimingRecItemAdapter tvProgramTimingRecItemAdapter;
    private TvProgramRecItemAdapter tvProgramRecItemAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            tvname = getArguments().getString("tvName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTvProgramBinding.inflate(inflater, container, false);

        setupUI();
        setupTimingRecycler();
        setupProgramRecycler();

        return binding.getRoot();
    }

    private void setupUI() {
        binding.tvName.setText(tvname);
        binding.backIv.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    // --------------------------------------------------------------------
    // TIMING RECYCLER
    // --------------------------------------------------------------------
    private void setupTimingRecycler() {

        binding.recVTiming.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        tvProgramTimingItemsList = generateProgramTimingItemList();

        tvProgramTimingRecItemAdapter = new TvProgramTimingRecItemAdapter(selectedTime -> {
            updateProgramsForSelectedTime(selectedTime);
        });

        binding.recVTiming.setAdapter(tvProgramTimingRecItemAdapter);
        tvProgramTimingRecItemAdapter.submitList(tvProgramTimingItemsList);
    }

    // --------------------------------------------------------------------
    // PROGRAM RECYCLER
    // --------------------------------------------------------------------
    private void setupProgramRecycler() {

        binding.recVProgrammes.setLayoutManager(new LinearLayoutManager(requireContext()));

        tvProgramRecItemAdapter = new TvProgramRecItemAdapter(item ->
                Toast.makeText(requireContext(),
                        "Currently Watching: " + item.getCurrentProgramName(),
                        Toast.LENGTH_SHORT).show()
        );

        binding.recVProgrammes.setAdapter(tvProgramRecItemAdapter);

        tvProgramItemsList = generateTvProgramItems();
        tvProgramRecItemAdapter.submitList(tvProgramItemsList);
    }

    // --------------------------------------------------------------------
    // TIMING LIST GENERATION
    // --------------------------------------------------------------------
    private List<TvProgramTimingItems> generateProgramTimingItemList() {

        List<TvProgramTimingItems> itemsList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        for (int i = 0; i < 12; i++) {
            itemsList.add(new TvProgramTimingItems(timeFormat.format(calendar.getTime())));
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }

        return itemsList;
    }

    // --------------------------------------------------------------------
    // PROGRAM LIST GENERATION
    // --------------------------------------------------------------------
    private List<TvItems> generateTvProgramItems() {

        List<TvItems> itemsList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        for (int i = 0; i < 12; i++) {

            String time = timeFormat.format(calendar.getTime());

            int image;
            String title;

            switch (i) {
                case 0: image = R.drawable.avatarhz; title = "Avatar The Way of Water"; break;
                case 1: image = R.drawable.avengers; title = "Avengers: The Endgame"; break;
                case 2: image = R.drawable.captainamerica; title = "Euphoria S1E1"; break;
                case 3: image = R.drawable.avatarhz4; title = "Shogun S1E3"; break;
                case 4: image = R.drawable.gots01e08; title = "High School Musical"; break;
                case 5: image = R.drawable.gots01e01; title = "GOT S1E1"; break;
                case 6: image = R.drawable.gots01e02; title = "GOT S1E2"; break;
                case 7: image = R.drawable.gots01e03; title = "GOT S1E3"; break;
                case 8: image = R.drawable.gots01e04; title = "GOT S1E4"; break;
                case 9: image = R.drawable.gots01e05; title = "GOT S1E5"; break;
                case 10: image = R.drawable.gots01e06; title = "GOT S1E6"; break;
                case 11: image = R.drawable.avatarthelastairbender; title = "Avatar The Last Airbender"; break;
                default: image = R.drawable.gots01e01; title = "Game of Thrones";
            }

            // FIXED: correct argument order (5 parameters)
            itemsList.add(
                    new TvItems(
                            "ESPN",      // tvLogoName
                            "ESPN HD",   // tvName (ADD THE CHANNEL NAME)
                            title,       // currentProgramName
                            time,        // currentProgramTiming
                            image        // img
                    )
            );

            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }

        return itemsList;
    }

    // --------------------------------------------------------------------
    // TIMING SELECTION HANDLING
    // --------------------------------------------------------------------
    private void updateProgramsForSelectedTime(String selectedTime) {

        List<TvItems> reordered = new ArrayList<>();
        List<TvItems> others = new ArrayList<>();

        for (TvItems item : tvProgramItemsList) {
            if (item.getCurrentProgramTiming().equals(selectedTime)) {
                reordered.add(item);
            } else {
                others.add(item);
            }
        }

        reordered.addAll(others);

        tvProgramRecItemAdapter.submitList(reordered);

        binding.recVProgrammes.post(() ->
                binding.recVProgrammes.scrollToPosition(0)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}