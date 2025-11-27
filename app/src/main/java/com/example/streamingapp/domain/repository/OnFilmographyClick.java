package com.example.streamingapp.domain.repository;


@FunctionalInterface
public interface OnFilmographyClick<T> {
    void onClick(T item);
}
