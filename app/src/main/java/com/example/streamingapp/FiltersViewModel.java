package com.example.streamingapp;

import androidx.lifecycle.ViewModel;

import java.util.List;

public class FiltersViewModel extends ViewModel {
    private List<String> selectedCategories;
    private List<String> selectedGenres;
    private List<String> selectedCountries;
    private int fromYear;
    private int toYear;
    private boolean isCategoriesExpanded;
    private boolean isGenresExpanded;
    private boolean isCountriesExpanded;
    private boolean isYearExpanded;

    public List<String> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(List<String> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public List<String> getSelectedGenres() {
        return selectedGenres;
    }

    public void setSelectedGenres(List<String> selectedGenres) {
        this.selectedGenres = selectedGenres;
    }

    public List<String> getSelectedCountries() {
        return selectedCountries;
    }

    public void setSelectedCountries(List<String> selectedCountries) {
        this.selectedCountries = selectedCountries;
    }

    public int getFromYear() {
        return fromYear;
    }

    public void setFromYear(int fromYear) {
        this.fromYear = fromYear;
    }

    public int getToYear() {
        return toYear;
    }

    public void setToYear(int toYear) {
        this.toYear = toYear;
    }

    public boolean isCategoriesExpanded() {
        return isCategoriesExpanded;
    }

    public void setCategoriesExpanded(boolean categoriesExpanded) {
        isCategoriesExpanded = categoriesExpanded;
    }

    public boolean isGenresExpanded() {
        return isGenresExpanded;
    }

    public void setGenresExpanded(boolean genresExpanded) {
        isGenresExpanded = genresExpanded;
    }

    public boolean isCountriesExpanded() {
        return isCountriesExpanded;
    }

    public void setCountriesExpanded(boolean countriesExpanded) {
        isCountriesExpanded = countriesExpanded;
    }

    public boolean isYearExpanded() {
        return isYearExpanded;
    }

    public void setYearExpanded(boolean yearExpanded) {
        isYearExpanded = yearExpanded;
    }
}
