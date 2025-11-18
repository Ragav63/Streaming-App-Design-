package com.example.streamingapp.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.streamingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView=findViewById(R.id.bottomview);

        // Handle intent for navigation to FiltersFragment
        handleIntent(getIntent());

//        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment=null;
                int id = item.getItemId();
                if (id == R.id.home) {
                    Toast.makeText(HomeActivity.this, "Home Fragment", Toast.LENGTH_SHORT).show();
                    selectedFragment = new HomeFragment();
                } else if (id == R.id.search) {
                    Toast.makeText(HomeActivity.this, "Search Fragment", Toast.LENGTH_SHORT).show();
                    selectedFragment = new SearchFragment();
                } else if (id == R.id.tv) {
                    Toast.makeText(HomeActivity.this, "Tv Fragment", Toast.LENGTH_SHORT).show();
                    selectedFragment = new TvFragment();
                } else if (id == R.id.favourite) {
                    Toast.makeText(HomeActivity.this, "Favourite Fragment", Toast.LENGTH_SHORT).show();
                    selectedFragment = new FavouriteFragment();
                } else if (id == R.id.account) {
                    Toast.makeText(HomeActivity.this, "Account Fragment", Toast.LENGTH_SHORT).show();
                    selectedFragment = new AccountFragment();
                }

                if (selectedFragment != null) {
                    FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, selectedFragment);
                    transaction.commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.bottomview);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("navigate_to_filters", false)) {

            FiltersFragment filtersFragment = new FiltersFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("selectedCategories", intent.getStringArrayListExtra("selectedCategories"));
            bundle.putStringArrayList("selectedGenres", intent.getStringArrayListExtra("selectedGenres"));
            filtersFragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, filtersFragment);
            transaction.commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        }
    }
}