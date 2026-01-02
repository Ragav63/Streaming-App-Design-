package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

public class RemoveHistoryUseCase {
    private final StreamingRepository repository;

    public RemoveHistoryUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public boolean execute(HistoryItems item) {
        return repository.removeHistory(item);
    }
}
