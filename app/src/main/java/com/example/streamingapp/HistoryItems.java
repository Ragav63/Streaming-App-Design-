package com.example.streamingapp;

public class HistoryItems {
    String historyRating, historyTiming;
    int historyImg;

    public HistoryItems(String historyRating, String historyTiming, int historyImg) {
        this.historyRating = historyRating;
        this.historyTiming = historyTiming;
        this.historyImg = historyImg;
    }

    public String getHistoryRating() {
        return historyRating;
    }

    public void setHistoryRating(String historyRating) {
        this.historyRating = historyRating;
    }

    public String getHistoryTiming() {
        return historyTiming;
    }

    public void setHistoryTiming(String historyTiming) {
        this.historyTiming = historyTiming;
    }

    public int getHistoryImg() {
        return historyImg;
    }

    public void setHistoryImg(int historyImg) {
        this.historyImg = historyImg;
    }
}
