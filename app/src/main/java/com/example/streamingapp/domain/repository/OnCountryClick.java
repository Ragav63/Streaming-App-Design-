package com.example.streamingapp.domain.repository;

import java.util.List;

// -------------------- Functional Interface --------------------
@FunctionalInterface
public interface OnCountryClick {
    void onClick(List<String> selectedCountries);
}
