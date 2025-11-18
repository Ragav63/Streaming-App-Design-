package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.TrailerItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetTrailersListUseCase {
    private final StreamingRepository repository;

    public GetTrailersListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<TrailerItems> execute() {
        return repository.getTrailersList();
    }
}

