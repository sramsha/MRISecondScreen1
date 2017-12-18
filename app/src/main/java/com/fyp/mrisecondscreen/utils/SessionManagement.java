package com.fyp.mrisecondscreen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

public class SessionManagement {

    // Sharedpref file name
    public static final String PREF_NAME = "AdSync";
    // All Shared Preferences Keys
    public static final boolean IS_LOGIN = false;
    // Name (make variable public to access from outside)
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_USERNAME = "KEY_USERNAME";
    public static final String KEY_EMAIL = "KEY_EMAIL";
    public static final String KEY_PASSWORD = "KEY_PASSWORD";
    public static final String KEY_GENDER = "KEY_GENDER";
    public static final String KEY_RELATIONSHIP_STATUS = "KEY_RELATIONSHIP_STATUS";
    public static final String KEY_BIRTHDAY = "KEY_BIRTHDAY";
    public static final String KEY_MAC = "KEY_MAC";
    public static final String KEY_LOCATION = "KEY_LOCATION";
    public static final String KEY_MOBILE_NUMBER = "KEY_MOBILE_NUMBER";
    public static final String KEY_LOGGED_IN_FROM_FACEBOOK = "KEY_LOGGED_IN_FROM_FACEBOOK";
    public static final String KEY_CITY = "KEY_CITY";
    public static final String KEY_COUNTRY = "KEY_COUNTRY";
    // Shared pref mode
    public int PRIVATE_MODE = 0;
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;


    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String email, String username, String password, String gender, String relationship_status, String birthday, String location, String MAC, String mobile_number, String loggedInFromFacebook, String city, String country) {
        // Storing login value as TRUE
        editor.putBoolean(String.valueOf(IS_LOGIN), true);
        Log.e("SM[NAME]", name);
        Log.e("SM[EMAIL]", email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_RELATIONSHIP_STATUS, relationship_status);
        editor.putString(KEY_BIRTHDAY, birthday);
        editor.putString(KEY_MAC, MAC);
        editor.putString(KEY_LOCATION, location);
        editor.putString(KEY_MOBILE_NUMBER, mobile_number);
        editor.putString(KEY_LOGGED_IN_FROM_FACEBOOK, loggedInFromFacebook);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_COUNTRY, country);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));
        user.put(KEY_RELATIONSHIP_STATUS, pref.getString(KEY_RELATIONSHIP_STATUS, null));
        user.put(KEY_BIRTHDAY, pref.getString(KEY_BIRTHDAY, null));
        user.put(KEY_MAC, pref.getString(KEY_MAC, null));
        user.put(KEY_LOCATION, pref.getString(KEY_LOCATION, null));
        user.put(KEY_MOBILE_NUMBER, pref.getString(KEY_MOBILE_NUMBER, null));
        user.put(KEY_LOGGED_IN_FROM_FACEBOOK, pref.getString(KEY_LOGGED_IN_FROM_FACEBOOK, null));
        user.put(KEY_CITY, pref.getString(KEY_CITY, null));
        user.put(KEY_COUNTRY, pref.getString(KEY_COUNTRY, null));

        // return user
        return user;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(String.valueOf(IS_LOGIN), false);
    }


}