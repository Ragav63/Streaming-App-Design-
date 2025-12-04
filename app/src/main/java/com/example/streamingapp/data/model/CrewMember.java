package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class CrewMember implements Parcelable {
    private String name;
    private String designation;
    private List<String> images;
    private String about;

    public CrewMember(String name, String designation, List<String> images, String about) {
        this.name = name;
        this.designation = designation;
        this.images = images;
        this.about = about;
    }

    protected CrewMember(Parcel in) {
        name = in.readString();
        designation = in.readString();
        images = in.createStringArrayList();
        about = in.readString();
    }

    public static final Creator<CrewMember> CREATOR = new Creator<CrewMember>() {
        @Override
        public CrewMember createFromParcel(Parcel in) {
            return new CrewMember(in);
        }

        @Override
        public CrewMember[] newArray(int size) {
            return new CrewMember[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(designation);
        dest.writeStringList(images);
        dest.writeString(about);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public String toString() {
        return "CrewMember{" +
                "name='" + name + '\'' +
                ", designation='" + designation + '\'' +
                ", images=" + images +
                ", about='" + about + '\'' +
                '}';
    }
}
