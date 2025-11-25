package com.example.streamingapp.domain.repository;

import com.example.streamingapp.data.model.AboutPhotosItems;

@FunctionalInterface
public interface OnPhotoClick {
    void onClick(String imageResource);
}

