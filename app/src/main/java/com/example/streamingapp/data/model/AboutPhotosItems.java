package com.example.streamingapp.data.model;

import androidx.annotation.NonNull;

public class AboutPhotosItems {
    String aboutImg;

    public AboutPhotosItems(String aboutImg) {
        this.aboutImg = aboutImg;
    }

    public String getAboutImg() {
        return aboutImg;
    }

    public void setAboutImg(String aboutImg) {
        this.aboutImg = aboutImg;
    }

    @Override
    public String toString() {
        return "AboutPhotosItems{" +
                "aboutImg='" + aboutImg + '\'' +
                '}';
    }
}
