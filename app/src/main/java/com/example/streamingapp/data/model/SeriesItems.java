package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SeriesItems implements Parcelable {

    private int id;
    private String title;
    private String poster;
    private String year;
    private String country;
    private String imdb_rating;
    private List<String> genres;
    private List<CrewMember> crew;
    private String plot;
    private List<String> trailers;
    private int noOfSeasons;
    private List<SeasonItems> seasons;
    private String awards;
    private String language;
    private String boxOffice;
    private String production;
    private String website;
    private List<String> images;

    public SeriesItems() {}

    protected SeriesItems(Parcel in) {
        id = in.readInt();
        title = in.readString();
        poster = in.readString();
        year = in.readString();
        country = in.readString();
        imdb_rating = in.readString();
        genres = in.createStringArrayList();
        crew = in.createTypedArrayList(CrewMember.CREATOR);
        plot = in.readString();
        trailers = in.createStringArrayList();
        noOfSeasons = in.readInt();
        seasons = in.createTypedArrayList(SeasonItems.CREATOR);
        awards = in.readString();
        language = in.readString();
        boxOffice = in.readString();
        production = in.readString();
        website = in.readString();
        images = in.createStringArrayList();
    }

    public static final Creator<SeriesItems> CREATOR = new Creator<SeriesItems>() {
        @Override
        public SeriesItems createFromParcel(Parcel in) {
            return new SeriesItems(in);
        }

        @Override
        public SeriesItems[] newArray(int size) {
            return new SeriesItems[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(year);
        dest.writeString(country);
        dest.writeString(imdb_rating);
        dest.writeStringList(genres);
        dest.writeTypedList(crew);
        dest.writeString(plot);
        dest.writeStringList(trailers);
        dest.writeInt(noOfSeasons);
        dest.writeTypedList(seasons);
        dest.writeString(awards);
        dest.writeString(language);
        dest.writeString(boxOffice);
        dest.writeString(production);
        dest.writeString(website);
        dest.writeStringList(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // -------------------- GETTERS & SETTERS -------------------- //

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getPoster() { return poster; }

    public void setPoster(String poster) { this.poster = poster; }

    public String getYear() { return year; }

    public void setYear(String year) { this.year = year; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getImdb_rating() { return imdb_rating; }

    public void setImdb_rating(String imdb_rating) { this.imdb_rating = imdb_rating; }

    public List<String> getGenres() { return genres; }

    public void setGenres(List<String> genres) { this.genres = genres; }

    public List<CrewMember> getCrew() { return crew; }

    public void setCrew(List<CrewMember> crew) { this.crew = crew; }

    public String getPlot() { return plot; }

    public void setPlot(String plot) { this.plot = plot; }

    public List<String> getTrailers() { return trailers; }

    public void setTrailers(List<String> trailers) { this.trailers = trailers; }

    public int getNoOfSeasons() { return noOfSeasons; }

    public void setNoOfSeasons(int noOfSeasons) { this.noOfSeasons = noOfSeasons; }

    public List<SeasonItems> getSeasons() { return seasons; }

    public void setSeasons(List<SeasonItems> seasons) { this.seasons = seasons; }

    public String getAwards() { return awards; }

    public void setAwards(String awards) { this.awards = awards; }

    public String getLanguage() { return language; }

    public void setLanguage(String language) { this.language = language; }

    public String getBoxOffice() { return boxOffice; }

    public void setBoxOffice(String boxOffice) { this.boxOffice = boxOffice; }

    public String getProduction() { return production; }

    public void setProduction(String production) { this.production = production; }

    public String getWebsite() { return website; }

    public void setWebsite(String website) { this.website = website; }

    public List<String> getImages() { return images; }

    public void setImages(List<String> images) { this.images = images; }
}
