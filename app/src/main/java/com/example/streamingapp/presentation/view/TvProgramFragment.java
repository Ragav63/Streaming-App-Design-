package com.example.streamingapp.presentation.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.streamingapp.data.model.Programme;
import com.example.streamingapp.data.model.TvChannel;
import com.example.streamingapp.data.model.TvChannelUiItem;
import com.example.streamingapp.databinding.FragmentTvProgramBinding;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.adapter.TvProgramRecItemAdapter;
import com.example.streamingapp.data.model.TvProgramTimingItems;
import com.example.streamingapp.presentation.adapter.TvProgramTimingRecItemAdapter;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class TvProgramFragment extends Fragment  {
    private FragmentTvProgramBinding binding;

    private String tvname;

    private List<TvProgramTimingItems> tvProgramTimingItemsList = new ArrayList<>();
    private List<TvChannelUiItem> tvProgramItemsList = new ArrayList<>();

    private TvProgramTimingRecItemAdapter tvProgramTimingRecItemAdapter;
    private TvProgramRecItemAdapter tvProgramRecItemAdapter;

    private StreamingViewModel vm;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            tvname = getArguments().getString("tvName");
            Log.d("TvName", "Values "+tvname);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTvProgramBinding.inflate(inflater, container, false);
        vm = new ViewModelProvider(requireActivity(), new StreamingViewModelFactory()).get(StreamingViewModel.class);

        setupUI();
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
    private void setupTimingRecycler(List<TvProgramTimingItems> timingItems) {

        binding.recVTiming.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        tvProgramTimingRecItemAdapter = new TvProgramTimingRecItemAdapter(
                selectedTime -> updateProgramsForSelectedTime(selectedTime)
        );

        binding.recVTiming.setAdapter(tvProgramTimingRecItemAdapter);
        tvProgramTimingRecItemAdapter.submitList(timingItems);
    }


    // --------------------------------------------------------------------
    // PROGRAM RECYCLER
    // --------------------------------------------------------------------
    private void setupProgramRecycler() {

        binding.recVProgrammes.setLayoutManager(new LinearLayoutManager(requireContext()));

        tvProgramRecItemAdapter = new TvProgramRecItemAdapter(item ->
                Toast.makeText(requireContext(),
                        "Currently Watching: " + item.getProgrammeName(),
                        Toast.LENGTH_SHORT).show()
        );

        binding.recVProgrammes.setAdapter(tvProgramRecItemAdapter);
        vm.loadTvItems();
        vm.getTvLiveData().observe(getViewLifecycleOwner(), channels -> {

            if (channels == null || channels.isEmpty() || tvname == null) {
                tvProgramRecItemAdapter.submitList(new ArrayList<>());
                return;
            }

            for (TvChannel channel : channels) {

                if (!channel.getChannelName().equalsIgnoreCase(tvname)) continue;

                // 1. Map programmes to UI items
                tvProgramItemsList = mapChannelsToUi(
                        Collections.singletonList(channel)
                );
                tvProgramRecItemAdapter.submitList(tvProgramItemsList);

                // 2. Build timing list from REAL programmes
                List<TvProgramTimingItems> timingItems = new ArrayList<>();

                if (channel.getProgrammes() != null) {
                    for (Programme programme : channel.getProgrammes()) {
                        timingItems.add(
                                new TvProgramTimingItems(programme.getTiming())
                        );
                    }
                }

                // 3. Apply timings
                setupTimingRecycler(timingItems);

                return; // single channel handled
            }
        });



    }


    public List<TvChannelUiItem> mapChannelsToUi(List<TvChannel> channels) {
        List<TvChannelUiItem> uiList = new ArrayList<>();

        for (TvChannel channel : channels) {
            if (channel.getProgrammes() != null && !channel.getProgrammes().isEmpty()) {
                Programme current = null;
                for (Programme p : channel.getProgrammes()) {
                    if ("live".equalsIgnoreCase(p.getStatus())) {
                        current = p;
                        break;
                    }
                }
                if (current == null) current = channel.getProgrammes().get(0); // fallback

                uiList.add(new TvChannelUiItem(
                        channel.getChannelLogo(),
                        channel.getChannelName(),
                        current.getName(),
                        current.getTiming(),
                        current.getUrl(),
                        current.getStatus()
                ));
            }
        }

        return uiList;
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
    // TIMING SELECTION HANDLING
    // --------------------------------------------------------------------
    private void updateProgramsForSelectedTime(String selectedTime) {

        List<TvChannelUiItem> reordered = new ArrayList<>();
        List<TvChannelUiItem> others = new ArrayList<>();

        for (TvChannelUiItem item : tvProgramItemsList) {
            if (item.getProgrammeTiming().equals(selectedTime)) {
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