package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.CategoryItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetCategoriesListUseCase {
    private final StreamingRepository repository;

    public GetCategoriesListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<CategoryItems> execute() {
        return repository.getCategoriesList();
    }
}

