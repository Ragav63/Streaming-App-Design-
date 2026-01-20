package com.example.streamingapp.presentation.utils;

import java.util.Locale;

public class UtilFunctions {
    public static String formatTime(long ms) {
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
    }
}
