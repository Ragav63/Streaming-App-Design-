package com.example.streamingapp.data.local;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.CategoryItems;
import com.example.streamingapp.data.model.ContinueWatchingItems;
import com.example.streamingapp.data.model.CountryItems;
import com.example.streamingapp.data.model.CrewMember;
import com.example.streamingapp.data.model.DownloadItems;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.PickGenreTypeRecItem;
import com.example.streamingapp.data.model.PickVideoTypeRecItem;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.data.model.TrailerItems;
import com.example.streamingapp.data.model.TvItems;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StreamingDataSource {

    private final Context context;

    // Constructor with Context
    public StreamingDataSource(Context context) {
        this.context = context.getApplicationContext();
    }

    public List<PickGenreTypeRecItem> getGenreList() {
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

    public List<CastItems> getCastList() {
        List<CastItems> castList = new ArrayList<>();

        // Combine movie cast
        for (MovieItems movie : getMoviesList()) {
            if (movie.getCrew() != null) {
                for (CrewMember c : movie.getCrew()) {
                    castList.add(new CastItems(
                            c.getName(),
                            c.getDesignation(),
                            c.getImages()
                    ));
                }
            }
        }

        // Combine series cast
        for (SeriesItems series : getSeriesList()) {
            if (series.getCrew() != null) {
                for (CrewMember c : series.getCrew()) {
                    castList.add(new CastItems(
                            c.getName(),
                            c.getDesignation(),
                            c.getImages()
                    ));
                }
            }
        }
        Log.d("DataSource","cast values "+castList);

        return castList;
    }


    public List<AboutPhotosItems> getPhotosList() {
        List<AboutPhotosItems> photoList = new ArrayList<>();

        // Movie images
        for (MovieItems movie : getMoviesList()) {
            if (movie.getImages() != null) {
                for (String url : movie.getImages()) {
                    photoList.add(new AboutPhotosItems(url));
                }
            }
        }

        // Series images
        for (SeriesItems series : getSeriesList()) {
            if (series.getImages() != null) {
                for (String url : series.getImages()) {
                    photoList.add(new AboutPhotosItems(url));
                }
            }
        }
        Log.d("DataSource","photos values "+photoList);
        return photoList;
    }


    public List<MovieItems> getMoviesList() {
        List<MovieItems> itemList = new ArrayList<>();

        try {
            InputStream inputStream = context.getAssets().open("movies.json");
            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type movieListType = new TypeToken<List<MovieItems>>(){}.getType();
            itemList = gson.fromJson(reader, movieListType);

            reader.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            itemList = new ArrayList<>();
        }

        return itemList;
    }


    public List<SeriesItems> getSeriesList() {
        List<SeriesItems> itemList = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open("series.json");
            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type seriesListType = new TypeToken<List<SeriesItems>>(){}.getType();
            itemList = gson.fromJson(reader, seriesListType);

            reader.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            itemList = new ArrayList<>();
        }
        return itemList;
    }

    public List<CategoryItems> getCategories() {
        List<CategoryItems> itemList = new ArrayList<>();
        itemList.add(new CategoryItems("TV CHANNELS", R.drawable.strthings));
        itemList.add(new CategoryItems("MOVIES", R.drawable.spartans));
        itemList.add(new CategoryItems("CARTOONS", R.drawable.anime));
        itemList.add(new CategoryItems("SCI-FI", R.drawable.scifi));
        itemList.add(new CategoryItems("SPORT", R.drawable.sports));
        itemList.add(new CategoryItems("SERIES", R.drawable.strthings));
        itemList.add(new CategoryItems("TV SHOWS", R.drawable.tvshows));

        return itemList;
    }

    public List<CountryItems> getCountriesList() {
        List<CountryItems> itemsList = new ArrayList<>();
        itemsList.add(new CountryItems("All"));
        itemsList.add(new CountryItems("India"));
        itemsList.add(new CountryItems("USA"));
        itemsList.add(new CountryItems("Korea"));
        itemsList.add(new CountryItems("China"));

        return itemsList;
    }

    public List<DownloadItems> getDownloadItemsList(){
        List<DownloadItems> itemsList = new ArrayList<>();
        itemsList.add(new DownloadItems("Avatar: The Way of Water", "","1h 34min / 7.2 gb / 720p", R.drawable.avatarthewayofwatervertical));
        itemsList.add(new DownloadItems("Game of Thrones, Season 1", "Episode 1, Winter is Coming","1h 34min / 7.2 gb / 720p", R.drawable.got));

        return itemsList;
    }

    public List<HistoryItems> getHistoryItemList(){
        List<HistoryItems> itemsList = new ArrayList<>();
        itemsList.add(new HistoryItems("7.3","View 10.06.2024", R.drawable.venom3verticalnew));
        itemsList.add(new HistoryItems("7.0","View 06.06.2024", R.drawable.avatarthelastairbenderverticalnew));
        itemsList.add(new HistoryItems("8.0","View 10.05.2024", R.drawable.avengersverticalnew));
        itemsList.add(new HistoryItems("6.5","View 01.05.2024", R.drawable.avatarthewayofwaterverticalnew1));
        itemsList.add(new HistoryItems("7.2","View 28.04.2024", R.drawable.kalkiverticalnew));
        itemsList.add(new HistoryItems("7.3","View 25.04.2024", R.drawable.captainamericaverticalnew));

        return itemsList;
    }




    public List<PickVideoTypeRecItem> getVideoTypeList() {
        List<PickVideoTypeRecItem> itemList = new ArrayList<>();
        itemList.add(new PickVideoTypeRecItem(R.drawable.spartans1,"Movies"));
        itemList.add(new PickVideoTypeRecItem(R.drawable.strangerthings1,"Series"));
        itemList.add(new PickVideoTypeRecItem(R.drawable.sports1,"Sports"));
        itemList.add(new PickVideoTypeRecItem(R.drawable.incedible,"Cartoons"));
        itemList.add(new PickVideoTypeRecItem(R.drawable.tvshows1,"Tv Shows"));

        return itemList;
    }

    public List<SeasonItems> getSeasonItemsList() {
        List<SeasonItems> itemList = new ArrayList<>();
        return itemList;
    }

    public List<TrailerItems> getTrailersList() {
        List<TrailerItems> itemList = new ArrayList<>();
       return itemList;
    }

    public List<TvItems> getTvList() {
        List itemsList = new ArrayList<>();
        itemsList.add(new TvItems("espn","ESPN", "NBA Playoff: Lakers vs Denver, Game 2","11.35-12.50",R.drawable.spart));
        itemsList.add(new TvItems("abc","ABC", "Euphoria - Season 1, Episode 1","12.35-01.50",R.drawable.strthings));
        itemsList.add(new TvItems("fox","FOX", "Shogun - Season 1, Episode 3","11.35-12.50",R.drawable.scifi1));
        itemsList.add(new TvItems("abc","abc", "High School Musical","11.35-12.50",R.drawable.scifi1));
        itemsList.add(new TvItems("SS","ss", "CSK vs MI","11.35-12.50",R.drawable.scifi1));
        itemsList.add(new TvItems("SS","ss", "KKR vs Delhi","11.35-12.50",R.drawable.scifi1));

        return itemsList;
    }

    public List<ContinueWatchingItems> getContinueWatchingList() {
        List<ContinueWatchingItems> itemList = new ArrayList<>();
        itemList.add(new ContinueWatchingItems("Venom 3", "",R.drawable.venom3));
        itemList.add(new ContinueWatchingItems("Stranger Things - Season 1","Episode 1 Winter is Coming",R.drawable.strangerthings1));

        return itemList;
    }


}
