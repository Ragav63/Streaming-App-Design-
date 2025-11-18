package com.example.streamingapp.domain.usecase;

import com.example.streamingapp.data.model.CountryItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.domain.repository.StreamingRepository;

import java.util.List;

public class GetCountryListUseCase {
    private final StreamingRepository repository;

    public GetCountryListUseCase(StreamingRepository repository) {
        this.repository = repository;
    }

    public List<CountryItems> execute() {
        return repository.getCountriesList();
    }
}

