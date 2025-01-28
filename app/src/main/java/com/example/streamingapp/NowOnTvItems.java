package com.example.streamingapp;

public class NowOnTvItems {
    String nowOnTvChannelName, nowOnTvTitle, nowOnTvTiming;
    int nowOnTvImg;

    public NowOnTvItems(String nowOnTvChannelName, String nowOnTvTitle, String nowOnTvTiming, int nowOnTvImg) {
        this.nowOnTvChannelName = nowOnTvChannelName;
        this.nowOnTvTitle = nowOnTvTitle;
        this.nowOnTvTiming = nowOnTvTiming;
        this.nowOnTvImg = nowOnTvImg;
    }

    public String getNowOnTvChannelName() {
        return nowOnTvChannelName;
    }

    public void setNowOnTvChannelName(String nowOnTvChannelName) {
        this.nowOnTvChannelName = nowOnTvChannelName;
    }

    public String getNowOnTvTitle() {
        return nowOnTvTitle;
    }

    public void setNowOnTvTitle(String nowOnTvTitle) {
        this.nowOnTvTitle = nowOnTvTitle;
    }

    public String getNowOnTvTiming() {
        return nowOnTvTiming;
    }

    public void setNowOnTvTiming(String nowOnTvTiming) {
        this.nowOnTvTiming = nowOnTvTiming;
    }

    public int getNowOnTvImg() {
        return nowOnTvImg;
    }

    public void setNowOnTvImg(int nowOnTvImg) {
        this.nowOnTvImg = nowOnTvImg;
    }
}
