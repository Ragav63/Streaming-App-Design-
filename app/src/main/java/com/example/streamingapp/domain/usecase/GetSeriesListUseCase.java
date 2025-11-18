package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetSeriesListUseCase {
    private final StreamingRepository repository;

    public GetSeriesListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<SeriesItems> execute() {
        return repository.getSeriesList();
    }
}

