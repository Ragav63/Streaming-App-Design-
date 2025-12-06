package com.example.streamingapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    private final MutableLiveData<List<PickGenreTypeRecItem>> _genresLiveData = new MutableLiveData<>();
    public LiveData<List<PickGenreTypeRecItem>> getGenresLiveData() {
        return _genresLiveData;
    }
    public void loadGenres() {
        // Execute on background thread
        new Thread(() -> {
            List<PickGenreTypeRecItem> genres = genreListUseCase.execute();
            _genresLiveData.postValue(genres);
        }).start();
    }

    private final MutableLiveData<List<PickVideoTypeRecItem>> _videoTypeLiveData = new MutableLiveData<>();

    public LiveData<List<PickVideoTypeRecItem>> getVideoTypeLiveData() {
        return _videoTypeLiveData;
    }
    public void loadVideoTypeItems() {
        // Execute on background thread
        new Thread(() -> {
            List<PickVideoTypeRecItem> videoTypes = videoTypeListUseCase.execute();
            _videoTypeLiveData.postValue(videoTypes);
        }).start();
    }

    private final MutableLiveData<List<CastItems>> _castLiveData = new MutableLiveData<>();
    public LiveData<List<CastItems>> getCastLiveData() { return _castLiveData; }

    public void loadCast() {
        new Thread(() -> {
            List<CastItems> cast = castListUseCase.execute();
            _castLiveData.postValue(cast);
        }).start();
    }
    private final MutableLiveData<List<AboutPhotosItems>> _photoLiveData = new MutableLiveData<>();
    public LiveData<List<AboutPhotosItems>> getPhotoLiveData() { return _photoLiveData; }

    public void loadPhotos() {
        new Thread(() -> {
            List<AboutPhotosItems> photos = photosListUseCase.execute();
            _photoLiveData.postValue(photos);
        }).start();
    }

    private final MutableLiveData<List<MovieItems>> _movieLiveData = new MutableLiveData<>();
    public LiveData<List<MovieItems>> getMovieLiveData() { return _movieLiveData; }

    public void loadMovies() {
        new Thread(() -> {
            List<MovieItems> movies = movieListUseCase.execute();
            _movieLiveData.postValue(movies);
        }).start();
    }

    private final MutableLiveData<List<SeriesItems>> _seriesLiveData = new MutableLiveData<>();
    public LiveData<List<SeriesItems>> getSeriesLiveData() { return _seriesLiveData; }

    public void loadSeries() {
        new Thread(() -> {
            List<SeriesItems> series = seriesListUseCase.execute();
            _seriesLiveData.postValue(series);
        }).start();
    }
    private final MutableLiveData<List<CategoryItems>> _categoryLiveData = new MutableLiveData<>();
    public LiveData<List<CategoryItems>> getCategoryLiveData() { return _categoryLiveData; }

    public void loadCategories() {
        new Thread(() -> {
            List<CategoryItems> cat = categoriesListUseCase.execute();
            _categoryLiveData.postValue(cat);
        }).start();
    }
    private final MutableLiveData<List<CountryItems>> _countryLiveData = new MutableLiveData<>();
    public LiveData<List<CountryItems>> getCountryLiveData() { return _countryLiveData; }

    public void loadCountries() {
        new Thread(() -> {
            List<CountryItems> countries = countryListUseCase.execute();
            _countryLiveData.postValue(countries);
        }).start();
    }
    private final MutableLiveData<List<DownloadItems>> _downloadLiveData = new MutableLiveData<>();
    public LiveData<List<DownloadItems>> getDownloadLiveData() { return _downloadLiveData; }

    public void loadDownloads() {
        new Thread(() -> {
            List<DownloadItems> downloads = downloadListUseCase.execute();
            _downloadLiveData.postValue(downloads);
        }).start();
    }
    private final MutableLiveData<List<HistoryItems>> _historyLiveData = new MutableLiveData<>();
    public LiveData<List<HistoryItems>> getHistoryLiveData() { return _historyLiveData; }

    public void loadHistory() {
        new Thread(() -> {
            List<HistoryItems> history = historyListUseCase.execute();
            _historyLiveData.postValue(history);
        }).start();
    }
    private final MutableLiveData<List<TvItems>> _tvLiveData = new MutableLiveData<>();
    public LiveData<List<TvItems>> getTvLiveData() { return _tvLiveData; }

    public void loadTvItems() {
        new Thread(() -> {
            List<TvItems> tv = tvListUseCase.execute();
            _tvLiveData.postValue(tv);
        }).start();
    }
    private final MutableLiveData<List<SeasonItems>> _seasonLiveData = new MutableLiveData<>();
    public LiveData<List<SeasonItems>> getSeasonLiveData() { return _seasonLiveData; }

    public void loadSeasons() {
        new Thread(() -> {
            List<SeasonItems> seasons = seasonListUseCase.execute();
            _seasonLiveData.postValue(seasons);
        }).start();
    }
    private final MutableLiveData<List<TrailerItems>> _trailersLiveData = new MutableLiveData<>();
    public LiveData<List<TrailerItems>> getTrailersLiveData() { return _trailersLiveData; }

    public void loadTrailers() {
        new Thread(() -> {
            List<TrailerItems> trailers = trailersListUseCase.execute();
            _trailersLiveData.postValue(trailers);
        }).start();
    }
    private final MutableLiveData<List<ContinueWatchingItems>> _continueWatchingLiveData = new MutableLiveData<>();
    public LiveData<List<ContinueWatchingItems>> getContinueWatchingLiveData() { return _continueWatchingLiveData; }

    public void loadContinueWatching() {
        new Thread(() -> {
            List<ContinueWatchingItems> list = continueWatchingListUseCase.execute();
            _continueWatchingLiveData.postValue(list);
        }).start();
    }
}
