package com.example.streamingapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoryUiItem {
    int id;
    String title;
    String timing;
    String rating;
    String posterUrl; // or int drawable if local
    private ContentType contentType;   // ✅ NEW
}
