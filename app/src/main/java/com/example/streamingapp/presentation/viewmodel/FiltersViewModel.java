package com.example.streamingapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.FilterState;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.Programme;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.data.model.TvChannel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FiltersViewModel extends ViewModel {
    private final List<MovieItems> allMovies = new ArrayList<>();
    private final List<SeriesItems> allSeries = new ArrayList<>();
    private final List<CastItems> allCast = new ArrayList<>();
    private final List<TvChannel> allTv = new ArrayList<>();

    private final MutableLiveData<FilterState> filterState =
            new MutableLiveData<>(FilterState.empty());

    private final MutableLiveData<List<MovieItems>> filteredMovies = new MutableLiveData<>();
    private final MutableLiveData<List<SeriesItems>> filteredSeries = new MutableLiveData<>();
    private final MutableLiveData<List<CastItems>> filteredCast = new MutableLiveData<>();
    private final MutableLiveData<List<TvChannel>> filteredTv = new MutableLiveData<>();

    public LiveData<FilterState> getFilterState() { return filterState; }
    public LiveData<List<MovieItems>> getFilteredMovies() { return filteredMovies; }
    public LiveData<List<SeriesItems>> getFilteredSeries() { return filteredSeries; }
    public LiveData<List<CastItems>> getFilteredCast() { return filteredCast; }
    public LiveData<List<TvChannel>> getFilteredTv() { return filteredTv; }

    public void setMovies(List<MovieItems> movies) {
        allMovies.clear();
        allMovies.addAll(movies);
        applyFilters();
    }

    public void setSeries(List<SeriesItems> series) {
        allSeries.clear();
        allSeries.addAll(series);
        applyFilters();
    }

    public void setCast(List<CastItems> cast) {
        allCast.clear();
        allCast.addAll(cast);
        applyFilters();
    }

    public void setTv(List<TvChannel> tv) {
        allTv.clear();
        allTv.addAll(tv);
        applyFilters();
    }

    public void updateFilter(FilterState newState) {
        filterState.setValue(newState);
        applyFilters();
    }

    public void resetFilters() {
        filterState.setValue(FilterState.empty());
        applyFilters();
    }

    private void applyFilters() {
        FilterState state = filterState.getValue();
        if (state == null) state = FilterState.empty();

        filteredMovies.setValue(sortMovies(filterMovies(state), state.getSort()));
        filteredSeries.setValue(sortSeries(filterSeries(state), state.getSort()));
        filteredCast.setValue(filterCast(state));
        filteredTv.setValue(sortTv(filterTv(state), state.getSort()));
    }



    private List<MovieItems> filterMovies(FilterState state) {
        List<MovieItems> result = new ArrayList<>();

        for (MovieItems m : allMovies) {
            if (state.getYear() != null && !state.getYear().equals(m.getYear())) continue;
            if (state.getCountry() != null && !state.getCountry().equalsIgnoreCase(m.getCountry())) continue;

            if (!state.getGenres().isEmpty()) {
                boolean match = false;
                for (String g : state.getGenres()) {
                    if (m.getGenres().contains(g)) {
                        match = true;
                        break;
                    }
                }
                if (!match) continue;
            }

            if (!state.getQuery().isEmpty() &&
                    !m.getTitle().toLowerCase().contains(state.getQuery().toLowerCase())) continue;

            result.add(m);
        }
        return result;
    }

    private List<MovieItems> sortMovies(List<MovieItems> list, String sortBy) {
        if (sortBy == null) return list;

        switch (sortBy) {
            case "Popular":
            case "Rating":
                list.sort((a, b) -> Float.compare(
                        Float.parseFloat(b.getImdb_rating()),
                        Float.parseFloat(a.getImdb_rating())
                ));
                break;
            case "Latest":
                list.sort((a, b) -> b.getYear().compareTo(a.getYear()));
                break;
            case "A → Z":
                list.sort(Comparator.comparing(MovieItems::getTitle, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Z → A":
                list.sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
                break;
        }
        return list;
    }

    private List<SeriesItems> filterSeries(FilterState state) {
        List<SeriesItems> result = new ArrayList<>();

        for (SeriesItems s : allSeries) {
            if (state.getYear() != null && !state.getYear().equals(s.getYear())) continue;
            if (state.getCountry() != null && !state.getCountry().equalsIgnoreCase(s.getCountry())) continue;

            if (!state.getGenres().isEmpty()) {
                boolean match = false;
                for (String g : state.getGenres()) {
                    if (s.getGenres().contains(g)) {
                        match = true;
                        break;
                    }
                }
                if (!match) continue;
            }

            if (!state.getQuery().isEmpty() &&
                    !s.getTitle().toLowerCase().contains(state.getQuery().toLowerCase())) continue;

            result.add(s);
        }
        return result;
    }

    private List<SeriesItems> sortSeries(List<SeriesItems> list, String sortBy) {
        if (sortBy == null) return list;

        switch (sortBy) {
            case "Popular":
            case "Rating":
                list.sort((a, b) -> Float.compare(
                        Float.parseFloat(b.getImdb_rating()),
                        Float.parseFloat(a.getImdb_rating())
                ));
                break;
            case "Latest":
                list.sort((a, b) -> b.getYear().compareTo(a.getYear()));
                break;
            case "A → Z":
                list.sort(Comparator.comparing(SeriesItems::getTitle, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Z → A":
                list.sort((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()));
                break;
        }
        return list;
    }

    private List<CastItems> filterCast(FilterState state) {
        if (state.getQuery().isEmpty()) return new ArrayList<>(allCast);

        List<CastItems> result = new ArrayList<>();
        for (CastItems c : allCast) {
            if (c.getCastName().toLowerCase().contains(state.getQuery().toLowerCase())) {
                result.add(c);
            }
        }
        return result;
    }

    private List<TvChannel> filterTv(FilterState state) {
        List<TvChannel> result = new ArrayList<>();

        for (TvChannel channel : allTv) {

            List<Programme> filteredProgrammes = new ArrayList<>();

            for (Programme p : channel.getProgrammes()) {

                // Country filter
                if (state.getCountry() != null &&
                        !state.getCountry().equalsIgnoreCase(p.getCountry())) {
                    continue;
                }

                // Genre filter
                if (!state.getGenres().isEmpty()) {
                    boolean match = false;
                    for (String g : state.getGenres()) {
                        if (p.getGenres() != null && p.getGenres().contains(g)) {
                            match = true;
                            break;
                        }
                    }
                    if (!match) continue;
                }

                // Search query filter
                if (!state.getQuery().isEmpty() &&
                        !p.getName().toLowerCase().contains(state.getQuery().toLowerCase())) {
                    continue;
                }

                filteredProgrammes.add(p);
            }

            // Only add channel if it has matching programmes
            if (!filteredProgrammes.isEmpty()) {
                result.add(
                        new TvChannel(
                                channel.getChannelLogo(),
                                channel.getChannelName(),
                                filteredProgrammes
                        )
                );
            }
        }

        return result;
    }


    private List<TvChannel> sortTv(List<TvChannel> list, String sortBy) {
        if (sortBy == null) return list;

        for (TvChannel channel : list) {
            List<Programme> programmes = channel.getProgrammes();

            switch (sortBy) {

                case "Popular":
                case "Rating":
                    programmes.sort((a, b) ->
                            parseFloat(b.getImdb_rating()) -
                                    parseFloat(a.getImdb_rating()) > 0 ? 1 : -1
                    );
                    break;

                case "Latest":
                    programmes.sort((a, b) ->
                            safeCompare(b.getYear(), a.getYear())
                    );
                    break;

                case "A → Z":
                    programmes.sort(Comparator.comparing(
                            Programme::getName,
                            String.CASE_INSENSITIVE_ORDER
                    ));
                    break;

                case "Z → A":
                    programmes.sort((a, b) ->
                            b.getName().compareToIgnoreCase(a.getName())
                    );
                    break;
            }
        }

        return list;
    }

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0f;
        }
    }

    private int safeCompare(String a, String b) {
        try {
            return Integer.parseInt(a) - Integer.parseInt(b);
        } catch (Exception e) {
            return 0;
        }
    }

    public LiveData<Boolean> hasAnyResults() {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();

        result.addSource(filteredMovies, v -> result.setValue(hasResults()));
        result.addSource(filteredSeries, v -> result.setValue(hasResults()));
        result.addSource(filteredCast, v -> result.setValue(hasResults()));
        result.addSource(filteredTv, v -> result.setValue(hasResults()));

        // initialize value
        result.setValue(hasResults());

        return result;
    }


    private boolean hasResults() {
        return (filteredMovies.getValue() != null && !filteredMovies.getValue().isEmpty())
                || (filteredSeries.getValue() != null && !filteredSeries.getValue().isEmpty())
                || (filteredCast.getValue() != null && !filteredCast.getValue().isEmpty())
                || (filteredTv.getValue() != null && !filteredTv.getValue().isEmpty());
    }




}
