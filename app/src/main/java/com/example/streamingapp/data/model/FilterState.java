package com.example.streamingapp.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FilterState {

    public enum Mode {
        MOVIES, SERIES, TV, ANIME
    }

    private final Mode mode;
    private final String year;
    private final String country;
    private final String sort;
    private final List<String> genres;
    private final String query;

    private FilterState(
            Mode mode,
            String year,
            String country,
            String sort,
            List<String> genres,
            String query
    ) {
        this.mode = mode;
        this.year = year;
        this.country = country;
        this.sort = sort;
        this.genres = genres;
        this.query = query;
    }

    // -------------------------
    // FACTORY
    // -------------------------
    public static FilterState empty() {
        return new FilterState(
                null,
                null,
                null,
                null,
                new ArrayList<>(),
                ""
        );
    }

    // -------------------------
    // COPY METHODS
    // -------------------------
    public FilterState copy(
            Mode mode,
            String year,
            String country,
            String sort,
            List<String> genres,
            String query
    ) {
        return new FilterState(
                mode,
                year,
                country,
                sort,
                genres,
                query
        );
    }

    public FilterState copyWithQuery(String query) {
        return new FilterState(
                this.mode,
                this.year,
                this.country,
                this.sort,
                new ArrayList<>(this.genres),
                query
        );
    }

    // -------------------------
    // UI HELPERS
    // -------------------------
    public List<String> asChipList() {
        List<String> chips = new ArrayList<>();

        if (mode != null) chips.add(mode.name());
        if (year != null) chips.add(year);
        if (country != null) chips.add(country);
        if (sort != null) chips.add(sort);
        if (genres != null && !genres.isEmpty()) chips.addAll(genres);

        return chips;
    }

    public int count() {
        int count = 0;
        if (mode != null) count++;
        if (year != null) count++;
        if (country != null) count++;
        if (sort != null) count++;
        if (genres != null) count += genres.size();
        return count;
    }

    // -------------------------
    // GETTERS (ViewModel needs these)
    // -------------------------
    public Mode getMode() { return mode; }
    public String getYear() { return year; }
    public String getCountry() { return country; }
    public String getSort() { return sort; }
    public List<String> getGenres() { return genres; }
    public String getQuery() { return query; }
}
