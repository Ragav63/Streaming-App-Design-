package com.example.streamingapp.domain.repository;

import com.example.streamingapp.data.model.HistoryItems;

@FunctionalInterface
public interface ContinueWatchingItemClick {
    void onClick(HistoryItems item, ActionType action);
}
