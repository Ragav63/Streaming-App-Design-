package com.example.streamingapp.domain.repository;

import com.example.streamingapp.data.model.ContinueWatchingItems;
import com.example.streamingapp.presentation.adapter.ContinueWatchingFragmentItemAdapter;

@FunctionalInterface
public interface ContinueWatchingItemClick {
    void onClick(ContinueWatchingItems item, ActionType action);
}
