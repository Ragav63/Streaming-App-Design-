package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetMovieListUseCase {
    private final StreamingRepository repository;

    public GetMovieListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<MovieItems> execute() {
        return repository.getMoviesList();
    }
}

