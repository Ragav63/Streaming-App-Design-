package com.example.streamingapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // getters, setters, toString, equals, hashCode
@NoArgsConstructor
public class TvItems {
    String tvLogoName, tvName, currentProgramName, currentProgramTiming;
    private boolean isFavorite = false;
    int img;

    public TvItems(
            String tvLogoName,
            String tvName,
            String currentProgramName,
            String currentProgramTiming,
            int img
    ) {
        this.tvLogoName = tvLogoName;
        this.tvName = tvName;
        this.currentProgramName = currentProgramName;
        this.currentProgramTiming = currentProgramTiming;
        this.img = img;
        this.isFavorite = false; // explicit, predictable
    }

}
