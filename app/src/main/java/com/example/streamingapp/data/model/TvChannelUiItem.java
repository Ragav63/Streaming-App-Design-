package com.example.streamingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TvChannelUiItem implements Parcelable {
    private String channelLogo;    // Channel logo URL or resource
    private String channelName;    // Channel name
    private String programmeName;  // Current/Live programme name
    private String programmeTiming;// Timing string
    private String programmeUrl;   // Thumbnail or media URL
    private String programmeStatus; // live, past, upcoming

    protected TvChannelUiItem(Parcel in) {
        channelLogo = in.readString();
        channelName = in.readString();
        programmeName = in.readString();
        programmeTiming = in.readString();
        programmeUrl = in.readString();
        programmeStatus = in.readString();
    }

    public static final Creator<TvChannelUiItem> CREATOR = new Creator<TvChannelUiItem>() {
        @Override
        public TvChannelUiItem createFromParcel(Parcel in) {
            return new TvChannelUiItem(in);
        }

        @Override
        public TvChannelUiItem[] newArray(int size) {
            return new TvChannelUiItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(channelLogo);
        parcel.writeString(channelName);
        parcel.writeString(programmeName);
        parcel.writeString(programmeTiming);
        parcel.writeString(programmeUrl);
        parcel.writeString(programmeStatus);
    }
}