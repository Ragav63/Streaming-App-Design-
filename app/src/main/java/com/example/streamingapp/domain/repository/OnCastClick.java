package com.example.streamingapp.domain.repository;

import com.example.streamingapp.data.model.CastItems;

@FunctionalInterface
public interface OnCastClick {
    void onClick(CastItems castItems);
}

