package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.PickGenreTypeRecItem;
import com.example.streamingapp.data.model.PickVideoTypeRecItem;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetVideoTypeListUseCase {
    private final StreamingRepository repository;

    public GetVideoTypeListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<PickVideoTypeRecItem> execute() {
        return repository.getVideoTypeList();
    }
}
