package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetGenreListUseCase {
    private final StreamingRepository repository;

    public GetGenreListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<PickItem> execute() {
        return repository.getGenreList();
    }
}
