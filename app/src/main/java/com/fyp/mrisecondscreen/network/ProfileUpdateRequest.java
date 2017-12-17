package com.fyp.mrisecondscreen.network;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.fyp.mrisecondscreen.utils.User;

import java.util.HashMap;
import java.util.Map;

public class ProfileUpdateRequest extends StringRequest {

    private static final String PROFILE_UPDATE_REQUEST_URL = "http://192.168.1.102:9999/updateProfile.php";
    private Map<String, String> params;

    public ProfileUpdateRequest(User user, Response.Listener<String> listener) {
        super(Request.Method.POST, PROFILE_UPDATE_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", user.getName() + "");
        params.put("email", user.getEmail() + "");
        params.put("ID", user.getID() + "");
        params.put("password", user.getPassword() + "");
        params.put("gender", user.getGender() + "");
        params.put("relationshipStatus", user.getRelationshipStatus() + "");
        params.put("birthday", user.getBirthday() + "");
        params.put("location", user.getLocation() + "");
        params.put("MAC", user.getMAC() + "");
        params.put("mobileNumber", user.getMobileNumber() + "");
        params.put("isProfileComplete", user.isProfileComplete() + "");
        //params.put("imei", imei);
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }

}
