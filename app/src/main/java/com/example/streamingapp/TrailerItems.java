package com.example.streamingapp;

public class TrailerItems {
    String trailerTitle, trailerTiming;
    int trailerImg;

    public TrailerItems(String trailerTitle, String trailerTiming, int trailerImg) {
        this.trailerTitle = trailerTitle;
        this.trailerTiming = trailerTiming;
        this.trailerImg = trailerImg;
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

    public int getTrailerImg() {
        return trailerImg;
    }

    public void setTrailerImg(int trailerImg) {
        this.trailerImg = trailerImg;
    }
}
