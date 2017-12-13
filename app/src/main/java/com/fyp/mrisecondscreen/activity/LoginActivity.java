package com.fyp.mrisecondscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fyp.mrisecondscreen.network.LoginRequest;
import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.network.RegisterRequest;
import com.fyp.mrisecondscreen.utils.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    LoginButton login_button_FB;
    CallbackManager callbackmanager;
    String Name, Email, ID, Gender;
    Integer Age=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        final EditText login_username = (EditText) findViewById(R.id.login_username);
        final EditText login_password = (EditText) findViewById(R.id.login_password);
        final Button login_button = (Button) findViewById(R.id.login_button);
        final TextView registerlink = (TextView) findViewById(R.id.login_register);

        login_button_FB = (LoginButton) findViewById(R.id.login_button_FB);
        login_button_FB.setReadPermissions("email", "public_profile");

        callbackmanager = CallbackManager.Factory.create();
        login_button_FB.registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        getUserInfo(object);

                        FBRegisterLoginHandler(Name, ID, Age, Email, Gender, getMacAddress());
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "name, email, id, gender");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                /* if needed */
            }

            @Override
            public void onError(FacebookException error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("An Error occurred while registering/logging in with Facebook!")
                        .setNegativeButton("Retry", null)
                        .create()
                        .show();
            }
        });


        assert registerlink != null;
        registerlink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent registerintent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerintent);
            }
        });


        assert login_button != null;
        login_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                assert login_username != null;
                final String username = login_username.getText().toString();
                assert login_password != null;
                final String password = login_password.getText().toString();
                if (Objects.equals(username, ""))
                {
                    Toast.makeText(LoginActivity.this, "Enter a username!", Toast.LENGTH_SHORT).show();
                    login_username.setFocusable(true);
                    login_username.requestFocus();
                }
                else if (Objects.equals(password, ""))
                {
                    Toast.makeText(LoginActivity.this, "Enter the password!", Toast.LENGTH_SHORT).show();
                    login_password.setFocusable(true);
                    login_password.requestFocus();
                }
                else
                {
                    login_password.setText("");
                    login_username.setText("");
                    login_username.setFocusable(true);
                    login_username.requestFocus();
                    LoginHandler(username, password);
                }
            }
        });
    }

    private void FBRegisterLoginHandler(String name, final String id, Integer age, String email, String gender, String macAddress) {

        final String password = id+email+gender;

        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                JSONObject jsonResponse;

                String serverReply = null;
                try {
                    jsonResponse = new JSONObject(response);
                    serverReply = jsonResponse.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (Objects.equals(serverReply, "USER_REGISTERED")) {
                    Toast.makeText(getApplicationContext(), "Registered Successfully!", Toast.LENGTH_LONG).show();
                    LoginHandler(id, password);
                    /*Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    LoginActivity.this.startActivity(intent);*/
                }
                else {
                    if (Objects.equals(serverReply, "USERNAME_NOT_AVAILABLE")){
                        LoginHandler(id, password);
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Registration failed due to unknown reasons")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                        Toast.makeText(getApplicationContext(), "Response: "+serverReply, Toast.LENGTH_LONG).show();
                    }
                }

            }
        };
        RegisterRequest registerRequest = new RegisterRequest(name, id, age, email, password, gender, macAddress, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(registerRequest);
    }

    private void LoginHandler(String username, String password) {

        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String success = jsonResponse.getString("success");
                    Log.e("Response", "Success = "+success);
                    if (success.equals("true")){
                        String name = jsonResponse.getString("name");
                        int age = jsonResponse.getInt("age");
                        String email = jsonResponse.getString("email");
                        String username = jsonResponse.getString("username");
                        // Session Manager
                        SessionManagement session = new SessionManagement(getApplicationContext());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("age", age);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);

                        session.createLoginSession(name, username, email);

                        LoginActivity.this.startActivity(intent);
                        LoginActivity.this.finish();
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Login failed - incorrect username and/or password!")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };



        LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
        Log.e("ResponseLogin", "Sending request with = "+username+" "+password);
    }

    public void getUserInfo(JSONObject object) {
        try {
            Name = object.getString("name");
            Email = object.getString("email");
            ID = object.getString("id");
            Gender = object.getString("gender");
            //age = object.getString("birthday");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }


    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF)).append(":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

}