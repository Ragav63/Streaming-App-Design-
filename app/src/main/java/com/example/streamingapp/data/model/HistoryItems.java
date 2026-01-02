package com.example.streamingapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryItems {

    private int id;
    private String title;
    private String viewedAt;          // "10 Jun 2024, 11:30 PM"
    private String imageUrl;
    private String videoUrl;
    private long durationMs;          // total video duration
    private long watchedMs;           // how much user watched
    private boolean fullyWatched;
    private ContentType contentType;   // ✅ NEW
}

