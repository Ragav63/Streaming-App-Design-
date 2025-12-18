package com.example.streamingapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A reusable SharedPreferences manager for saving/loading sets of integers.
 */
public class LocalManager {

    private final Context context;

    // ----- Preference file names -----
    private static final String AVATOR_PREFS = "avator_prefs";
    private static final String GENRE_PREFS = "genre_prefs";
    private static final String YEAR_PREFS = "year_prefs";
    private static final String FILTER_PREFS = "FiltersPrefs";
    private static final String COUNTRY_PREFS = "country_prefs";
    private static final String CATEGORY_PREFS = "category_prefs";

    // ----- Keys -----
    private static final String KEY_SELECTED_POSITIONS = "selected_positions";
    private static final String KEY_SELECTED_CATEGORIES = "selected_categories";
    private static final String KEY_SELECTED_GENRES = "selected_genres";
    private static final String KEY_COUNTRY_SELECTED = "selected_countries";
    private static final String FROM_YEAR_KEY = "from_year";
    private static final String TO_YEAR_KEY = "to_year";

    // ================= LOGIN CREDENTIALS =================
    private static final String LOGIN_PREFS = "login_prefs";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    //==================== PLAYER PREFERENCES ===================
    private static final String PLAYER_PREFS = "player_prefs";
    private static final String KEY_SPEED = "KEY_SPEED";
    private static final String KEY_AUDIO = "KEY_AUDIO";


    public LocalManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void clearAllPrefs() {
        try {
            File prefsDir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
            if (prefsDir.exists() && prefsDir.isDirectory()) {
                for (File file : prefsDir.listFiles()) {
                    String prefName = file.getName().replace(".xml", "");
                    SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
                    prefs.edit().clear().apply();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CATEGORY POSITION METHODS (Integer based for adapter) =================
    public void saveCategoryPositions(Set<Integer> positions) {
        saveIntSet(CATEGORY_PREFS, KEY_SELECTED_POSITIONS, positions);
        Log.d("LocalManager", "Saved category positions: " + positions);
    }

    public Set<Integer> loadCategoryPositions() {
        Set<Integer> positions = loadIntSet(CATEGORY_PREFS, KEY_SELECTED_POSITIONS);
        Log.d("LocalManager", "Loaded category positions: " + positions);
        return positions;
    }

    public void clearCategorySelection() {
        clearPrefs(CATEGORY_PREFS);
    }

    // ================= AVATOR METHODS =================
    public void saveAvatorSelection(Set<Integer> positions) {
        saveIntSet(AVATOR_PREFS, KEY_SELECTED_POSITIONS, positions);
        Log.d("LocalManager", "Saved genre positions: " + positions);
    }

    public Set<Integer> loadAvatorSelection() {
        Set<Integer> positions = loadIntSet(AVATOR_PREFS, KEY_SELECTED_POSITIONS);
        Log.d("LocalManager", "Loaded genre positions: " + positions);
        return positions;
    }

    public void clearAvatorSelection() {
        clearPrefs(AVATOR_PREFS);
    }


    // ================= GENRE METHODS =================
    public void saveGenreSelection(Set<Integer> positions) {
        saveIntSet(GENRE_PREFS, KEY_SELECTED_POSITIONS, positions);
        Log.d("LocalManager", "Saved genre positions: " + positions);
    }

    public Set<Integer> loadGenreSelection() {
        Set<Integer> positions = loadIntSet(GENRE_PREFS, KEY_SELECTED_POSITIONS);
        Log.d("LocalManager", "Loaded genre positions: " + positions);
        return positions;
    }

    public void clearGenreSelection() {
        clearPrefs(GENRE_PREFS);
    }

    // ================= COUNTRY METHODS =================
    public void saveCountrySelection(Set<String> countries) {
        SharedPreferences prefs = context.getSharedPreferences(COUNTRY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putStringSet(KEY_COUNTRY_SELECTED, countries).apply();
        Log.d("LocalManager", "Saved country selection: " + countries);
    }

    public Set<String> loadCountrySelection() {
        SharedPreferences prefs = context.getSharedPreferences(COUNTRY_PREFS, Context.MODE_PRIVATE);
        Set<String> data = prefs.getStringSet(KEY_COUNTRY_SELECTED, new HashSet<>());
        Set<String> result = new HashSet<>(data);
        Log.d("LocalManager", "Loaded country selection: " + result);
        return result;
    }

    public void clearCountrySelection() {
        clearPrefs(COUNTRY_PREFS);
    }

    // ================= YEAR METHODS =================
    public void saveYearRange(int fromYear, int toYear) {
        SharedPreferences prefs = context.getSharedPreferences(YEAR_PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putInt(FROM_YEAR_KEY, fromYear)
                .putInt(TO_YEAR_KEY, toYear)
                .apply();
        Log.d("LocalManager", "Saved year range: " + fromYear + " - " + toYear);
    }

    public int loadFromYear(int defaultYear) {
        SharedPreferences prefs = context.getSharedPreferences(YEAR_PREFS, Context.MODE_PRIVATE);
        int year = prefs.getInt(FROM_YEAR_KEY, defaultYear);
        Log.d("LocalManager", "Loaded fromYear: " + year);
        return year;
    }

    public int loadToYear(int defaultYear) {
        SharedPreferences prefs = context.getSharedPreferences(YEAR_PREFS, Context.MODE_PRIVATE);
        int year = prefs.getInt(TO_YEAR_KEY, defaultYear);
        Log.d("LocalManager", "Loaded toYear: " + year);
        return year;
    }

    public void clearYearPrefs() {
        clearPrefs(YEAR_PREFS);
    }



    // ================= FILTER SETTINGS =================
    public void saveBoolean(String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
        Log.d("LocalManager", "Saved boolean to " + key + ": " + value);
    }

    public boolean loadBoolean(String key, boolean defaultVal) {
        SharedPreferences prefs = context.getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
        boolean value = prefs.getBoolean(key, defaultVal);
        Log.d("LocalManager", "Loaded boolean from " + key + ": " + value);
        return value;
    }

    public void saveSortOption(String option) {
        SharedPreferences prefs = context.getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString("selectedSortOption", option).apply();
        Log.d("LocalManager", "Saved sort option: " + option);
    }

    public String loadSortOption() {
        SharedPreferences prefs = context.getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
        String option = prefs.getString("selectedSortOption", "popularTv");
        Log.d("LocalManager", "Loaded sort option: " + option);
        return option;
    }

    // ================= GENERIC METHODS =================
    private void saveIntSet(String prefName, String key, Set<Integer> values) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        Set<String> stringSet = values.stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());
        prefs.edit().putStringSet(key, stringSet).apply();
        Log.d("LocalManager", "Saved int set to " + prefName + "." + key + ": " + values);
    }

    private Set<Integer> loadIntSet(String prefName, String key) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        Set<String> stringSet = prefs.getStringSet(key, new HashSet<>());
        Set<Integer> result = new HashSet<>();
        for (String s : stringSet) {
            try {
                result.add(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {}
        }
        Log.d("LocalManager", "Loaded int set from " + prefName + "." + key + ": " + result);
        return result;
    }

    private void clearPrefs(String prefName) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Log.d("LocalManager", "Cleared preferences: " + prefName);
    }

    public void saveLoginCredentials(String email, String password) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }


    public String loadEmail() {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, "");
    }

    public String loadPassword() {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PASSWORD, "");
    }

    public void clearLogin() {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public void setLoggedIn(boolean loggedIn) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setSpeed(float value) {
        SharedPreferences prefs = context.getSharedPreferences(PLAYER_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putFloat(KEY_SPEED, value).apply();
    }

    public float getSpeed() {
        SharedPreferences prefs = context.getSharedPreferences(PLAYER_PREFS, Context.MODE_PRIVATE);
        return prefs.getFloat(KEY_SPEED, 1f);  // default = Normal
    }

    public void setAudio(String audio) {
        SharedPreferences prefs = context.getSharedPreferences(PLAYER_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_AUDIO, audio).apply();
    }

    public String getAudio() {
        SharedPreferences prefs = context.getSharedPreferences(PLAYER_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY_AUDIO, "Auto"); // default
    }

    public void clearPlayerSettingsPref() {
        clearPrefs(PLAYER_PREFS);
    }


}