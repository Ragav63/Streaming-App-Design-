package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

public class SaveHistoryUseCase {
    private final StreamingRepository repository;

    public SaveHistoryUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public void execute(HistoryItems item) {
        repository.saveHistory(item);
    }
}
