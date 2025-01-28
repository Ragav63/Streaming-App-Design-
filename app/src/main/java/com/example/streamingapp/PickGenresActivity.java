package com.example.streamingapp;

import static java.security.AccessController.getContext;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PickGenresActivity extends AppCompatActivity {

    ImageView backIv;
    private RecyclerView recVPickGenre;
    RecyclerView.LayoutManager layoutManager;
    private PickGenreRecItemAdapter pickGenreRecItemAdapter;
    private List<PickGenreTypeRecItem> pickGenreTypeRecItemList;
    TextView nextTv;

    private List<PickGenreTypeRecItem> generateGenreTypeList() {
        List<PickGenreTypeRecItem> itemList = new ArrayList<>();
        itemList.add(new PickGenreTypeRecItem(R.drawable.spartans1,"Action"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.strangerthings1,"Adventure"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.sports1,"Biography"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.incedible,"Comedy"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.tvshows1,"Crime"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.spartans1,"Documentry"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.strangerthings1,"Drama"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.sports1,"Family"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.incedible,"Fantasy"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.tvshows1,"History"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.spartans1,"Horror"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.strangerthings1,"Mystery"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.sports1,"Romance"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.incedible,"Scifi"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.tvshows1,"Thriller"));

        return itemList;
    }

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

        backIv = findViewById(R.id.backIv);
        recVPickGenre = findViewById(R.id.recVGenre);
        nextTv = findViewById(R.id.nextTv);

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

        pickGenreTypeRecItemList = generateGenreTypeList();

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