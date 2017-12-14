package com.fyp.mrisecondscreen.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManagement {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    public int PRIVATE_MODE = 0;

    // Sharedpref file name
    public static final String PREF_NAME = "AdSync";

    // All Shared Preferences Keys
    public static final String IS_LOGIN = "IsLoggedIn";

    // Name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // User name (make variable public to access from outside)
    public static final String KEY_USERNAME = "username";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Age (make variable public to access from outside)
    //public static final Integer KEY_AGE = 0;

    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String username, String email){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing username in pref
        editor.putString(KEY_USERNAME, username);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing age in pref
        //editor.putInt(String.valueOf(KEY_AGE), age);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user name
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user age
        //user.put(KEY_AGE, pref.getInt(KEY_AGE, null));

        // return user
        return user;
    }

    public void checkLogin(){
        // Check login status
        /*if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
        else
        {
            // user is logged in redirect him to Main Activity
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Starting Main Activity
            _context.startActivity(i);
        }*/

    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        /*// After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);*/
    }

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }


}