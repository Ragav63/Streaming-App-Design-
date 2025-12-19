package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.TvChannel;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetTvListUseCase {
    private final StreamingRepository repository;

    public GetTvListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<TvChannel> execute() {
        return repository.getNowOnTvList();
    }
}

