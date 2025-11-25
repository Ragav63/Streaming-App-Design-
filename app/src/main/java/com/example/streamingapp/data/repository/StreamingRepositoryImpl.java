package com.example.streamingapp.data.repository;

import com.example.streamingapp.data.local.StreamingDataSource;
import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.CategoryItems;
import com.example.streamingapp.data.model.ContinueWatchingItems;
import com.example.streamingapp.data.model.CountryItems;
import com.example.streamingapp.data.model.DownloadItems;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.PickGenreTypeRecItem;
import com.example.streamingapp.data.model.PickVideoTypeRecItem;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.data.model.TrailerItems;
import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class StreamingRepositoryImpl implements StreamingRepository {
    public final StreamingDataSource dataSource;

    public StreamingRepositoryImpl(StreamingDataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public List<PickGenreTypeRecItem> getGenreList(){
        return dataSource.getGenreList();
    }

    @Override
    public List<CastItems> getCastList() {
        return dataSource.getCastList();
    }

    @Override
    public List<AboutPhotosItems> getPhotosList() {
        return dataSource.getPhotosList();
    }
    @Override
    public List<MovieItems> getMoviesList() {
        return dataSource.getMoviesList();
    }
    @Override
    public List<SeriesItems> getSeriesList() {
        return dataSource.getSeriesList();
    }

    @Override
    public List<CategoryItems> getCategoriesList() {
        return dataSource.getCategories();
    }

    @Override
    public List<CountryItems> getCountriesList() {
        return dataSource.getCountriesList();
    }

    public List<DownloadItems> getDownloadList() {
        return dataSource.getDownloadItemsList();
    }

    @Override
    public List<HistoryItems> getHistoryList() {
        return dataSource.getHistoryItemList();
    }
    @Override
    public List<TvItems> getNowOnTvList() {
        return dataSource.getTvList();
    }
    @Override
    public List<PickVideoTypeRecItem> getVideoTypeList() {
        return dataSource.getVideoTypeList();
    }
    @Override
    public List<SeasonItems> getSeasonItemList() {
        return dataSource.getSeasonItemsList();
    }
    @Override
    public List<TrailerItems> getTrailersList() {
        return dataSource.getTrailersList();
    }
    @Override
    public List<ContinueWatchingItems> getContinueWatchingList() { return dataSource.getContinueWatchingList();}
}
