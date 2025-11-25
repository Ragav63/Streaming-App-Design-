package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.ContinueWatchingItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetContinueWatchingListUseCase {
    private final StreamingRepository repository;

    public GetContinueWatchingListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<ContinueWatchingItems> execute() {
        return repository.getContinueWatchingList();
    }
}

