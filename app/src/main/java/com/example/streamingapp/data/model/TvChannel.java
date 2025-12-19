package com.example.streamingapp.data.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TvChannel {
    private String channelLogo;         // channel logo image
    private String channelName;         // channel name
    private List<Programme> programmes; // list of programmes
}

