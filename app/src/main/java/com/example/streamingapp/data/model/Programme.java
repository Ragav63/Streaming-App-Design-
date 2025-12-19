package com.example.streamingapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Programme {
    private String name;       // programme name
    private String timing;     // programme timing
    private String url;        // link to the programme
    private String duration;   // duration of programme
    private String status;     // live, past, upcoming
}
