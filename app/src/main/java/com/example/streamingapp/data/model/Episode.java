package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Episode implements Parcelable {
    public int episodeNumber;
    public String episodeTitle;
    public String runtime;
    public String url;
    public String rating;
    public List<String> images;

    protected Episode(Parcel in) {
        episodeNumber = in.readInt();
        episodeTitle = in.readString();
        runtime = in.readString();
        url = in.readString();
        rating = in.readString();
        images = in.createStringArrayList();
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(episodeNumber);
        dest.writeString(episodeTitle);
        dest.writeString(runtime);
        dest.writeString(url);
        dest.writeString(rating);
        dest.writeStringList(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // -------------------- GETTERS & SETTERS -------------------- //

    public int getEpisodeNumber() { return episodeNumber; }

    public void setEpisodeNumber(int episodeNumber) { this.episodeNumber = episodeNumber; }

    public String getEpisodeTitle() { return episodeTitle; }

    public void setEpisodeTitle(String episodeTitle) { this.episodeTitle = episodeTitle; }

    public String getRuntime() { return runtime; }

    public void setRuntime(String runtime) { this.runtime = runtime; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public String getRating() { return rating; }

    public void setRating(String rating) { this.rating = rating; }

    public List<String> getImages() { return images; }

    public void setImages(List<String> images) { this.images = images; }
}
