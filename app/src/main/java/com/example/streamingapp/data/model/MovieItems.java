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
    private List<TrailerItems> trailers;
    private int runtime;
    private String awards;
    private String language;
    private String boxOffice;
    private String production;
    private String website;
    private List<String> images;
    private String url;


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
        trailers = in.createTypedArrayList(TrailerItems.CREATOR);
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
        dest.writeTypedList(trailers);
        dest.writeInt(runtime);
        dest.writeString(awards);
        dest.writeString(language);
        dest.writeString(boxOffice);
        dest.writeString(production);
        dest.writeString(website);
        dest.writeStringList(images);
        dest.writeString(url);
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
