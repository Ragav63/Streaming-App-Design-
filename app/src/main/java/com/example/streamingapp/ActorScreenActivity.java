package com.example.streamingapp;

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

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

public class ActorScreenActivity extends AppCompatActivity {
    ImageView backIv, actorScreenIv;
    TextView actorNameTv, actorDescTv;
    TabLayout tabLayout;
    private List<PopularMovieItems> popularMovieItemsList;
    private List<PopularSeriesItems> popularSeriesItemsList;
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
            popularMovieItemsList = intent.getParcelableArrayListExtra("popularMovieItemsList");
        } else if (intent.hasExtra("popularSeriesItemsList")) {
            popularSeriesItemsList = intent.getParcelableArrayListExtra("popularSeriesItemsList");
        }

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (popularMovieItemsList != null) {
            fragmentTransaction.add(R.id.framelayout, FilmographyFragment.newInstanceWithMovies(popularMovieItemsList));
        } else if (popularSeriesItemsList != null) {
            fragmentTransaction.add(R.id.framelayout, FilmographyFragment.newInstanceWithSeries(popularSeriesItemsList));
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
                            if (popularMovieItemsList != null) {
                                fragment = FilmographyFragment.newInstanceWithMovies(popularMovieItemsList);
                            } else if (popularSeriesItemsList != null) {
                                fragment = FilmographyFragment.newInstanceWithSeries(popularSeriesItemsList);
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