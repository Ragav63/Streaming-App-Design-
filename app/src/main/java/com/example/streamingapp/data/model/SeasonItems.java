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
public class SeasonItems implements Parcelable {
    public int seasonNumber;
    public String seasonTitle;
    public List<Episode> episodes;

    protected SeasonItems(Parcel in) {
        seasonNumber = in.readInt();
        seasonTitle = in.readString();
        episodes = in.createTypedArrayList(Episode.CREATOR);
    }

    public static final Creator<SeasonItems> CREATOR = new Parcelable.Creator<SeasonItems>() {
        @Override
        public SeasonItems createFromParcel(Parcel in) {
            return new SeasonItems(in);
        }

        @Override
        public SeasonItems[] newArray(int size) {
            return new SeasonItems[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(seasonNumber);
        dest.writeString(seasonTitle);
        dest.writeTypedList(episodes);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
