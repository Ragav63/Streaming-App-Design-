package com.example.streamingapp.presentation.view;

import android.app.Activity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamingapp.presentation.adapter.PickVideoRecItemAdapter;
import com.example.streamingapp.data.model.PickVideoTypeRecItem;
import com.example.streamingapp.R;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;
import com.example.streamingapp.presentation.viewmodelfactory.StreamingViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class PickVideoTypeActivity extends AppCompatActivity {
    ImageView backIv;
    private RecyclerView recVPickVideo;
    private PickVideoRecItemAdapter pickVideoRecItemAdapter;
    private List<PickVideoTypeRecItem> videoTypeRecItemList;
    TextView nextTv;

    private StreamingViewModel vm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pick_video_type);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vm = new ViewModelProvider(this, new StreamingViewModelFactory()).get(StreamingViewModel.class);

        backIv = findViewById(R.id.backIv);
        recVPickVideo = findViewById(R.id.recVPickVideoTypes);
        nextTv = findViewById(R.id.nextTv);

        // Retrieve data passed from SettingsActivity
        String origin = getIntent().getStringExtra("origin");
        // Use the retrieved value as needed
        if (origin != null) {
            Toast.makeText(this, "Received value: " + origin, Toast.LENGTH_SHORT).show();
        }
        String login = getIntent().getStringExtra("login");

        if (login != null) {
            Toast.makeText(this, "Received value: " + login, Toast.LENGTH_SHORT).show();
        }

        nextTv.setText("Select at Least 1");

        backIv.setOnClickListener(v -> finish());

        videoTypeRecItemList = vm.getVideoTypeItems();

        pickVideoRecItemAdapter = new PickVideoRecItemAdapter(this, videoTypeRecItemList, this::updateNextButtonAppearance);

        recVPickVideo.setLayoutManager(new LinearLayoutManager(this));

        recVPickVideo.setHasFixedSize(true);

        recVPickVideo.setAdapter(pickVideoRecItemAdapter);

        nextTv.setOnClickListener(v -> {
            if (pickVideoRecItemAdapter.isAnyItemSelected()) {
                List<String> selectedTitles = getSelectedVideoTitles();

                if ("origin".equals(origin)) {
                    Intent intent = new Intent(PickVideoTypeActivity.this, PickGenresActivity.class);
                    // Pass the extra along to the next activity
                    intent.putExtra("origin", origin);
                    startActivity(intent);
                } else if ("login".equals(login)) {
                    Intent intent = new Intent(PickVideoTypeActivity.this, PickGenresActivity.class);
                    startActivity(intent);
                } else {
//                    // Collect selected video titles
//                    List<String> selectedTitles = getSelectedVideoTitles();
                    // Navigate back to FiltersFragment
                    List<String> selectedTitles1 = getSelectedVideoTitles();
                    Intent resultIntent = new Intent();
                    resultIntent.putStringArrayListExtra("selectedCategories", new ArrayList<>(selectedTitles1));
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish(); // This will close the activity and send the result back
                }
            } else {
                Toast.makeText(PickVideoTypeActivity.this, "Please select a video type before proceeding.", Toast.LENGTH_SHORT).show();
            }
        });

        updateNextButtonAppearance();
    }

    private List<String> getSelectedVideoTitles() {
        List<String> selectedTitles = new ArrayList<>();
        for (int position : pickVideoRecItemAdapter.getSelectedPositions()) {
            selectedTitles.add(videoTypeRecItemList.get(position).getItemTitle());
        }
        return selectedTitles;
    }

    private void updateNextButtonAppearance() {
        if (pickVideoRecItemAdapter.isAnyItemSelected()) {
            nextTv.setBackgroundResource(R.drawable.bluecircle_bg);
            nextTv.setTextColor(getResources().getColor(android.R.color.white));
            nextTv.setText("Next");
        } else {
            nextTv.setBackgroundResource(R.drawable.whitecircle_bg);
            nextTv.setTextColor(getResources().getColor(android.R.color.black));
            nextTv.setText("Select at Least 1");
        }
    }
}