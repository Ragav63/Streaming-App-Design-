package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetPhotosListUseCase {
    private final StreamingRepository repository;

    public GetPhotosListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<AboutPhotosItems> execute() {
        return repository.getPhotosList();
    }
}

