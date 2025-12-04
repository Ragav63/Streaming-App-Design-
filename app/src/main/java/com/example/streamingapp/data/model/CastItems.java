package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class CastItems implements Parcelable {
    String personName, personDesignation;
    private List<String> filmographies;
    private List<String> biographyDetails;
    private List<String> personImages;   // FIXED

    public CastItems(String personName, String personDesignation, List<String> personImages) {
        this.personName = personName;
        this.personDesignation = personDesignation;
        this.personImages = personImages;
    }

    protected CastItems(Parcel in) {
        personName = in.readString();
        personDesignation = in.readString();
        filmographies = in.createStringArrayList();
        biographyDetails = in.createStringArrayList();
        personImages = in.createStringArrayList();
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

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonDesignation() {
        return personDesignation;
    }

    public void setPersonDesignation(String personDesignation) {
        this.personDesignation = personDesignation;
    }

    public List<String> getPersonImages() {   // FIXED
        return personImages;
    }

    public void setPersonImages(List<String> personImages) {   // FIXED
        this.personImages = personImages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(personName);
        parcel.writeString(personDesignation);
        parcel.writeStringList(filmographies);
        parcel.writeStringList(biographyDetails);
        parcel.writeStringList(personImages);
    }

    @Override
    public String toString() {
        return "CastItems{" +
                "personName='" + personName + '\'' +
                ", personDesignation='" + personDesignation + '\'' +
                ", filmographies=" + filmographies +
                ", biographyDetails=" + biographyDetails +
                ", personImages=" + personImages +
                '}';
    }
}
