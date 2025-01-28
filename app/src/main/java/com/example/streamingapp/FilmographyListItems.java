package com.example.streamingapp;

import android.os.Parcel;
import android.os.Parcelable;

public class FilmographyListItems implements Parcelable{
    String filmRating, filmTitle;
    int filmImg;

    public FilmographyListItems(String filmRating, String filmTitle, int filmImg) {
        this.filmRating = filmRating;
        this.filmTitle = filmTitle;
        this.filmImg = filmImg;
    }

    protected FilmographyListItems(Parcel in) {
        filmRating = in.readString();
        filmTitle = in.readString();
        filmImg = in.readInt();
    }

    public static final Parcelable.Creator<FilmographyListItems> CREATOR = new Parcelable.Creator<FilmographyListItems>() {
        @Override
        public FilmographyListItems createFromParcel(Parcel in) {
            return new FilmographyListItems(in);
        }

        @Override
        public FilmographyListItems[] newArray(int size) {
            return new FilmographyListItems[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filmRating);
        dest.writeString(filmTitle);
        dest.writeInt(filmImg);
    }

    public String getFilmRating() {
        return filmRating;
    }

    public void setFilmRating(String filmRating) {
        this.filmRating = filmRating;
    }

    public String getFilmTitle() {
        return filmTitle;
    }

    public void setFilmTitle(String filmTitle) {
        this.filmTitle = filmTitle;
    }

    public int getFilmImg() {
        return filmImg;
    }

    public void setFilmImg(int filmImg) {
        this.filmImg = filmImg;
    }
}
