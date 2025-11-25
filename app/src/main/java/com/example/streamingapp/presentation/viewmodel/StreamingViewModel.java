package com.example.streamingapp.presentation.viewmodel;

import androidx.lifecycle.ViewModel;

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
import com.example.streamingapp.di.AppModule;
import com.example.streamingapp.domain.usecase.GetCastListUseCase;
import com.example.streamingapp.domain.usecase.GetCategoriesListUseCase;
import com.example.streamingapp.domain.usecase.GetContinueWatchingListUseCase;
import com.example.streamingapp.domain.usecase.GetCountryListUseCase;
import com.example.streamingapp.domain.usecase.GetDownloadListUseCase;
import com.example.streamingapp.domain.usecase.GetGenreListUseCase;
import com.example.streamingapp.domain.usecase.GetHistoryListUseCase;
import com.example.streamingapp.domain.usecase.GetMovieListUseCase;
import com.example.streamingapp.domain.usecase.GetTvListUseCase;
import com.example.streamingapp.domain.usecase.GetPhotosListUseCase;
import com.example.streamingapp.domain.usecase.GetSeasonListUseCase;
import com.example.streamingapp.domain.usecase.GetSeriesListUseCase;
import com.example.streamingapp.domain.usecase.GetTrailersListUseCase;
import com.example.streamingapp.domain.usecase.GetVideoTypeListUseCase;

import java.util.List;

public class StreamingViewModel extends ViewModel {

    private final GetCastListUseCase castListUseCase;
    private final GetPhotosListUseCase photosListUseCase;

    private final GetGenreListUseCase genreListUseCase;
    private final GetMovieListUseCase movieListUseCase;
    private final GetSeriesListUseCase seriesListUseCase;
    private final GetCategoriesListUseCase categoriesListUseCase;
    private final GetCountryListUseCase countryListUseCase;
    private final GetDownloadListUseCase downloadListUseCase;
    private final GetHistoryListUseCase historyListUseCase;
    private final GetTvListUseCase tvListUseCase;
    private final GetVideoTypeListUseCase videoTypeListUseCase;
    private final GetSeasonListUseCase seasonListUseCase;
    private final GetTrailersListUseCase trailersListUseCase;
    private final GetContinueWatchingListUseCase continueWatchingListUseCase;

    public StreamingViewModel(
            GetCastListUseCase cast,
            GetPhotosListUseCase photos,
            GetGenreListUseCase genres,
            GetMovieListUseCase movies,
            GetSeriesListUseCase series,
            GetCategoriesListUseCase categories,
            GetCountryListUseCase countries,
            GetDownloadListUseCase downloads,
            GetHistoryListUseCase history,
            GetTvListUseCase tv,
            GetVideoTypeListUseCase videoType,
            GetSeasonListUseCase season,
            GetTrailersListUseCase trailers,
            GetContinueWatchingListUseCase continueWatchingList
    ) {
        this.castListUseCase = cast;
        this.photosListUseCase = photos;
        this.genreListUseCase = genres;
        this.movieListUseCase = movies;
        this.seriesListUseCase = series;
        this.categoriesListUseCase = categories;
        this.countryListUseCase = countries;
        this.downloadListUseCase = downloads;
        this.historyListUseCase = history;
        this.tvListUseCase = tv;
        this.videoTypeListUseCase = videoType;
        this.seasonListUseCase = season;
        this.trailersListUseCase = trailers;
        this.continueWatchingListUseCase = continueWatchingList;
    }

    public List<PickGenreTypeRecItem> getGenres(){
        return genreListUseCase.execute();
    }
    public List<CastItems> getCast() {
        return castListUseCase.execute();
    }
    public List<AboutPhotosItems> getPhotos() {
        return photosListUseCase.execute();
    }

    public List<MovieItems> getMovies() { return movieListUseCase.execute();}

    public List<SeriesItems> getSeries() { return seriesListUseCase.execute();}
    public List<CategoryItems> getCategories() {return categoriesListUseCase.execute();}
    public List<CountryItems> getCountries() { return countryListUseCase.execute();}
    public List<DownloadItems> getDownloadItems() { return downloadListUseCase.execute();}
    public List<HistoryItems> getHistoryItems() {return historyListUseCase.execute();}
    public List<TvItems> getNowOnTvItems() { return tvListUseCase.execute();}
    public List<PickVideoTypeRecItem> getVideoTypeItems() { return videoTypeListUseCase.execute();}
    public List<SeasonItems> getSeasonItems() { return seasonListUseCase.execute();}
    public List<TrailerItems> getTrailerItems() { return trailersListUseCase.execute();}
    public List<ContinueWatchingItems> getContinueWatchingItems() {return continueWatchingListUseCase.execute();}
}
