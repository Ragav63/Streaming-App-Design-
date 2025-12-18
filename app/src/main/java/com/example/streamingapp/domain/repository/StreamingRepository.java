package com.example.streamingapp.domain.repository;

import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.CategoryItems;
import com.example.streamingapp.data.model.ContinueWatchingItems;
import com.example.streamingapp.data.model.CountryItems;
import com.example.streamingapp.data.model.DownloadItems;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.data.model.TrailerItems;
import com.example.streamingapp.data.model.TvItems;

import java.util.List;

public interface StreamingRepository {
    List<PickItem> getAvatorList();

    List<PickItem> getGenreList();
    List<CastItems> getCastList();
    List<AboutPhotosItems> getPhotosList();
    List<MovieItems> getMoviesList();
    List<SeriesItems> getSeriesList();
    List<CategoryItems> getCategoriesList();
    List<CountryItems> getCountriesList();
    List<DownloadItems> getDownloadList();
    List<HistoryItems> getHistoryList();

    List<TvItems> getNowOnTvList();

    List<PickItem> getVideoTypeList();

    List<SeasonItems> getSeasonItemList();

    List<TrailerItems> getTrailersList();

    List<ContinueWatchingItems> getContinueWatchingList();
}
