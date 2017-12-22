package com.fyp.mrisecondscreen.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.Objects;

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
    private String city;
    private String country;
    private SessionManagement session;
    public boolean loggedInFromFacebook;

    public User(Context applicationContext) {
        session = new SessionManagement(applicationContext);

        name = email = ID = password = gender = relationshipStatus = birthday = location = MAC = mobileNumber = city = country = null;
        isProfileComplete = loggedInFromFacebook = false;

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
            this.loggedInFromFacebook = Boolean.parseBoolean(user.get(SessionManagement.KEY_LOGGED_IN_FROM_FACEBOOK));
            this.city = user.get(SessionManagement.KEY_CITY);
            this.country = user.get(SessionManagement.KEY_COUNTRY);
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void logout() {
        if (session.isLoggedIn())
        {
            session.logoutUser();
        }
    }


    public void updateSession() {
        session.createLoginSession(name, email, ID, password, gender, relationshipStatus, birthday,
                location, MAC, mobileNumber, String.valueOf(loggedInFromFacebook), city, country);
    }

    public void updateProfile() {
        if (Objects.equals(name, "") || Objects.equals(name, "null") || name == null)
            isProfileComplete = false;
        else if (Objects.equals(email, "") || Objects.equals(email, "null") || email == null)
            isProfileComplete = false;
        else if (Objects.equals(ID, "") || Objects.equals(ID, "null") || ID == null)
            isProfileComplete = false;
        else if (Objects.equals(password, "") || Objects.equals(password, "null") || password == null)
            isProfileComplete = false;
        else if (Objects.equals(gender, "") || Objects.equals(gender, "null") || gender == null)
            isProfileComplete = false;
        else if (Objects.equals(relationshipStatus, "") || Objects.equals(relationshipStatus, "null") || relationshipStatus == null)
            isProfileComplete = false;
        else if (Objects.equals(birthday, "") || Objects.equals(birthday, "null") || birthday == null)
            isProfileComplete = false;
        else if (Objects.equals(location, "") || Objects.equals(location, "null") || location == null)
            isProfileComplete = false;
        else if (Objects.equals(MAC, "") || Objects.equals(MAC, "null") || MAC == null)
            isProfileComplete = false;
        else if (Objects.equals(mobileNumber, "") || Objects.equals(mobileNumber, "null") || mobileNumber == null)
            isProfileComplete = false;
        else if (Objects.equals(city, "") || Objects.equals(city, "null") || city == null)
            isProfileComplete = false;
        else if (Objects.equals(country, "") || Objects.equals(country, "null") || country == null)
            isProfileComplete = false;
        else
            isProfileComplete = true;
    }

    public void setIsProfileComplete(boolean isProfileComplete) {
        this.isProfileComplete = isProfileComplete;
    }
}