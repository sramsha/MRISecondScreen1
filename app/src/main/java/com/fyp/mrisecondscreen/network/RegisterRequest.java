package com.fyp.mrisecondscreen.network;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "http://118.103.237.80:9999/registerCustom.php";
    private Map<String, String> params;

    public RegisterRequest(String name, String username, int age, String email, String password, String gender, String mac, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("username", username);
        params.put("age", age + "");
        params.put("email", email);
        params.put("password", password);
        params.put("gender", gender);
        params.put("mac", mac);
        //params.put("imei", imei);
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }

}
