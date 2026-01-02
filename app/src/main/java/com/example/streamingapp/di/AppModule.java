package com.example.streamingapp.di;

import android.app.Application;
import android.content.Context;

import com.example.streamingapp.data.local.StreamingDataSource;
import com.example.streamingapp.data.repository.StreamingRepositoryImpl;
import com.example.streamingapp.domain.usecase.GetAvatorListUseCase;
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
import com.example.streamingapp.domain.usecase.RemoveHistoryUseCase;
import com.example.streamingapp.domain.usecase.SaveHistoryUseCase;

public class AppModule {
    private static Application application;

    public static void initialize(Application app) {
        application = app;
    }

    private static Context provideContext() {
        return application.getApplicationContext();
    }

    private static StreamingDataSource provideLocalDataSource() {
        return new StreamingDataSource(provideContext());
    }

    public static StreamingRepositoryImpl provideStreamingRepository() {
        return new StreamingRepositoryImpl(provideLocalDataSource());
    }

    public static GetAvatorListUseCase provideAvatorUseCase() {
        return new GetAvatorListUseCase(provideStreamingRepository());
    }

    public static GetMovieListUseCase provideMoviesUseCase() {
        return new GetMovieListUseCase(provideStreamingRepository());
    }

    public static GetSeriesListUseCase provideSeriesUseCase() {
        return new GetSeriesListUseCase(provideStreamingRepository());
    }

    public static GetGenreListUseCase provideGenreUseCase() {
        return new GetGenreListUseCase(provideStreamingRepository());
    }

    public static GetCastListUseCase provideCastUseCase() {
        return new GetCastListUseCase(provideStreamingRepository());
    }

    public static GetPhotosListUseCase providePhotosUseCase() {
        return new GetPhotosListUseCase(provideStreamingRepository());
    }

    public static GetCategoriesListUseCase provideCategoriesUseCase() {
        return new GetCategoriesListUseCase(provideStreamingRepository());
    }

    public static GetCountryListUseCase provideCountriesUseCase() {
        return new GetCountryListUseCase(provideStreamingRepository());
    }

    public static GetDownloadListUseCase provideDownloadUseCase(){
        return new GetDownloadListUseCase(provideStreamingRepository());
    }

    public static GetHistoryListUseCase provideHistoryUseCase() {
        return new GetHistoryListUseCase(provideStreamingRepository());
    }

    public static SaveHistoryUseCase saveHistoryUseCase() {
        return new SaveHistoryUseCase(provideStreamingRepository());
    }

    public static RemoveHistoryUseCase removeHistoryUseCase() {
        return new RemoveHistoryUseCase(provideStreamingRepository());
    }

    public static GetTvListUseCase provideNowOnTvUseCase() {
        return new GetTvListUseCase(provideStreamingRepository());
    }

    public static GetVideoTypeListUseCase provideVideoTypeUseCase() {
        return new GetVideoTypeListUseCase(provideStreamingRepository());
    }

    public static GetSeasonListUseCase provideSeasonUseCase() {
        return new GetSeasonListUseCase(provideStreamingRepository());
    }

    public static GetTrailersListUseCase provideTrailersUseCase() {
        return new GetTrailersListUseCase(provideStreamingRepository());
    }

    public static GetContinueWatchingListUseCase provideContinueWatchingUseCase() {
        return new GetContinueWatchingListUseCase(provideStreamingRepository());
    }
}
