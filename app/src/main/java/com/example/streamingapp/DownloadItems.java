package com.example.streamingapp;

public class DownloadItems {
    String downloadTitle, downloadEpTitle, downloadDuration;
    int downloadImg;

    public DownloadItems(String downloadTitle, String downloadEpTitle, String downloadDuration, int downloadImg) {
        this.downloadTitle = downloadTitle;
        this.downloadEpTitle = downloadEpTitle;
        this.downloadDuration = downloadDuration;
        this.downloadImg = downloadImg;
    }

    public String getDownloadTitle() {
        return downloadTitle;
    }

    public void setDownloadTitle(String downloadTitle) {
        this.downloadTitle = downloadTitle;
    }

    public String getDownloadEpTitle() {
        return downloadEpTitle;
    }

    public void setDownloadEpTitle(String downloadEpTitle) {
        this.downloadEpTitle = downloadEpTitle;
    }

    public String getDownloadDuration() {
        return downloadDuration;
    }

    public void setDownloadDuration(String downloadDuration) {
        this.downloadDuration = downloadDuration;
    }

    public int getDownloadImg() {
        return downloadImg;
    }

    public void setDownloadImg(int downloadImg) {
        this.downloadImg = downloadImg;
    }
}
