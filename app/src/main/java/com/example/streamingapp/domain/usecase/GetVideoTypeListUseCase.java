package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.PickItem;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetVideoTypeListUseCase {
    private final StreamingRepository repository;

    public GetVideoTypeListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<PickItem> execute() {
        return repository.getVideoTypeList();
    }
}
