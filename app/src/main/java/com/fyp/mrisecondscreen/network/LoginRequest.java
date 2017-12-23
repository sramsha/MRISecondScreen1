package com.fyp.mrisecondscreen.network;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "http://lb-89089438.us-east-2.elb.amazonaws.com/api/users/login";
    private Map<String, String> params;

    public LoginRequest(String username, String password, Response.Listener<String> listener){
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("ID", username);
        params.put("password", password);
        Log.e("LoginRequest", "Sending login request to"+LOGIN_REQUEST_URL);
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
