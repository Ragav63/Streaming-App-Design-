package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SeasonItems implements Parcelable {
    public int seasonNumber;
    public String seasonTitle;
    public List<Episode> episodes;

    protected SeasonItems(Parcel in) {
        seasonNumber = in.readInt();
        seasonTitle = in.readString();
        episodes = in.createTypedArrayList(Episode.CREATOR);
    }

    public static final Creator<SeasonItems> CREATOR = new Parcelable.Creator<SeasonItems>() {
        @Override
        public SeasonItems createFromParcel(Parcel in) {
            return new SeasonItems(in);
        }

        @Override
        public SeasonItems[] newArray(int size) {
            return new SeasonItems[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(seasonNumber);
        dest.writeString(seasonTitle);
        dest.writeTypedList(episodes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // -------------------- GETTERS & SETTERS -------------------- //

    public int getSeasonNumber() { return seasonNumber; }

    public void setSeasonNumber(int seasonNumber) { this.seasonNumber = seasonNumber; }

    public String getSeasonTitle() { return seasonTitle; }

    public void setSeasonTitle(String seasonTitle) { this.seasonTitle = seasonTitle; }

    public List<Episode> getEpisodes() { return episodes; }

    public void setEpisodes(List<Episode> episodes) { this.episodes = episodes; }
}
