package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

public class MovieScreenActivity extends AppCompatActivity {
    TabLayout tabLayout;
    FrameLayout frameLayout;
    Fragment fragment=null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TrailersFragment trailersFragment = new TrailersFragment();
    ImageView backIv, movieScreenIv, downloadIv, favIv, shareIv;
    TextView ratingTv, titleTv, watchNowTv, originTv, genreTv, yearTv, descriptionTv, durationTv;
    private List<MovieItems> movieItemsList;
    private boolean isDownloaded = false;
    private boolean isFavourite = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backIv = findViewById(R.id.backIv);
        movieScreenIv = findViewById(R.id.movieScreenIv);
        ratingTv = findViewById(R.id.ratingTv);
        titleTv = findViewById(R.id.titleTv);
        watchNowTv = findViewById(R.id.watchNowTv);
        downloadIv = findViewById(R.id.downloadIv);
        favIv = findViewById(R.id.favIv);
        shareIv = findViewById(R.id.shareIv);
        tabLayout = findViewById(R.id.tab_layout);
        frameLayout = findViewById(R.id.framelayout);
        originTv = findViewById(R.id.originTv);
        genreTv = findViewById(R.id.genreTv);
        yearTv = findViewById(R.id.yearTv);
        durationTv = findViewById(R.id.durationTv);
        descriptionTv = findViewById(R.id.descriptionTv);

        backIv.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();

        int imageResource = intent.getIntExtra("imageResource", -1);
        String rating = intent.getStringExtra("rating");
        String title = intent.getStringExtra("title");
        String year = intent.getStringExtra("year");
        String country = intent.getStringExtra("country");
        String genre = intent.getStringExtra("genre");
        String duration = intent.getStringExtra("duration");
        String description = intent.getStringExtra("description");


        if (imageResource != -1) {
            movieScreenIv.setImageResource(imageResource);
        } else {
            Toast.makeText(this, "Movie Screen Img Not Found", Toast.LENGTH_SHORT).show();
        }

        if (rating != null) {
            ratingTv.setText(rating);
        } else {
            Toast.makeText(this, "Rating Not Found", Toast.LENGTH_SHORT).show();
        }

        if (title != null) {
            titleTv.setText(title);
        } else {
            Toast.makeText(this, "Title Not Found", Toast.LENGTH_SHORT).show();
        }

        if (year != null) {
            yearTv.setText(year);
        } else {
            Toast.makeText(this, "Year Not Found", Toast.LENGTH_SHORT).show();
        }

        if (country != null) {
            originTv.setText(country);
        } else {
            Toast.makeText(this, "Country Not Found", Toast.LENGTH_SHORT).show();
        }

        if (genre != null) {
            genreTv.setText(genre);
        } else {
            Toast.makeText(this, "Genre Not Found", Toast.LENGTH_SHORT).show();
        }

        if (duration != null) {
            durationTv.setText("PG-13 - "+duration);
        } else {
            Toast.makeText(this, "Duration Not Found", Toast.LENGTH_SHORT).show();
        }

        if (description != null) {
            descriptionTv.setText(description);
        } else {
            Toast.makeText(this, "Description Not Found", Toast.LENGTH_SHORT).show();
        }

        movieItemsList = intent.getParcelableArrayListExtra("popularMovieItemsList");

        if (movieItemsList == null) {
            // Handle case where list is not passed correctly
            Toast.makeText(this, "Error: No movie items found", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no list is found
            return;
        }

        if (movieItemsList.isEmpty()) {
            // Handle case where list is empty
            Toast.makeText(this, "Error: Empty movie items list", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if list is empty
            return;
        }

        if (movieItemsList == null) {
            Log.d("MovieScreenActivity", "popularMovieItemsList is null");
        } else {
            Log.d("MovieScreenActivity", "popularMovieItemsList size: " + movieItemsList.size());
        }

        int defaultTintColor = ContextCompat.getColor(this, R.color.white);
        int selectedTintColor = ContextCompat.getColor(this, R.color.bluemain);

        downloadIv.setOnClickListener(v -> {
            if (isDownloaded) {
                Toast.makeText(this, "Already added to Download", Toast.LENGTH_SHORT).show();
            } else {
                downloadIv.setColorFilter(selectedTintColor, PorterDuff.Mode.SRC_IN);
                Toast.makeText(this, "Added to Download", Toast.LENGTH_SHORT).show();
                openDownloadDialog(movieScreenIv, titleTv.getText().toString());
                isDownloaded = true;
            }
        });

        favIv.setOnClickListener(v -> {
            if (isFavourite) {
                favIv.setColorFilter(defaultTintColor, PorterDuff.Mode.SRC_IN);
                Toast.makeText(this, "Removed from Favourite", Toast.LENGTH_SHORT).show();
                isFavourite = false;
            } else {
                favIv.setColorFilter(selectedTintColor, PorterDuff.Mode.SRC_IN);
                Toast.makeText(this, "Added to Favourite", Toast.LENGTH_SHORT).show();
                isFavourite = true;
            }
        });

        shareIv.setOnClickListener(v -> {
            shareIv.setColorFilter(selectedTintColor, PorterDuff.Mode.SRC_IN);
            Toast.makeText(this, "Starting to share", Toast.LENGTH_SHORT).show();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this movie!");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            shareIv.postDelayed(() -> shareIv.setColorFilter(defaultTintColor, PorterDuff.Mode.SRC_IN), 1000);
        });

        watchNowTv.setOnClickListener(v -> {
            Toast.makeText(this, "Added to Download", Toast.LENGTH_SHORT).show();
            openDownloadDialog(movieScreenIv, titleTv.getText().toString());
        });

//        watchNowTv.setOnClickListener(v -> {
//            Intent intent1 = new Intent(this, SeriesPlayerScreenActivity.class);
//            intent1.putExtra("imageResource", imageResource);
//            intent1.putExtra("title", title);
//            intent1.putExtra("rating", rating);
//            intent1.putParcelableArrayListExtra("popularMovieItemsList", (ArrayList<? extends Parcelable>) popularMovieItemsList);
//            startActivity(intent1);
//            finish();
//        });

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.framelayout, trailersFragment);
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
                            fragment = trailersFragment;
                            break;
                        case 1:
                            fragment = MoreLikeThisFragment.newInstanceWithMovies(movieItemsList);
                            break;
                        case 2:
                            fragment = AboutFragment.newInstanceWithMovies(movieItemsList);
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

    private void openDownloadDialog(ImageView movieImageView, String movieTitle) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_download);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView downloadIv = dialog.findViewById(R.id.downloadIv);
        TextView downloadTv = dialog.findViewById(R.id.downloadTv);
        TextView downloadTitle, downloadQualityVal, downloadAudio1, downloadAudio2, downloadSubtitleOff, downloadSubtitle1, downloadSubtitle2;
        SeekBar qualitySbar = dialog.findViewById(R.id.qualitySeekbar);

        downloadTitle = dialog.findViewById(R.id.downloadTitleTv);
        downloadQualityVal = dialog.findViewById(R.id.qualityValTv);
        downloadAudio1 = dialog.findViewById(R.id.audio1Tv);
        downloadAudio2 = dialog.findViewById(R.id.audio2Tv);
        downloadSubtitleOff = dialog.findViewById(R.id.subtitleOffTv);
        downloadSubtitle1 = dialog.findViewById(R.id.subtitle1Tv);
        downloadSubtitle2 = dialog.findViewById(R.id.subtitle2Tv);

        downloadIv.setImageDrawable(movieImageView.getDrawable());
        downloadTitle.setText(movieTitle);

        // Set default audio and subtitle selections
        downloadAudio1.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
        downloadSubtitleOff.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);

        // Configure SeekBar for quality selection
        qualitySbar.setMax(100);
        qualitySbar.setProgress(25); // Default to 25%

        qualitySbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 25) {
                    downloadQualityVal.setText("Low (360p)");
                } else if (progress < 50) {
                    downloadQualityVal.setText("Medium (480p)");
                } else if (progress < 75) {
                    downloadQualityVal.setText("High (720p)");
                } else {
                    downloadQualityVal.setText("HD (1080p)");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Set listeners for audio options
        downloadAudio1.setOnClickListener(v -> {
            downloadAudio1.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadAudio2.setBackgroundResource(R.drawable.dimcircle_bg);
        });

        downloadAudio2.setOnClickListener(v -> {
            downloadAudio2.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadAudio1.setBackgroundResource(R.drawable.dimcircle_bg);
        });

        downloadSubtitleOff.setOnClickListener(v -> {
            downloadSubtitleOff.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadSubtitle1.setBackgroundResource(R.drawable.dimcircle_bg);
            downloadSubtitle2.setBackgroundResource(R.drawable.dimcircle_bg);
        });
        // Set listeners for subtitle options
        downloadSubtitle1.setOnClickListener(v -> {
            downloadSubtitle1.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadSubtitle2.setBackgroundResource(R.drawable.dimcircle_bg);
            downloadSubtitleOff.setBackgroundResource(R.drawable.dimcircle_bg);
        });

        downloadSubtitle2.setOnClickListener(v -> {
            downloadSubtitle2.setBackgroundResource(R.drawable.lgtransparentbluestroke_bg);
            downloadSubtitle1.setBackgroundResource(R.drawable.dimcircle_bg);
            downloadSubtitleOff.setBackgroundResource(R.drawable.dimcircle_bg);
        });


        downloadTv.setOnClickListener(v -> {
            Toast.makeText(this, "Started to Download", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

//    private void resetIcons() {
//        int defaultTintColor = ContextCompat.getColor(this, R.color.white);
//        downloadIv.setColorFilter(defaultTintColor, PorterDuff.Mode.SRC_IN);
//        favIv.setColorFilter(defaultTintColor, PorterDuff.Mode.SRC_IN);
//        shareIv.setColorFilter(defaultTintColor, PorterDuff.Mode.SRC_IN);
//        // Reset other icons as needed
//    }

//    private void resetIconsDelayed() {
//        int defaultTintColor = ContextCompat.getColor(this, R.color.white);
//        downloadIv.postDelayed(() -> downloadIv.setColorFilter(defaultTintColor, PorterDuff.Mode.SRC_IN), 1000);
//        favIv.postDelayed(() -> favIv.setColorFilter(defaultTintColor, PorterDuff.Mode.SRC_IN), 1000);
//        shareIv.postDelayed(() -> shareIv.setColorFilter(defaultTintColor, PorterDuff.Mode.SRC_IN), 1000);
//        // Reset other icons as needed
//    }
}