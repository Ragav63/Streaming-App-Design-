package com.example.streamingapp;

public class TvNameItems {
    String tvLogoName, tvName, currentProgramName;
    private boolean isFavorite;

    public TvNameItems(String tvLogoName, String tvName, String currentProgramName) {
        this.tvLogoName = tvLogoName;
        this.tvName = tvName;
        this.currentProgramName = currentProgramName;
    }

    public String getTvLogoName() {
        return tvLogoName;
    }

    public void setTvLogoName(String tvLogoName) {
        this.tvLogoName = tvLogoName;
    }

    public String getTvName() {
        return tvName;
    }

    public void setTvName(String tvName) {
        this.tvName = tvName;
    }

    public String getCurrentProgramName() {
        return currentProgramName;
    }

    public void setCurrentProgramName(String currentProgramName) {
        this.currentProgramName = currentProgramName;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
