package com.example.streamingapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.streamingapp.data.model.FilterState;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.PickItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Centralized SharedPreferences manager
 * MUST be initialized once in Application class
 */
public final class LocalManager {

    private static final String PREFS_NAME = "app_prefs";

    private static SharedPreferences prefs;
    private static final Gson gson = new Gson();

    private LocalManager() {}

    // ===================== INIT =====================
    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getApplicationContext()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    private static void checkInit() {
        if (prefs == null) {
            throw new IllegalStateException("LocalManager.init(context) not called");
        }
    }

    // ===================== KEYS =====================

    private static final String KEY_GUEST_START_TIME = "guest_start_time";
    private static final long GUEST_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    private static final String KEY_SELECTED_CATEGORIES = "selected_categories";
    private static final String KEY_SELECTED_GENRES = "selected_genres";
    private static final String KEY_SELECTED_COUNTRIES = "selected_countries";

    private static final String KEY_FROM_YEAR = "from_year";
    private static final String KEY_TO_YEAR = "to_year";

    private static final String KEY_AVATAR_ITEMS = "avatar_items";

    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LOGGED_IN = "logged_in";

    private static final String KEY_PLAYER_SPEED = "player_speed";
    private static final String KEY_PLAYER_AUDIO = "player_audio";

    private static final String KEY_HISTORY = "watch_history";

    private static final String KEY_PARENTAL_ENABLED = "parental_enabled";
    private static final String KEY_SCREEN_TIME = "key_screen_time";
    private static final String KEY_MATURITY_LEVEL = "maturity_level";
    private static final String KEY_PARENTAL_PIN = "parental_pin";

    private static final String KEY_FILTER = "filter";
    private static final String KEY_FILTER_MODE = "selected_filter_selectedMode";
    private static final String KEY_FILTER_GENRES = "selected_filter_genres";
    private static final String KEY_FILTER_YEAR = "selected_filter_year";
    private static final String KEY_FILTER_COUNTRIES = "selected_filter_countries";
    private static final String KEY_FILTER_SORT_BY = "selected_filter_sort";



    // ===================== GUEST MODE =====================

    public static void startGuestSession() {
        checkInit();
        prefs.edit()
                .putLong(KEY_GUEST_START_TIME, System.currentTimeMillis())
                .apply();
    }

    public static boolean isGuestSessionActive() {
        checkInit();
        long start = prefs.getLong(KEY_GUEST_START_TIME, -1);
        if (start == -1) return false;

        return (System.currentTimeMillis() - start) < GUEST_DURATION_MS;
    }

    public static void clearGuestSession() {
        checkInit();
        prefs.edit().remove(KEY_GUEST_START_TIME).apply();
    }

    // ===================== INTERNAL =====================
    private static void saveIntSet(String key, Set<Integer> values) {
        checkInit();
        Set<String> set = new HashSet<>();
        for (int v : values) set.add(String.valueOf(v));
        prefs.edit().putStringSet(key, set).apply();
    }

    private static Set<Integer> loadIntSet(String key) {
        checkInit();
        Set<String> data = prefs.getStringSet(key, new HashSet<>());
        Set<Integer> result = new HashSet<>();
        for (String s : data) result.add(Integer.parseInt(s));
        return result;
    }

    // ===================== CATEGORY =====================
    public static void saveCategoryPositions(Set<Integer> set) {
        saveIntSet(KEY_SELECTED_CATEGORIES, set);
    }

    public static Set<Integer> loadCategoryPositions() {
        return loadIntSet(KEY_SELECTED_CATEGORIES);
    }

    public static void clearCategories() {
        checkInit();
        prefs.edit().remove(KEY_SELECTED_CATEGORIES).apply();
    }

    // ===================== GENRE =====================
    public static void saveGenreSelection(Set<Integer> set) {
        saveIntSet(KEY_SELECTED_GENRES, set);
    }

    public static Set<Integer> loadGenreSelection() {
        return loadIntSet(KEY_SELECTED_GENRES);
    }

    public static void clearGenres() {
        checkInit();
        prefs.edit().remove(KEY_SELECTED_GENRES).apply();
    }

    // ===================== COUNTRY =====================
    public static void saveCountrySelection(Set<String> set) {
        checkInit();
        prefs.edit().putStringSet(KEY_SELECTED_COUNTRIES, set).apply();
    }

    public static Set<String> loadCountrySelection() {
        checkInit();
        return new HashSet<>(prefs.getStringSet(KEY_SELECTED_COUNTRIES, new HashSet<>()));
    }

    public static void clearCountries() {
        checkInit();
        prefs.edit().remove(KEY_SELECTED_COUNTRIES).apply();
    }

    // ===================== YEAR =====================
    public static void saveYearRange(int from, int to) {
        checkInit();
        prefs.edit()
                .putInt(KEY_FROM_YEAR, from)
                .putInt(KEY_TO_YEAR, to)
                .apply();
    }

    public static int loadFromYear(int def) {
        checkInit();
        return prefs.getInt(KEY_FROM_YEAR, def);
    }

    public static int loadToYear(int def) {
        checkInit();
        return prefs.getInt(KEY_TO_YEAR, def);
    }

    public static void clearYearRange() {
        checkInit();
        prefs.edit()
                .remove(KEY_FROM_YEAR)
                .remove(KEY_TO_YEAR)
                .apply();
    }

    // ===================== AVATAR (SINGLE SELECTION) =====================

    public static void saveAvatar(PickItem item) {
        checkInit();
        if (item == null) return;

        // Always replace
        prefs.edit()
                .putString(KEY_AVATAR_ITEMS, gson.toJson(item))
                .apply();
    }

    @Nullable
    public static PickItem loadAvatar() {
        checkInit();
        String json = prefs.getString(KEY_AVATAR_ITEMS, null);
        if (json == null) return null;

        return gson.fromJson(json, PickItem.class);
    }

    public static void clearAvatar() {
        checkInit();
        prefs.edit().remove(KEY_AVATAR_ITEMS).apply();
    }


    // ===================== LOGIN =====================
    public static void saveLogin(String name, String email, String password) {
        checkInit();
        prefs.edit()
                .putString(KEY_USERNAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public static boolean isLoggedIn() {
        checkInit();
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public static String  loadUserName() {
        checkInit();
        return prefs.getString(KEY_USERNAME,"");
    }

    public static String  loadEmail() {
        checkInit();
        return prefs.getString(KEY_EMAIL,"");
    }
    public static void clearLogin() {
        checkInit();
        prefs.edit()
                .remove(KEY_USERNAME)
                .remove(KEY_PASSWORD)
                .remove(KEY_LOGGED_IN)
                .apply();
    }

    // ===================== PLAYER =====================
    public static void setSpeed(float speed) {
        checkInit();
        prefs.edit().putFloat(KEY_PLAYER_SPEED, speed).apply();
    }

    public static float getSpeed() {
        checkInit();
        return prefs.getFloat(KEY_PLAYER_SPEED, 1f);
    }

    public static void setAudio(String audio) {
        checkInit();
        prefs.edit().putString(KEY_PLAYER_AUDIO, audio).apply();
    }

    public static String getAudio() {
        checkInit();
        return prefs.getString(KEY_PLAYER_AUDIO, "Auto");
    }

    public static void resetPlayerSettings() {
        checkInit();
        prefs.edit()
                .putFloat(KEY_PLAYER_SPEED, 1f)
                .putString(KEY_PLAYER_AUDIO, "Auto")
                .apply();
    }

    // ===================== HISTORY =====================
    public static void saveHistory(HistoryItems item) {
        checkInit();
        List<HistoryItems> list = getHistory();
        list.removeIf(i -> i.getTitle().equals(item.getTitle()));
        list.add(0, item);
        if (list.size() > 10) list = list.subList(0, 10);
        prefs.edit().putString(KEY_HISTORY, gson.toJson(list)).apply();
    }

    public static List<HistoryItems> getHistory() {
        checkInit();
        String json = prefs.getString(KEY_HISTORY, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<HistoryItems>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static boolean removeHistoryItem(HistoryItems item) {
        checkInit();
        if (item == null) return false;

        List<HistoryItems> list = getHistory();
        if (list.isEmpty()) return false;

        boolean removed = false;

        for (int i = 0; i < list.size(); i++) {
            if (item.getVideoUrl().equals(list.get(i).getVideoUrl())) {
                list.remove(i);
                removed = true;
                break;
            }
        }

        if (removed) {
            prefs.edit()
                    .putString(KEY_HISTORY, gson.toJson(list))
                    .apply();
        }

        return removed;
    }




    public static void clearHistory() {
        checkInit();
        prefs.edit().remove(KEY_HISTORY).apply();
    }

    // ===================== PARENTAL =====================
    public static boolean hasParentalPin() {
        checkInit();
        String pin = prefs.getString(KEY_PARENTAL_PIN, "");
        return pin != null && pin.length() >= 4;
    }

    public static void setParentalEnabled(boolean enabled) {
        checkInit();
        prefs.edit().putBoolean(KEY_PARENTAL_ENABLED, enabled).apply();
    }

    public static boolean isParentalEnabled() {
        checkInit();
        return prefs.getBoolean(KEY_PARENTAL_ENABLED, false);
    }

    public static void setScreenTimeIndex(int index) {
        checkInit();
        prefs.edit().putInt(KEY_SCREEN_TIME, index).apply();
    }

    public static int getScreenTimeIndex() {
        checkInit();
        return prefs.getInt(KEY_SCREEN_TIME, 0);
    }

    public static void setMaturityLevel(int level) {
        checkInit();
        prefs.edit().putInt(KEY_MATURITY_LEVEL, level).apply();
    }

    public static int getMaturityLevel() {
        checkInit();
        return prefs.getInt(KEY_MATURITY_LEVEL, 0);
    }

    public static void setParentalPin(String pin) {
        checkInit();
        prefs.edit().putString(KEY_PARENTAL_PIN, pin).apply();
    }

    public static String getParentalPin() {
        checkInit();
        return prefs.getString(KEY_PARENTAL_PIN, "");
    }

    public static void resetParentalControl() {
        checkInit();
        prefs.edit()
                .putBoolean(KEY_PARENTAL_ENABLED, false)
                .putInt(KEY_MATURITY_LEVEL, 0)
                .remove(KEY_PARENTAL_PIN)
                .apply();
    }

    // ===================== SEARCH FILTERS =====================

    // MODE
    public static void saveFilterMode(@Nullable FilterState.Mode mode) {
        checkInit();
        if (mode == null) {
            prefs.edit().remove(KEY_FILTER_MODE).apply();
        } else {
            prefs.edit().putString(KEY_FILTER_MODE, mode.name()).apply();
        }
    }

    @Nullable
    public static FilterState.Mode loadFilterMode() {
        checkInit();
        String value = prefs.getString(KEY_FILTER_MODE, null);
        return value == null ? null : FilterState.Mode.valueOf(value);
    }

    // GENRES (already OK)
    public static void saveFilterGenreSelection(Set<Integer> set) {
        saveIntSet(KEY_FILTER_GENRES, set);
    }

    public static Set<Integer> loadFilterGenreSelection() {
        return loadIntSet(KEY_FILTER_GENRES);
    }

    // YEAR
    public static void saveFilterYear(@Nullable String year) {
        checkInit();
        if (year == null) prefs.edit().remove(KEY_FILTER_YEAR).apply();
        else prefs.edit().putString(KEY_FILTER_YEAR, year).apply();
    }

    @Nullable
    public static String loadFilterYear() {
        checkInit();
        return prefs.getString(KEY_FILTER_YEAR, null);
    }

    // COUNTRY
    public static void saveFilterCountry(@Nullable String country) {
        checkInit();
        if (country == null) prefs.edit().remove(KEY_FILTER_COUNTRIES).apply();
        else prefs.edit().putString(KEY_FILTER_COUNTRIES, country).apply();
    }

    @Nullable
    public static String loadFilterCountry() {
        checkInit();
        return prefs.getString(KEY_FILTER_COUNTRIES, null);
    }

    // SORT
    public static void saveFilterSort(@Nullable String sort) {
        checkInit();
        if (sort == null) prefs.edit().remove(KEY_FILTER_SORT_BY).apply();
        else prefs.edit().putString(KEY_FILTER_SORT_BY, sort).apply();
    }

    @Nullable
    public static String loadFilterSort() {
        checkInit();
        return prefs.getString(KEY_FILTER_SORT_BY, null);
    }

    // CLEAR ALL FILTERS (IMPORTANT)
    public static void clearAllFilters() {
        checkInit();
        prefs.edit()
                .remove(KEY_FILTER_MODE)
                .remove(KEY_FILTER_GENRES)
                .remove(KEY_FILTER_YEAR)
                .remove(KEY_FILTER_COUNTRIES)
                .remove(KEY_FILTER_SORT_BY)
                .apply();
    }


    // ===================== FULL RESET =====================
    public static void clearAll() {
        checkInit();
        prefs.edit().clear().apply();
    }

}