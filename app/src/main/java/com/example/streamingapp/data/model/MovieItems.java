package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieItems implements Parcelable {
    String imdbRating, title, year, genre, country, duration, description;
    int image;

    public MovieItems(String imdbRating, String title, String year, String genre, String country, String duration, String description, int image) {
        this.imdbRating = imdbRating;
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.country = country;
        this.duration = duration;
        this.description = description;
        this.image = image;
    }

    protected MovieItems(Parcel in) {
        imdbRating = in.readString();
        title = in.readString();
        year = in.readString();
        genre = in.readString();
        country = in.readString();
        duration = in.readString();
        description = in.readString();
        image = in.readInt();
    }

    public static final Creator<MovieItems> CREATOR = new Creator<MovieItems>() {
        @Override
        public MovieItems createFromParcel(Parcel in) {
            return new MovieItems(in);
        }

        @Override
        public MovieItems[] newArray(int size) {
            return new MovieItems[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imdbRating);
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(genre);
        dest.writeString(country);
        dest.writeString(duration);
        dest.writeString(description);
        dest.writeInt(image);
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
