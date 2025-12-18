package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetAvatorListUseCase {
    private final StreamingRepository repository;

    public GetAvatorListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<PickItem> execute() {
        return repository.getAvatorList();
    }
}
