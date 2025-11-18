package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetHistoryListUseCase {
    private final StreamingRepository repository;

    public GetHistoryListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<HistoryItems> execute() {
        return repository.getHistoryList();
    }
}

