package com.example.streamingapp;

public class HomeStartItems {
    String homeStartRating, homeStartTitle;
    int homeStartImg;

    public HomeStartItems(String homeStartRating, String homeStartTitle, int homeStartImg) {
        this.homeStartRating = homeStartRating;
        this.homeStartTitle = homeStartTitle;
        this.homeStartImg = homeStartImg;
    }

    public String getHomeStartRating() {
        return homeStartRating;
    }

    public void setHomeStartRating(String homeStartRating) {
        this.homeStartRating = homeStartRating;
    }

    public String getHomeStartTitle() {
        return homeStartTitle;
    }

    public void setHomeStartTitle(String homeStartTitle) {
        this.homeStartTitle = homeStartTitle;
    }

    public int getHomeStartImg() {
        return homeStartImg;
    }

    public void setHomeStartImg(int homeStartImg) {
        this.homeStartImg = homeStartImg;
    }
}
