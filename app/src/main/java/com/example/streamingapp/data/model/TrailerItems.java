package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // getters, setters, toString, equals, hashCode
@AllArgsConstructor     // full constructor
@NoArgsConstructor
public class TrailerItems implements Parcelable {
    String trailerName, duration;
    String url;

    protected TrailerItems(Parcel in) {
        trailerName = in.readString();
        duration = in.readString();
        url = in.readString();
    }

    public static final Creator<TrailerItems> CREATOR = new Creator<TrailerItems>() {
        @Override
        public TrailerItems createFromParcel(Parcel in) {
            return new TrailerItems(in);
        }

        @Override
        public TrailerItems[] newArray(int size) {
            return new TrailerItems[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(trailerName);
        parcel.writeString(duration);
        parcel.writeString(url);

    }
}
