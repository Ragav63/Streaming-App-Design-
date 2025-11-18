package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.PickGenreTypeRecItem;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetGenreListUseCase {
    private final StreamingRepository repository;

    public GetGenreListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<PickGenreTypeRecItem> execute() {
        return repository.getGenreList();
    }
}
