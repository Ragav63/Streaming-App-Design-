package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.streamingapp.domain.repository.OnTimeSelectedListener;
import com.example.streamingapp.domain.repository.OnTimingSelectedListener;
import com.example.streamingapp.R;
import com.example.streamingapp.data.model.TvProgramItems;
import com.example.streamingapp.presentation.adapter.TvProgramRecItemAdapter;
import com.example.streamingapp.data.model.TvProgramTimingItems;
import com.example.streamingapp.presentation.adapter.TvProgramTimingRecItemAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class TvProgramFragment extends Fragment implements OnTimingSelectedListener, OnTimeSelectedListener {
    String tvname;
    ImageView backIv;
    TextView tvName;
    private RecyclerView recVTimings, recVPrograms;
    RecyclerView.LayoutManager timingLayoutManager, programsLayoutManager;
    private List<TvProgramTimingItems> tvProgramTimingItemsList;
    private TvProgramTimingRecItemAdapter tvProgramTimingRecItemAdapter;
    private List<TvProgramItems> tvProgramItemsList;
    private TvProgramRecItemAdapter tvProgramRecItemAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tvname = getArguments().getString("tvName");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tv_program, container, false);

        tvName = view.findViewById(R.id.tvName);
        backIv = view.findViewById(R.id.backIv);
        recVTimings = view.findViewById(R.id.recVTiming);
        recVPrograms = view.findViewById(R.id.recVProgrammes);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        tvName.setText(tvname);

        timingLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recVTimings.setLayoutManager(timingLayoutManager);
        tvProgramTimingItemsList = generateProgramTimingItemList();
        tvProgramTimingRecItemAdapter = new TvProgramTimingRecItemAdapter(getActivity(), tvProgramTimingItemsList, this);
        recVTimings.setAdapter(tvProgramTimingRecItemAdapter);

        programsLayoutManager = new LinearLayoutManager(getActivity());
        recVPrograms.setLayoutManager(programsLayoutManager);
        tvProgramItemsList = generateTvProgramItems();
        tvProgramRecItemAdapter = new TvProgramRecItemAdapter(getActivity(), tvProgramItemsList);
        recVPrograms.setAdapter(tvProgramRecItemAdapter);

        return view;
    }

    @Override
    public void onTimeSelected(String selectedTime) {
        updateProgramsForSelectedTime(selectedTime);
    }

    private List<TvProgramTimingItems> generateProgramTimingItemList() {
        List<TvProgramTimingItems> itemsList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        // Round to the next hour
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Generate timings for the next 12 hours
        for (int i = 0; i < 12; i++) {
            String time = timeFormat.format(calendar.getTime());
            itemsList.add(new TvProgramTimingItems(time));
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }

        return itemsList;
    }

    private List<TvProgramItems> generateTvProgramItems() {
        List<TvProgramItems> itemsList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        // Round to the next hour
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Generate program items for the next 12 hours
        for (int i = 0; i < 12; i++) {
            String time = timeFormat.format(calendar.getTime());
            int imageResource;
            String programTitle;

            // Customize each item based on position or other criteria
            switch (i) {
                case 0:
                    imageResource = R.drawable.avatarhz; // First item image
                    programTitle = "Avatar The Way of Water";
                    break;
                case 1:
                    imageResource = R.drawable.avengers;
                    programTitle = "Avengers: The Endgame";
                    break;
                case 2:
                    imageResource = R.drawable.captainamerica;
                    programTitle = "Euphoria - Season 1, Episode 1";
                    break;
                case 3:
                    imageResource = R.drawable.avatarhz4;
                    programTitle = "Shongun - Season 1, Episode 3";
                    break;
                case 4:
                    imageResource = R.drawable.gots01e08;
                    programTitle = "High School Musical";
                    break;
                case 5:
                    imageResource = R.drawable.gots01e01;
                    programTitle = "Game Of Thrones Season 1 Ep-1";
                    break;
                case 6:
                    imageResource = R.drawable.gots01e02;
                    programTitle = "Game Of Thrones Season 1 Ep-2";
                    break;
                case 7:
                    imageResource = R.drawable.gots01e03;
                    programTitle = "Game Of Thrones Season 1 Ep-1";
                    break;
                case 8:
                    imageResource = R.drawable.gots01e04;
                    programTitle = "Game Of Thrones Season 1 Ep-4";
                    break;
                case 9:
                    imageResource = R.drawable.gots01e05;
                    programTitle = "Game Of Thrones Season 1 Ep-5";
                    break;
                case 10:
                    imageResource = R.drawable.gots01e06;
                    programTitle = "Game Of Thrones Season 1 Ep-6";
                    break;
                case 11:
                    imageResource = R.drawable.avatarthelastairbender; // Last item image
                    programTitle = "Avatar The Last Airbender";
                    break;
                default:
                    imageResource = R.drawable.gots01e01; // Default image for other items
                    programTitle = "Game of Thrones";
                    break;
            }

            itemsList.add(new TvProgramItems(imageResource, time, programTitle));
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }

        return itemsList;
    }

    @Override
    public void onTimingSelected(int position) {
        String selectedTime = tvProgramTimingItemsList.get(position).getTiming();
        updateProgramsForSelectedTime(selectedTime);
    }

    private void updateProgramsForSelectedTime(String selectedTime) {
        // Create a new list to hold the reordered items
        List<TvProgramItems> reorderedList = new ArrayList<>();
        List<TvProgramItems> otherItems = new ArrayList<>();

        // Iterate over the items and separate the selected timing item from others
        for (TvProgramItems item : tvProgramItemsList) {
            if (item.getProgramTiming().equals(selectedTime)) {
                reorderedList.add(item); // Add selected item at the top
            } else {
                otherItems.add(item); // Add other items to a separate list
            }
        }

        // Add other items below the selected item
        reorderedList.addAll(otherItems);

        // Update the adapter with the reordered list
        tvProgramRecItemAdapter.updateProgramList(reorderedList);
    }

}