package com.johnmillercoding.slidingpuzzle.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.login.LoginManager;

public class SessionManager {

    // Shared Preferences
    private final SharedPreferences pref;

    private final Editor editor;

    // SharedPreferences tags
    private static final String PREFS = "prefs";
    private static final String EMAIL = "email";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String FACEBOOK_ACCESS_TOKEN = "facebookAccessToken";
    private static final String FACEBOOK_IMAGE_URL = "facebookImageURL";
    private static final String PUZZLE_PATH = "puzzle_path";
    private static final String ROWS = "rows";
    private static final String COLS = "cols";
    private static final String UNLOCKED = "unlocked";

    // Constructor
    public SessionManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREFS, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    /**
     * Sets the login status preference.
     */
    public void setLoggedIn() {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Sets the user's email preference.
     * @param email the email.
     */
    public void setEmail(String email){
        editor.putString(EMAIL, email);
        editor.apply();
    }

    /**
     * Sets the user's Facebook profile picture url preference.
     * @param url the url.
     */
    public void setFacebookImageUrl(String url){
        editor.putString(FACEBOOK_IMAGE_URL, url);
        editor.apply();
    }

    /**
     * Sets the user's free play puzzle path preference.
     * @param puzzlePath the puzzle path.
     */
    public void setPuzzlePath(String puzzlePath){
        editor.putString(PUZZLE_PATH, puzzlePath);
        editor.apply();
    }

    /**
     * Gets the user's free play puzzle path preference.
     * @return the puzzle path.
     */
    public String getPuzzlePath(){
        return pref.getString(PUZZLE_PATH, null);
    }

    /**
     * Gets the user's Facebook profile picture url preference.
     * @return the url.
     */
    public String getFacebookImageUrl(){
        return pref.getString(FACEBOOK_IMAGE_URL, "");
    }

    /**
     * Checks the status of the logged in preference.
     * @return true or false.
     */
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    /**
     * Gets the user's email preference.
     * @return the email.
     */
    public String getEmail() { return pref.getString(EMAIL, ""); }

    /**
     * Sets the user's row preference.
     * @param rows the rows.
     */
    public void setRows(int rows){
        editor.putInt(ROWS, rows);
        editor.apply();
    }

    /**
     * Gets the user's row preference.
     * @return the rows.
     */
    public int getRows(){
        return pref.getInt(ROWS, 4);
    }

    /**
     * Sets the user's column preference.
     * @param cols the columns.
     */
    public void setCols(int cols){
        editor.putInt(COLS, cols);
        editor.apply();
    }

    /**
     * Gets the user's column preference.
     * @return the columns.
     */
    public int getCols(){
        return pref.getInt(COLS, 3);
    }

    /**
     * Sets the user's unlocked levels preference.
     * @param unlocked the levels unlocked.
     */
    public void setUnlocked(int unlocked){
        editor.putInt(UNLOCKED, unlocked);
        editor.apply();
    }

    /**
     * Gets the user's unlocked levels preference.
     * @return the levels unlocked.
     */
    public int getUnlocked(){
        return pref.getInt(UNLOCKED, 0);
    }

    /**
     * Saves the user's Facebook access token.
     * @param token the access token.
     */
    public void saveAccessToken(String token) {
        editor.putString(FACEBOOK_ACCESS_TOKEN, token);
        editor.apply();
    }

    /**
     * Clears the shared preferences and logs the user out.
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
        LoginManager.getInstance().logOut();
    }
}