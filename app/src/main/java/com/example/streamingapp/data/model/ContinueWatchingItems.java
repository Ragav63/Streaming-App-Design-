package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // getters, setters, toString, equals, hashCode
@AllArgsConstructor     // full constructor
@NoArgsConstructor
public class ContinueWatchingItems implements Parcelable{
    String conWatchTitle, conWatchDesc;
    int conWatchImg;


    protected ContinueWatchingItems(Parcel in) {
        conWatchTitle = in.readString();
        conWatchDesc = in.readString();
        conWatchImg = in.readInt();
    }

    public static final Creator<ContinueWatchingItems> CREATOR = new Creator<ContinueWatchingItems>() {
        @Override
        public ContinueWatchingItems createFromParcel(Parcel in) {
            return new ContinueWatchingItems(in);
        }

        @Override
        public ContinueWatchingItems[] newArray(int size) {
            return new ContinueWatchingItems[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conWatchTitle);
        dest.writeString(conWatchDesc);
        dest.writeInt(conWatchImg);
    }
}
