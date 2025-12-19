package com.example.streamingapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // getters, setters, toString, equals, hashCode
@AllArgsConstructor     // full constructor
public class HistoryItems {
    int id;
    String historyTitle, historyTiming;
}
