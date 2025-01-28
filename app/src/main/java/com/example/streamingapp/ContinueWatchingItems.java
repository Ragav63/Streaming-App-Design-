package com.example.streamingapp;

import android.os.Parcel;
import android.os.Parcelable;

public class ContinueWatchingItems implements Parcelable{
    String conWatchTitle, conWatchDesc;
    int conWatchImg;

    public ContinueWatchingItems(String conWatchTitle, String conWatchDesc, int conWatchImg) {
        this.conWatchTitle = conWatchTitle;
        this.conWatchDesc = conWatchDesc;
        this.conWatchImg = conWatchImg;
    }
    protected ContinueWatchingItems(Parcel in) {
        conWatchTitle = in.readString();
        conWatchDesc = in.readString();
        conWatchImg = in.readInt();
    }

    public static final Creator<ContinueWatchingItems> CREATOR = new Creator<ContinueWatchingItems>() {
        @Override
        public ContinueWatchingItems createFromParcel(Parcel in) {
            return new ContinueWatchingItems(in);
        }

        @Override
        public ContinueWatchingItems[] newArray(int size) {
            return new ContinueWatchingItems[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conWatchTitle);
        dest.writeString(conWatchDesc);
        dest.writeInt(conWatchImg);
    }

    public String getConWatchTitle() {
        return conWatchTitle;
    }

    public void setConWatchTitle(String conWatchTitle) {
        this.conWatchTitle = conWatchTitle;
    }

    public String getConWatchDesc() {
        return conWatchDesc;
    }

    public void setConWatchDesc(String conWatchDesc) {
        this.conWatchDesc = conWatchDesc;
    }

    public int getConWatchImg() {
        return conWatchImg;
    }

    public void setConWatchImg(int conWatchImg) {
        this.conWatchImg = conWatchImg;
    }
}
