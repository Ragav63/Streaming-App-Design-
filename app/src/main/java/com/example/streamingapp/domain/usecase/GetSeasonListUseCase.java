package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetSeasonListUseCase {
    private final StreamingRepository repository;

    public GetSeasonListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<SeasonItems> execute() {
        return repository.getSeasonItemList();
    }
}

