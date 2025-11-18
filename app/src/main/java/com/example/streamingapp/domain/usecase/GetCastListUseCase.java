package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetCastListUseCase {
    private final StreamingRepository repository;

    public GetCastListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<CastItems> execute() {
        return repository.getCastList();
    }
}

