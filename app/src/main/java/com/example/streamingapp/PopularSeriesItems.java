package com.example.streamingapp;

import android.os.Parcel;
import android.os.Parcelable;

public class PopularSeriesItems implements Parcelable {
    String imdbRating, title, year, genre, country, seasons, description;
    int image;

    public PopularSeriesItems(String imdbRating, String title, String year, String genre, String country, String seasons, String description, int image) {
        this.imdbRating = imdbRating;
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.country = country;
        this.seasons = seasons;
        this.description = description;
        this.image = image;
    }

    protected PopularSeriesItems(Parcel in) {
        imdbRating = in.readString();
        title = in.readString();
        year = in.readString();
        genre = in.readString();
        country = in.readString();
        seasons = in.readString();
        description = in.readString();
        image = in.readInt();
    }

    public static final Creator<PopularSeriesItems> CREATOR = new Creator<PopularSeriesItems>() {
        @Override
        public PopularSeriesItems createFromParcel(Parcel in) {
            return new PopularSeriesItems(in);
        }

        @Override
        public PopularSeriesItems[] newArray(int size) {
            return new PopularSeriesItems[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imdbRating);
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(genre);
        dest.writeString(country);
        dest.writeString(seasons);
        dest.writeString(description);
        dest.writeInt(image);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getSeasons() {
        return seasons;
    }

    public void setSeasons(String seasons) {
        this.seasons = seasons;
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

    @Override
    public String toString() {
        return "PopularSeriesItems{" +
                "imdbRating='" + imdbRating + '\'' +
                ", title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", genre='" + genre + '\'' +
                ", country='" + country + '\'' +
                ", seasons='" + seasons + '\'' +
                ", description='" + description + '\'' +
                ", image=" + image +
                '}';
    }
}
