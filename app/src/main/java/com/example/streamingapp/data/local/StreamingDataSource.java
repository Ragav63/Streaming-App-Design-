package com.example.streamingapp.data.local;

import android.content.Context;
import android.util.Log;

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
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.data.model.Programme;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.data.model.TrailerItems;
import com.example.streamingapp.data.model.TvChannel;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StreamingDataSource {

    private final Context context;

    // Constructor with Context
    public StreamingDataSource(Context context) {
        this.context = context.getApplicationContext();
    }

    public List<PickItem> getAvatorList() {
        List<PickItem> itemList = new ArrayList<>();
        itemList.add(new PickItem(R.drawable.spartans1,"Action"));
        itemList.add(new PickItem(R.drawable.strangerthings1,"Adventure"));
        itemList.add(new PickItem(R.drawable.sports1,"Biography"));
        itemList.add(new PickItem(R.drawable.incedible,"Comedy"));
        itemList.add(new PickItem(R.drawable.tvshows1,"Crime"));

        return itemList;
    }

    private static final int[] GENRE_IMAGES = {
            R.drawable.spartans1,
            R.drawable.strangerthings1,
            R.drawable.sports1,
            R.drawable.incedible,
            R.drawable.tvshows1
    };


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

    private int getImageForGenre(String genre) {
        int index = Math.abs(genre.hashCode()) % GENRE_IMAGES.length;
        return GENRE_IMAGES[index];
    }

    public List<PickItem> getGenreList() {

        Set<String> genreSet = new HashSet<>();

        List<MovieItems> movies = getMoviesList();
        List<SeriesItems> series = getSeriesList();
        List<TvChannel> channels = getTvList();

        // Extract from movies
        for (MovieItems movie : movies) {
            if (movie.getGenres() == null) continue;

            for (String genre : movie.getGenres()) {
                if (genre != null && !genre.trim().isEmpty()) {
                    genreSet.add(genre.trim());
                }
            }
        }

        // Extract from series
        for (SeriesItems s : series) {
            if (s.getGenres() == null) continue;

            for (String genre : s.getGenres()) {
                if (genre != null && !genre.trim().isEmpty()) {
                    genreSet.add(genre.trim());
                }
            }
        }

        for (TvChannel s : channels) {

            for (Programme p : s.getProgrammes()) {
                if (p.getGenres() == null) continue;
                for (String genre : p.getGenres()) {
                    if (genre != null && !genre.trim().isEmpty()) {
                        genreSet.add(genre.trim());
                    }
                }
            }
        }

        // Convert + sort
        List<String> genreList = new ArrayList<>(genreSet);
        Collections.sort(genreList, String.CASE_INSENSITIVE_ORDER);

        // Build PickItem list with stable images
        List<PickItem> pickItems = new ArrayList<>();
        for (String genre : genreList) {
            pickItems.add(
                    new PickItem(
                            getImageForGenre(genre),
                            genre
                    )
            );
        }

        return pickItems;
    }



    public List<CategoryItems> getCategories() {
        List<CategoryItems> itemList = new ArrayList<>();
        itemList.add(new CategoryItems("TV CHANNELS", R.drawable.strthings));
        itemList.add(new CategoryItems("MOVIES", R.drawable.spartans));
        itemList.add(new CategoryItems("CARTOONS", R.drawable.anime));
        itemList.add(new CategoryItems("SPORT", R.drawable.sports));
        itemList.add(new CategoryItems("SERIES", R.drawable.strthings));

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

    public List<HistoryItems> getHistoryItemList() {
        List<HistoryItems> list = new ArrayList<>();

        list.add(new HistoryItems(1, "The Shawshank Redemption", "View 10.06.2024"));
        list.add(new HistoryItems(2, "The Godfather", "View 06.06.2024"));
        list.add(new HistoryItems(3, "The Godfather: Part II", "View 10.05.2024"));

        return list;
    }




    public List<PickItem> getVideoTypeList() {
        List<PickItem> itemList = new ArrayList<>();
        itemList.add(new PickItem(R.drawable.spartans1,"Movies"));
        itemList.add(new PickItem(R.drawable.strangerthings1,"Series"));
        itemList.add(new PickItem(R.drawable.sports1,"Sports"));
        itemList.add(new PickItem(R.drawable.incedible,"Cartoons"));
        itemList.add(new PickItem(R.drawable.tvshows1,"Tv Shows"));

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

    public List<TvChannel> getTvList() {
        List<TvChannel> itemList = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open("tvItems.json");
            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type tvListType = new TypeToken<List<TvChannel>>(){}.getType();
            itemList = gson.fromJson(reader, tvListType);

            reader.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            itemList = new ArrayList<>();
        }
        return itemList;
    }

    public List<ContinueWatchingItems> getContinueWatchingList() {
        List<ContinueWatchingItems> itemList = new ArrayList<>();
        itemList.add(new ContinueWatchingItems("Venom 3", "",R.drawable.venom3));
        itemList.add(new ContinueWatchingItems("Stranger Things - Season 1","Episode 1 Winter is Coming",R.drawable.strangerthings1));

        return itemList;
    }


}
