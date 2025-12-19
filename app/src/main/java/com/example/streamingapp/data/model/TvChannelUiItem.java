package com.example.streamingapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TvChannelUiItem {
    private String channelLogo;    // Channel logo URL or resource
    private String channelName;    // Channel name
    private String programmeName;  // Current/Live programme name
    private String programmeTiming;// Timing string
    private String programmeUrl;   // Thumbnail or media URL
    private String programmeStatus; // live, past, upcoming
}