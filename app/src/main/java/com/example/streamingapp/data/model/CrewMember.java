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
@NoArgsConstructor
public class CrewMember implements Parcelable {
    private String name;
    private String designation;
    private List<String> images;
    private String about;

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
}
