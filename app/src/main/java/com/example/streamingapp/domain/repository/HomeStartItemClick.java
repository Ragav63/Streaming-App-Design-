package com.example.streamingapp.domain.repository;

import com.example.streamingapp.data.model.MovieItems;

@FunctionalInterface
public interface HomeStartItemClick {
    void onAction(MovieItems item, int position, ActionType actionType, boolean isFavorite);

    enum ActionType {
        ITEM_CLICK,
        WATCH_NOW_CLICK,
        FAVORITE_CLICK
    }
}
