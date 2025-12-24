package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // getters, setters, toString, equals, hashCode
@AllArgsConstructor     // full constructor
@NoArgsConstructor      // empty constructor (often needed)
public class CastItems implements Parcelable {

    private String castName;
    private String castDesignation;
    private String castAbout;
    private List<String> filmographies;
    private List<String> biographyDetails;
    private List<String> personImages;

    // -------- Parcelable --------

    protected CastItems(Parcel in) {
        castName = in.readString();
        castDesignation = in.readString();
        castAbout = in.readString();
        filmographies = in.createStringArrayList();
        biographyDetails = in.createStringArrayList();
        personImages = in.createStringArrayList();
    }

    public CastItems(String castName,
                     String castDesignation,
                     String castAbout,
                     List<String> personImages) {
        this.castName = castName;
        this.castDesignation = castDesignation;
        this.castAbout = castAbout;
        this.personImages = personImages;
    }


    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(castName);
        parcel.writeString(castDesignation);
        parcel.writeString(castAbout);
        parcel.writeStringList(filmographies);
        parcel.writeStringList(biographyDetails);
        parcel.writeStringList(personImages);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CastItems> CREATOR = new Creator<CastItems>() {
        @Override
        public CastItems createFromParcel(Parcel in) {
            return new CastItems(in);
        }

        @Override
        public CastItems[] newArray(int size) {
            return new CastItems[size];
        }
    };
}
