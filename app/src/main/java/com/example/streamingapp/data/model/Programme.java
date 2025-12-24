package com.example.streamingapp.data.model;

import java.util.List;

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
    private List<String> genres;
    private String country;
    private String imdb_rating;
    private String year;
}
