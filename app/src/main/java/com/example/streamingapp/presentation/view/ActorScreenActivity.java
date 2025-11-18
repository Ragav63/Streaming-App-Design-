package com.example.streamingapp.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

public class ActorScreenActivity extends AppCompatActivity {
    ImageView backIv, actorScreenIv;
    TextView actorNameTv, actorDescTv;
    TabLayout tabLayout;
    private List<MovieItems> movieItemsList;
    private List<SeriesItems> seriesItemsList;
    FrameLayout frameLayout;
    Fragment fragment=null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actor_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backIv = findViewById(R.id.backIv);
        actorScreenIv = findViewById(R.id.actorScreenIv);
        actorNameTv = findViewById(R.id.actorNameTv);
        actorDescTv = findViewById(R.id.actorDescTv);
        tabLayout = findViewById(R.id.tab_layout);
        frameLayout = findViewById(R.id.framelayout);

        backIv.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        int imageResource = intent.getIntExtra("imageResource", -1);
        String actorName = intent.getStringExtra("actorName");
        String actorDesc = intent.getStringExtra("actorDesc");

        if (imageResource != -1) {
            actorScreenIv.setImageResource(imageResource);
        } else {
            Toast.makeText(this, "Actor Screen Img Not Found", Toast.LENGTH_SHORT).show();
        }

        if (actorName != null) {
            actorNameTv.setText(actorName);
        } else {
            Toast.makeText(this, "Actor Name Not Found", Toast.LENGTH_SHORT).show();
        }

        if (actorDesc != null) {
            actorDescTv.setText(actorDesc);
        } else {
            Toast.makeText(this, "Actor Description Not Found", Toast.LENGTH_SHORT).show();
        }

        // Retrieve lists based on type
        if (intent.hasExtra("popularMovieItemsList")) {
            movieItemsList = intent.getParcelableArrayListExtra("popularMovieItemsList");
        } else if (intent.hasExtra("popularSeriesItemsList")) {
            seriesItemsList = intent.getParcelableArrayListExtra("popularSeriesItemsList");
        }

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (movieItemsList != null) {
            fragmentTransaction.add(R.id.framelayout, FilmographyFragment.newInstanceWithMovies(movieItemsList));
        } else if (seriesItemsList != null) {
            fragmentTransaction.add(R.id.framelayout, FilmographyFragment.newInstanceWithSeries(seriesItemsList));
        }

        fragmentTransaction.commit();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Objects.requireNonNull(tabLayout.getTabAt(tab.getPosition()));

                String tag = "android:switcher:" + R.id.framelayout + ":" + position;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    switch (position) {
                        case 0:
                            if (movieItemsList != null) {
                                fragment = FilmographyFragment.newInstanceWithMovies(movieItemsList);
                            } else if (seriesItemsList != null) {
                                fragment = FilmographyFragment.newInstanceWithSeries(seriesItemsList);
                            }
                            break;
                        case 1:
                            fragment = new BiographyFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("actorName", actorNameTv.getText().toString());
                            fragment.setArguments(bundle);
                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragment, tag).commit();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabLayout.getTabAt(tab.getPosition()).setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.getTabAt(0);

    }
}