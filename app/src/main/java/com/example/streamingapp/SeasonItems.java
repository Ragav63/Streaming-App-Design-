package com.example.streamingapp;

public class SeasonItems {
    String seasonEpTitle, seasonEpTiming;
    int seasonEpImg;
    private boolean isDownloading; // New field to track state

    public SeasonItems(int seasonEpImg, String seasonEpTitle, String seasonEpTiming) {
        this.seasonEpImg = seasonEpImg;
        this.seasonEpTitle = seasonEpTitle;
        this.seasonEpTiming = seasonEpTiming;
        this.isDownloading = false; // Initialize to false
    }

    public String getSeasonEpTitle() {
        return seasonEpTitle;
    }

    public void setSeasonEpTitle(String seasonEpTitle) {
        this.seasonEpTitle = seasonEpTitle;
    }

    public String getSeasonEpTiming() {
        return seasonEpTiming;
    }

    public void setSeasonEpTiming(String seasonEpTiming) {
        this.seasonEpTiming = seasonEpTiming;
    }

    public int getSeasonEpImg() {
        return seasonEpImg;
    }

    public void setSeasonEpImg(int seasonEpImg) {
        this.seasonEpImg = seasonEpImg;
    }

    public boolean isDownloading() { return isDownloading; }
    public void setDownloading(boolean downloading) { isDownloading = downloading; }
}
