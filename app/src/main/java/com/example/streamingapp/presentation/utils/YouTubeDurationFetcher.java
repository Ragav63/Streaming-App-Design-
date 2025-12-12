package com.example.streamingapp.presentation.utils;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeDurationFetcher {

    private static final String TAG = "YouTubeDurationFetcher";
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public interface DurationCallback {
        void onDurationReady(String duration);
        void onError(String message);
    }

    public static void fetchDuration(String youtubeUrl, DurationCallback callback) {
        executor.execute(() -> {
            try {
                String id = extractVideoId(youtubeUrl);
                if (id == null) {
                    callback.onError("Invalid YouTube URL");
                    return;
                }

                String raw = fetchUrl("https://www.youtube.com/get_video_info?video_id=" + id + "&el=detailpage");
                if (raw == null || raw.isEmpty()) {
                    callback.onError("No response from YT");
                    return;
                }

                Map<String, String> map = parseQuery(raw);

                if (!map.containsKey("length_seconds")) {
                    callback.onError("Duration not found");
                    return;
                }

                long sec = Long.parseLong(map.get("length_seconds"));
                callback.onDurationReady(format(sec));

            } catch (Exception e) {
                callback.onError("Exception: " + e.getMessage());
            }
        });
    }

    private static String fetchUrl(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("User-Agent", "Mozilla/5.0");
            c.setConnectTimeout(8000);
            c.setReadTimeout(8000);

            BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = r.readLine()) != null) sb.append(line);
            r.close();
            return sb.toString();

        } catch (Exception e) {
            Log.e(TAG, "fetchUrl error", e);
            return null;
        }
    }

    private static Map<String, String> parseQuery(String raw) {
        Map<String, String> out = new HashMap<>();
        try {
            String[] pairs = raw.split("&");
            for (String p : pairs) {
                String[] kv = p.split("=", 2);
                if (kv.length == 2) {
                    out.put(kv[0], URLDecoder.decode(kv[1], "UTF-8"));
                }
            }
        } catch (Exception ignored) {}
        return out;
    }

    private static String extractVideoId(String url) {
        try {
            if (url.contains("watch?v=")) {
                String id = url.substring(url.indexOf("v=") + 2);
                int amp = id.indexOf("&");
                return amp == -1 ? id : id.substring(0, amp);
            }

            if (url.contains("youtu.be/")) {
                return url.substring(url.lastIndexOf("/") + 1);
            }

            if (url.contains("embed/")) {
                return url.substring(url.indexOf("embed/") + 6);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static String format(long s) {
        long h = s / 3600;
        long m = (s % 3600) / 60;
        long sec = s % 60;

        if (h > 0) return String.format("%dh %02dm", h, m);
        return String.format("%dm %02ds", m, sec);
    }
}
