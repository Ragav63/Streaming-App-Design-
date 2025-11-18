package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.TvItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetTvListUseCase {
    private final StreamingRepository repository;

    public GetTvListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<TvItems> execute() {
        return repository.getNowOnTvList();
    }
}

