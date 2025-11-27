package com.example.streamingapp.domain.repository;

import com.example.streamingapp.data.model.DownloadItems;

@FunctionalInterface
public interface OnDownloadClick {
    void onClick(DownloadItems item, ActionType actionType, int position);
}


