package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // getters, setters, toString, equals, hashCode
@AllArgsConstructor     // full constructor
@NoArgsConstructor
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
}
