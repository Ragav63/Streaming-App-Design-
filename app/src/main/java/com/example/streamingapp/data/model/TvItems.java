package com.example.streamingapp.data.model;

public class TvItems {
    String tvLogoName, tvName, currentProgramName, currentProgramTiming;
    private boolean isFavorite;
    int img;

    public TvItems(String tvLogoName, String tvName, String currentProgramName, String currentProgramTiming, int img) {
        this.tvLogoName = tvLogoName;
        this.tvName = tvName;
        this.currentProgramName = currentProgramName;
        this.currentProgramTiming = currentProgramTiming;
        this.img = img;
    }

    public String getCurrentProgramTiming() {
        return currentProgramTiming;
    }

    public void setCurrentProgramTiming(String currentProgramTiming) {
        this.currentProgramTiming = currentProgramTiming;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
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
