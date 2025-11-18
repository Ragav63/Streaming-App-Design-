package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.DownloadItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetDownloadListUseCase {
    private final StreamingRepository repository;

    public GetDownloadListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<DownloadItems> execute() {
        return repository.getDownloadList();
    }
}

