package com.example.streamingapp.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.data.local.StreamingDataSource;
import com.example.streamingapp.data.repository.StreamingRepositoryImpl;
import com.example.streamingapp.domain.usecase.GetGenreListUseCase;
import com.example.streamingapp.presentation.adapter.PickGenreRecItemAdapter;
import com.example.streamingapp.data.model.PickGenreTypeRecItem;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class PickGenresActivity extends AppCompatActivity {

    ImageView backIv;
    private RecyclerView recVPickGenre;
    RecyclerView.LayoutManager layoutManager;
    private PickGenreRecItemAdapter pickGenreRecItemAdapter;
    private List<PickGenreTypeRecItem> pickGenreTypeRecItemList;
    TextView nextTv;

    private StreamingViewModel vm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pick_genres);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        vm = new ViewModelProvider(this, new StreamingViewModelFactory()).get(StreamingViewModel.class);

        backIv = findViewById(R.id.backIv);
        recVPickGenre = findViewById(R.id.recVGenre);
        nextTv = findViewById(R.id.nextTv);

        pickGenreTypeRecItemList = vm.getGenres();

        // Retrieve data passed from SettingsActivity
        String origin = getIntent().getStringExtra("origin");
        // Use the retrieved value as needed
        if (origin != null) {
            Toast.makeText(this, "Received value: " + origin, Toast.LENGTH_SHORT).show();
        }

        // Retrieve data passed from SettingsActivity
        String filters = getIntent().getStringExtra("filters");
        // Use the retrieved value as needed
        if (filters != null) {
            Toast.makeText(this, "Received value: " + filters, Toast.LENGTH_SHORT).show();
        }

        nextTv.setText("Select at Least 1");

        backIv.setOnClickListener(v -> {
            finish();
        });


        layoutManager = new GridLayoutManager(this, 3);
        recVPickGenre.setLayoutManager(layoutManager);

        pickGenreRecItemAdapter = new PickGenreRecItemAdapter(this, pickGenreTypeRecItemList, this::updateNextButtonAppearance);

        recVPickGenre.setHasFixedSize(true);

        recVPickGenre.setAdapter(pickGenreRecItemAdapter);

        nextTv.setOnClickListener(v -> {
            List<String> selectedGenres = getSelectedVideoTitles();
            if (pickGenreRecItemAdapter.isAnyItemSelected()) {
                if ("settings".equals(origin)) {
                    // Return to SettingsActivity
                    Intent intent = new Intent(PickGenresActivity.this, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear all activities above SettingsActivity
                    startActivity(intent);
                } else if ("filters".equals(filters)) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("selectedGenres", new ArrayList<>(selectedGenres));
                    intent.putExtra("navigate_to_filters", true);
                    setResult(RESULT_OK, intent);
                    finish();
//                    // Collect selected video titles
//                    List<String> selectedTitles = getSelectedVideoTitles();
//                    // Navigate back to FiltersFragment
//                    Intent intent = new Intent(PickGenresActivity.this, HomeActivity.class);
//                    intent.putStringArrayListExtra("selectedGenres", new ArrayList<>(selectedTitles));
//                    intent.putExtra("navigate_to_filters", true);
//                    startActivity(intent);
//                    finish(); // Optional: finish the current activity
                } else {
                    // Default behavior: navigate to HomeActivity
                    Intent intent = new Intent(PickGenresActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(PickGenresActivity.this, "Please select a video type before proceeding.", Toast.LENGTH_SHORT).show();
            }
        });

        updateNextButtonAppearance();
    }

    private List<String> getSelectedVideoTitles() {
        List<String> selectedTitles = new ArrayList<>();
        for (int position : pickGenreRecItemAdapter.getSelectedPositions()) {
            selectedTitles.add(pickGenreTypeRecItemList.get(position).getItemTitle());
        }
        return selectedTitles;
    }

    private void updateNextButtonAppearance() {
        if (pickGenreRecItemAdapter.isAnyItemSelected()) {
            nextTv.setBackgroundResource(R.drawable.bluecircle_bg);
            nextTv.setTextColor(getResources().getColor(android.R.color.white));
            nextTv.setText("Done");
        } else {
            nextTv.setBackgroundResource(R.drawable.whitecircle_bg);
            nextTv.setTextColor(getResources().getColor(android.R.color.black));
            nextTv.setText("Select at Least 1");
        }
    }
}