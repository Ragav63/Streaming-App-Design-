package com.example.streamingapp.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SeriesScreenActivity extends AppCompatActivity {
    private static final String TAG = "SeriesScreenActivity";
    private static final int DEFAULT_TINT_COLOR = R.color.white;
    private static final int SELECTED_TINT_COLOR = R.color.bluemain;
    TabLayout tabLayout;
    FrameLayout frameLayout;
    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
//    SeasonFragment seasonFragment = new SeasonFragment();
    ImageView backIv, seriesScreenIv, downloadIv, favIv, shareIv;
    TextView ratingTv, titleTv, watchNowTv, yearTv, genreTv, originTv, seasonsTv, descriptionTv;
    private List<SeriesItems> seriesItemsList;
    private boolean isDownloaded = false;
    private boolean isFavourite = false;
    int imageResource;
    String rating, title, year, genre, country, seasons, description;
    SeasonFragment seasonFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_series_screen);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        backIv = findViewById(R.id.backIv);
        seriesScreenIv = findViewById(R.id.seriesScreenIv);
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
        seasonsTv = findViewById(R.id.seasonsTv);
        descriptionTv = findViewById(R.id.descriptionTv);

        backIv.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        imageResource = intent.getIntExtra("imageResource", -1);
        rating = intent.getStringExtra("rating");
        title = intent.getStringExtra("title");
        year = intent.getStringExtra("year");
        country = intent.getStringExtra("country");
        genre = intent.getStringExtra("genre");
        seasons = intent.getStringExtra("seasons");
        description = intent.getStringExtra("description");
        seriesItemsList = intent.getParcelableArrayListExtra("popularSeriesItemsList");

        // Log the received data
        Log.d(TAG, "Received imageResource: " + imageResource);
        Log.d(TAG, "Received rating: " + rating);

        if (imageResource != -1) {
            seriesScreenIv.setImageResource(imageResource);
            titleTv.setText(title);
            ratingTv.setText(rating);
            titleTv.setText(title);
            yearTv.setText(year);
            originTv.setText(country);
            genreTv.setText(genre);
            seasonsTv.setText("PG-13 "+seasons+" Seasons");
            descriptionTv.setText(description);
        } else {
            Toast.makeText(this, "Data Not Found", Toast.LENGTH_SHORT).show();
        }

//        seriesScreenIv.setScaleType(ImageView.ScaleType.MATRIX);
//        Matrix matrix = new Matrix();
//        matrix.postTranslate(-20, -20); // Adjust these values to position the image correctly
//        seriesScreenIv.setImageMatrix(matrix);

        if (seriesItemsList == null || seriesItemsList.isEmpty()) {
            Log.d(TAG, "popularSeriesItemsList is null or empty");
            Toast.makeText(this, "Error: No series items found", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if list is empty or null
            return;
        }

        // Log the contents of the list if needed
        if (seriesItemsList != null) {
            for (SeriesItems item : seriesItemsList) {
                Log.d("SeriesPlayerScreenActivity", "Item from list: " + item.toString());
            }
        }

        // Create the SeasonFragment with data
        seasonFragment = SeasonFragment.newInstance(seriesItemsList, false,false);

        watchNowTv.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, SeriesPlayerScreenActivity.class);
            intent1.putExtra("imageResource", imageResource);
            intent1.putExtra("title", title);
            intent1.putExtra("rating", rating);
            intent1.putParcelableArrayListExtra("popularSeriesItemsList", (ArrayList<? extends Parcelable>) seriesItemsList);
            startActivity(intent1);
            finish();
        });


        downloadIv.setOnClickListener(v -> {
            if (isDownloaded) {
                Toast.makeText(this, "Already added to Download", Toast.LENGTH_SHORT).show();
            } else {
                downloadIv.setColorFilter(ContextCompat.getColor(this, SELECTED_TINT_COLOR), PorterDuff.Mode.SRC_IN);
                Toast.makeText(this, "Added to Download", Toast.LENGTH_SHORT).show();
                openDownloadDialog(seriesScreenIv, titleTv.getText().toString());
                isDownloaded = true;
            }
        });

        favIv.setOnClickListener(v -> {
            if (isFavourite) {
                favIv.setColorFilter(ContextCompat.getColor(this, DEFAULT_TINT_COLOR), PorterDuff.Mode.SRC_IN);
                Toast.makeText(this, "Removed from Favourite", Toast.LENGTH_SHORT).show();
                isFavourite = false;
            } else {
                favIv.setColorFilter(ContextCompat.getColor(this, SELECTED_TINT_COLOR), PorterDuff.Mode.SRC_IN);
                Toast.makeText(this, "Added to Favourite", Toast.LENGTH_SHORT).show();
                isFavourite = true;
            }
        });

        shareIv.setOnClickListener(v -> {
            shareIv.setColorFilter(ContextCompat.getColor(this, SELECTED_TINT_COLOR), PorterDuff.Mode.SRC_IN);
            Toast.makeText(this, "Starting to share", Toast.LENGTH_SHORT).show();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this movie!");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            shareIv.postDelayed(() -> shareIv.setColorFilter(ContextCompat.getColor(this, DEFAULT_TINT_COLOR), PorterDuff.Mode.SRC_IN), 1000);
        });

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.framelayout, seasonFragment);
        fragmentTransaction.commit();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();

                Fragment selectedFragment = getFragmentForPosition(position);
                if (selectedFragment != null) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.framelayout, seasonFragment, "fragment:" + position);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }


//                String tag = "android:switcher:" + R.id.framelayout + ":" + position;
//                fragment = getSupportFragmentManager().findFragmentByTag(tag);
//
//                if (fragment == null) {
//                    fragment = seasonFragment; // Use existing fragment with data
//                }
//
//                if (fragment != null) {
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.framelayout, fragment, tag)
//                            .commit();
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });
        tabLayout.getTabAt(0); // Select the first tab

    }

    private Fragment getFragmentForPosition(int position) {
        seasonFragment = SeasonFragment.newInstance(seriesItemsList, false,false);
        if (position == 0) {
            return seasonFragment;
        }
        return seasonFragment;
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
            Intent intent = new Intent(this, SeriesPlayerScreenActivity.class);
            intent.putExtra("imageResource", imageResource);
            intent.putExtra("title", title);
            intent.putExtra("rating", rating);
            intent.putParcelableArrayListExtra("popularSeriesItemsList", (ArrayList<? extends Parcelable>) seriesItemsList);
            startActivity(intent);
            finish();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}