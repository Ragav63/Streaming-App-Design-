package com.example.streamingapp.domain.usecase;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetContinueWatchingListUseCase {
    private final StreamingRepository repository;

    public GetContinueWatchingListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public List<HistoryItems> execute() {
        return repository.getContinueWatchingList()
                .stream()
                .filter(h -> !h.isFullyWatched())
                .filter(h -> h.getWatchedMs() > 0)
                .toList();
    }
}

