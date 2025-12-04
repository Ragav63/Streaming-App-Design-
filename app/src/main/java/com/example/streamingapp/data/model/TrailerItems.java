package com.example.streamingapp.data.model;

public class TrailerItems {
    String trailerTitle, trailerTiming;
    String trailerUrl;

    public TrailerItems(String trailerTitle, String trailerTiming, String trailerUrl) {
        this.trailerTitle = trailerTitle;
        this.trailerTiming = trailerTiming;
        this.trailerUrl = trailerUrl;
    }

    public String getTrailerTitle() {
        return trailerTitle;
    }

    public void setTrailerTitle(String trailerTitle) {
        this.trailerTitle = trailerTitle;
    }

    public String getTrailerTiming() {
        return trailerTiming;
    }

    public void setTrailerTiming(String trailerTiming) {
        this.trailerTiming = trailerTiming;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }
}
