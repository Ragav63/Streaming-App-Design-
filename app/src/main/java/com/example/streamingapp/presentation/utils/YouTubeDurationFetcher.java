package com.example.streamingapp.presentation.utils;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeDurationFetcher {

    private static final String TAG = "YouTubeDurationFetcher";

    public interface DurationCallback {
        void onDurationReady(String duration);
        void onError(String message);
    }

    public static void fetchDuration(String youtubeUrl, DurationCallback callback) {
        new FetchDurationTask(callback).execute(youtubeUrl);
    }

    private static class FetchDurationTask extends AsyncTask<String, Void, String> {
        private final DurationCallback callback;
        private String errorMessage = null;

        public FetchDurationTask(DurationCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... urls) {
            String youtubeUrl = urls[0];
            String videoId = extractVideoId(youtubeUrl);

            if (videoId == null) {
                errorMessage = "Invalid YouTube URL";
                return null;
            }

            try {
                // Try multiple methods to get duration
                String duration = getDurationMethod1(videoId);
                if (duration != null) return duration;

                duration = getDurationMethod2(videoId);
                if (duration != null) return duration;

                duration = getDurationMethod3(videoId);
                if (duration != null) return duration;

                errorMessage = "Could not extract duration";
                return null;

            } catch (Exception e) {
                errorMessage = "Error: " + e.getMessage();
                Log.e(TAG, "Error fetching duration", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String duration) {
            if (callback != null) {
                if (duration != null) {
                    callback.onDurationReady(duration);
                } else {
                    callback.onError(errorMessage != null ? errorMessage : "Unknown error");
                }
            }
        }

        // Method 1: Extract from video page (most common)
        private String getDurationMethod1(String videoId) {
            try {
                String videoPageUrl = "https://www.youtube.com/watch?v=" + videoId;
                String pageContent = fetchUrlContent(videoPageUrl);

                if (pageContent != null) {
                    // Pattern 1: Look for "lengthSeconds":"123"
                    Pattern pattern = Pattern.compile("\"lengthSeconds\":\"?(\\d+)\"?");
                    Matcher matcher = pattern.matcher(pageContent);
                    if (matcher.find()) {
                        long seconds = Long.parseLong(matcher.group(1));
                        return formatDuration(seconds);
                    }

                    // Pattern 2: Look for "approxDurationMs":"123000"
                    pattern = Pattern.compile("\"approxDurationMs\":\"?(\\d+)\"?");
                    matcher = pattern.matcher(pageContent);
                    if (matcher.find()) {
                        long ms = Long.parseLong(matcher.group(1));
                        long seconds = ms / 1000;
                        return formatDuration(seconds);
                    }

                    // Pattern 3: Look for ytInitialData
                    pattern = Pattern.compile("\"lengthSeconds\":(\\d+)");
                    matcher = pattern.matcher(pageContent);
                    if (matcher.find()) {
                        long seconds = Long.parseLong(matcher.group(1));
                        return formatDuration(seconds);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Method 1 failed", e);
            }
            return null;
        }

        // Method 2: Try with embed page
        private String getDurationMethod2(String videoId) {
            try {
                String embedUrl = "https://www.youtube.com/embed/" + videoId;
                String pageContent = fetchUrlContent(embedUrl);

                if (pageContent != null) {
                    Pattern pattern = Pattern.compile("\"length_seconds\":(\\d+)");
                    Matcher matcher = pattern.matcher(pageContent);
                    if (matcher.find()) {
                        long seconds = Long.parseLong(matcher.group(1));
                        return formatDuration(seconds);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Method 2 failed", e);
            }
            return null;
        }

        // Method 3: Try with oembed
        private String getDurationMethod3(String videoId) {
            try {
                String oembedUrl = "https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=" + videoId + "&format=json";
                String jsonResponse = fetchUrlContent(oembedUrl);

                if (jsonResponse != null && jsonResponse.contains("\"title\":")) {
                    // oembed doesn't give duration directly, but we can parse title for duration
                    // Example title: "Movie Title (2:30)"
                    Pattern pattern = Pattern.compile("\\(([0-9]+):([0-9]+)\\)");
                    Matcher matcher = pattern.matcher(jsonResponse);
                    if (matcher.find()) {
                        int minutes = Integer.parseInt(matcher.group(1));
                        int seconds = Integer.parseInt(matcher.group(2));
                        return String.format("%d:%02d", minutes, seconds);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Method 3 failed", e);
            }
            return null;
        }

        private String fetchUrlContent(String urlString) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to fetch URL: " + urlString, e);
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (connection != null) connection.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing streams", e);
                }
            }
            return null;
        }

        private String extractVideoId(String url) {
            if (url == null) return null;

            String videoId = null;

            // Handle youtu.be URLs
            if (url.contains("youtu.be/")) {
                videoId = url.substring(url.lastIndexOf("/") + 1);
                int questionPos = videoId.indexOf("?");
                if (questionPos != -1) {
                    videoId = videoId.substring(0, questionPos);
                }
            }
            // Handle youtube.com URLs
            else if (url.contains("youtube.com/watch")) {
                int vIndex = url.indexOf("v=");
                if (vIndex != -1) {
                    videoId = url.substring(vIndex + 2);
                    int ampersandPos = videoId.indexOf("&");
                    if (ampersandPos != -1) {
                        videoId = videoId.substring(0, ampersandPos);
                    }
                }
            }
            // Handle embed URLs
            else if (url.contains("youtube.com/embed/")) {
                videoId = url.substring(url.indexOf("embed/") + 6);
                int questionPos = videoId.indexOf("?");
                if (questionPos != -1) {
                    videoId = videoId.substring(0, questionPos);
                }
            }

            // Clean up: remove any # fragments
            if (videoId != null && videoId.contains("#")) {
                videoId = videoId.substring(0, videoId.indexOf("#"));
            }

            return videoId;
        }

        private String formatDuration(long seconds) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;

            if (hours > 0) {
                return String.format("%dh %02dm", hours, minutes);
            } else {
                return String.format("%dm %02ds", minutes, secs);
            }
        }
    }
}
