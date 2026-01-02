package com.example.streamingapp.presentation.viewmodelfactory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.streamingapp.di.AppModule;
import com.example.streamingapp.domain.usecase.GetGenreListUseCase;
import com.example.streamingapp.presentation.viewmodel.StreamingViewModel;

public class StreamingViewModelFactory implements ViewModelProvider.Factory {

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {

        if (modelClass.isAssignableFrom(StreamingViewModel.class)) {

            return (T) new StreamingViewModel(
                    AppModule.provideAvatorUseCase(),
                    AppModule.provideCastUseCase(),
                    AppModule.providePhotosUseCase(),
                    AppModule.provideGenreUseCase(),
                    AppModule.provideMoviesUseCase(),
                    AppModule.provideSeriesUseCase(),
                    AppModule.provideCategoriesUseCase(),
                    AppModule.provideCountriesUseCase(),
                    AppModule.provideDownloadUseCase(),
                    AppModule.provideHistoryUseCase(),
                    AppModule.provideNowOnTvUseCase(),
                    AppModule.provideVideoTypeUseCase(),
                    AppModule.provideSeasonUseCase(),
                    AppModule.provideTrailersUseCase(),
                    AppModule.provideContinueWatchingUseCase(),
                    AppModule.saveHistoryUseCase(),
                    AppModule.removeHistoryUseCase()
            );
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}


