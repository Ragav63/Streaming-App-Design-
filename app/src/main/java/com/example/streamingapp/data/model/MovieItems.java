package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MovieItems implements Parcelable {
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
    private int runtime;
    private String awards;
    private String language;
    private String boxOffice;
    private String production;
    private String website;
    private List<String> images;
    private String url;

    // Constructor
    public MovieItems(int id, String title, String poster, String year, String country,
                      String imdb_rating, List<String> genres, List<CrewMember> crew,
                      String plot, List<String> trailers, int runtime, String awards,
                      String language, String boxOffice, String production, String website,
                      List<String> images, String url) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.year = year;
        this.country = country;
        this.imdb_rating = imdb_rating;
        this.genres = genres;
        this.crew = crew;
        this.plot = plot;
        this.trailers = trailers;
        this.runtime = runtime;
        this.awards = awards;
        this.language = language;
        this.boxOffice = boxOffice;
        this.production = production;
        this.website = website;
        this.images = images;
        this.url = url;
    }

    protected MovieItems(Parcel in) {
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
        runtime = in.readInt();
        awards = in.readString();
        language = in.readString();
        boxOffice = in.readString();
        production = in.readString();
        website = in.readString();
        images = in.createStringArrayList();
        url = in.readString();
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
        dest.writeInt(runtime);
        dest.writeString(awards);
        dest.writeString(language);
        dest.writeString(boxOffice);
        dest.writeString(production);
        dest.writeString(website);
        dest.writeStringList(images);
        dest.writeString(url);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImdbRating() {
        return imdb_rating;
    }

    public void setImdbRating(String imdb_rating) {
        this.imdb_rating = imdb_rating;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<CrewMember> getCrew() {
        return crew;
    }

    public void setCrew(List<CrewMember> crew) {
        this.crew = crew;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public List<String> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<String> trailers) {
        this.trailers = trailers;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(String boxOffice) {
        this.boxOffice = boxOffice;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Helper method to get genres as comma separated string
    public String getGenresAsString() {
        if (genres == null || genres.isEmpty()) {
            return "";
        }
        return String.join(", ", genres);
    }

    // Helper method to get duration in "Xh Ym" format
    public String getFormattedDuration() {
        int hours = runtime / 60;
        int minutes = runtime % 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
}
