package com.fyp.mrisecondscreen.utils;

import android.content.Context;

import java.util.HashMap;

public class User {
    private String name;
    private String email;
    private SessionManagement session;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(Context applicationContext) {
        session = new SessionManagement(applicationContext);

        if (session.isLoggedIn())
        {
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();
            // name
            name = user.get(SessionManagement.KEY_NAME);
            // email
            email = user.get(SessionManagement.KEY_EMAIL);
        }
    }

    public void logout() {
        if (session.isLoggedIn())
        {
            session.logoutUser();
        }
    }
}