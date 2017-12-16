package com.fyp.mrisecondscreen.utils;

import android.content.Context;

import java.util.HashMap;

public class User {
    private String name;
    private String email;
    private String ID;
    private String password;
    private String gender;
    private String relationshipStatus;
    private String birthday;
    private String location;
    private String MAC;
    private String mobileNumber;
    private boolean isProfileComplete;
    private SessionManagement session;

    public User(Context applicationContext) {
        session = new SessionManagement(applicationContext);

        name = email = ID = password = gender = relationshipStatus = birthday = location = MAC = mobileNumber = null;

        if (session.isLoggedIn()) {
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            this.name = user.get(SessionManagement.KEY_NAME);
            this.email = user.get(SessionManagement.KEY_EMAIL);
            this.ID = user.get(SessionManagement.KEY_USERNAME);
            this.password = user.get(SessionManagement.KEY_PASSWORD);
            this.gender = user.get(SessionManagement.KEY_GENDER);
            this.relationshipStatus = user.get(SessionManagement.KEY_RELATIONSHIP_STATUS);
            this.birthday = user.get(SessionManagement.KEY_BIRTHDAY);
            this.location = user.get(SessionManagement.KEY_LOCATION);
            this.MAC = user.get(SessionManagement.KEY_MAC);
            this.mobileNumber = user.get(SessionManagement.KEY_MOBILE_NUMBER);
        }
    }

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

    public boolean isProfileComplete() {
        return isProfileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        isProfileComplete = profileComplete;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void logout() {
        if (session.isLoggedIn())
        {
            session.logoutUser();
        }
    }
}