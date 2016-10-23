package com.johnmillercoding.slidingpuzzle.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.login.LoginManager;

public class SessionManager {

    // Shared Preferences
    private final SharedPreferences pref;

    private final Editor editor;

    // Shared preferences file name
    private static final String PREFS = "prefs";
    private static final String EMAIL = "email";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String FACEBOOK_ACCESS_TOKEN = "facebookAccessToken";
    private static final String FACEBOOK_IMAGE_URL = "facebookImageURL";
    private static final String PUZZLE_PATH = "puzzle_path";

    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREFS, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void setLoggedIn(boolean isLoggedIn) {

        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.commit();
    }

    public void setEmail(String email){

        editor.putString(EMAIL, email);
        editor.commit();
    }

    public void setFacebookImageUrl(String url){
        editor.putString(FACEBOOK_IMAGE_URL, url);
        editor.commit();
    }

    public void setPuzzlePath(String puzzlePath){
        editor.putString(PUZZLE_PATH, puzzlePath);
        editor.commit();
    }

    public String getPuzzlePath(){
        return pref.getString(PUZZLE_PATH, null);
    }

    public String getFacebookImageUrl(){
        return pref.getString(FACEBOOK_IMAGE_URL, "");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN, false);
    }
    public String getEmail() { return pref.getString(EMAIL, ""); }

    public void saveAccessToken(String token) {
        editor.putString(FACEBOOK_ACCESS_TOKEN, token);
        editor.commit();
    }

//    public String getToken() {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
//        return sp.getString(FACEBOOK_ACCESS_TOKEN, null);
//    }

    public void clearSession() {
        editor.clear();
        editor.commit();
        LoginManager.getInstance().logOut();
    }
}