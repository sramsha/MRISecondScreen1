package com.fyp.mrisecondscreen.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.network.LoginRequest;
import com.fyp.mrisecondscreen.network.RegisterRequest;
import com.fyp.mrisecondscreen.utils.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    LoginButton login_button_FB;
    CallbackManager callbackmanager;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        user = new User(getApplicationContext());

        final EditText login_username = findViewById(R.id.login_username);
        final EditText login_password = findViewById(R.id.login_password);
        final Button login_button = findViewById(R.id.login_button);
        final TextView registerlink = findViewById(R.id.login_register);

        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        login_button_FB = findViewById(R.id.login_button_FB);

        login_button_FB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                buttonAnimation(v, 3500);
            }
        });

        login_button_FB.setReadPermissions(Arrays.asList(
                "email", "public_profile", "user_birthday", "user_relationships"));

        callbackmanager = CallbackManager.Factory.create();
        login_button_FB.registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        getUserInfo(object);
                        String accessToken = AccessToken.getCurrentAccessToken().toString();
                        Log.e("[FB]ACCESS TOKEN", accessToken);
                        user.setMAC(getMacAddress());
                        user.setPassword(user.getID() + user.getEmail() + user.getGender());
                        FBRegisterLoginHandler(user);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "name, email, id, gender, " +
                        "birthday, relationship_status");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                /* if needed */
                Toast.makeText(LoginActivity.this, "Facebook login cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("An Error occurred while logging in with Facebook!")
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
                    if (checkActiveInternetConnection())
                    {
                        /*login_password.setText("");
                        login_username.setText("");
                        login_username.setFocusable(true);
                        login_username.requestFocus();*/

                        buttonAnimation(v, 2500);
                        LoginHandler(username, password);
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.coordinatorLayout), "No internet connection!", Snackbar.LENGTH_LONG)
                                .setAction("CLOSE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                });

                        // Changing message text color
                        snackbar.setActionTextColor(Color.RED);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    }
                }
            }
        });
    }

    private void FBRegisterLoginHandler(final User user) {

        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                JSONObject jsonResponse;

                String serverReply = null;
                try {
                    Log.i("RESPONSE", "[" + response + "]");
                    jsonResponse = new JSONObject(response);
                    serverReply = jsonResponse.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (Objects.equals(serverReply, "USER_REGISTERED")) {
                    Toast.makeText(getApplicationContext(), "Registered Successfully!", Toast.LENGTH_LONG).show();
                    user.loggedInFromFacebook = true;
                    LoginHandler(user.getID(), user.getPassword());
                    /*Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    LoginActivity.this.startActivity(intent);*/
                }
                else {
                    if (Objects.equals(serverReply, "USERNAME_NOT_AVAILABLE")){
                        user.loggedInFromFacebook = true;
                        LoginHandler(user.getID(), user.getPassword());
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
        RegisterRequest registerRequest = new RegisterRequest(user, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(registerRequest);
    }

    private void LoginHandler(String ID, String password) {

        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String success = jsonResponse.getString("success");
                    Log.e("Response", "Success = "+success);
                    if (success.equals("true")){
                        user.setName(jsonResponse.getString("name"));
                        user.setEmail(jsonResponse.getString("email"));
                        user.setID(jsonResponse.getString("ID"));
                        user.setPassword(jsonResponse.getString("password"));
                        user.setGender(jsonResponse.getString("gender"));
                        user.setRelationshipStatus(jsonResponse.getString("relationshipStatus"));
                        user.setBirthday(jsonResponse.getString("birthday"));
                        user.setLocation(jsonResponse.getString("location"));
                        user.setMAC(jsonResponse.getString("MAC"));
                        user.setMobileNumber(jsonResponse.getString("mobileNumber"));
                        user.setIsProfileComplete(Boolean.parseBoolean(jsonResponse.getString("isProfileComplete")));
                        user.setCity(jsonResponse.getString("city"));
                        user.setCountry(jsonResponse.getString("country"));

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
/*                        intent.putExtra("username", ID);
                        intent.putExtra("age", birthday);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);
*/
                        user.updateSession();

                        Toast.makeText(getApplicationContext(), "Logged in succesfully!", Toast.LENGTH_LONG).show();
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


        LoginRequest loginRequest = new LoginRequest(ID, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
        Log.e("ResponseLogin", "Sending request with = " + ID + " " + password);
    }

    public void getUserInfo(JSONObject object) {
        try {
            user.setName(object.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
            user.setName(null);
        }
        try {
            user.setEmail(object.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
            user.setEmail(null);
        }
        try {
            user.setID(object.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
            user.setID(null);
        }
        try {
            user.setGender(object.getString("gender"));
        } catch (JSONException e) {
            e.printStackTrace();
            user.setGender(null);
        }
        // https://graph.facebook.com/ID?fields=name,email,gender&access_token=ACCESSTOKEN
        try {
            user.setRelationshipStatus(object.getString("relationship_status"));
        } catch (JSONException e) {
            e.printStackTrace();
            user.setRelationshipStatus(null);
        }
        try {
            user.setBirthday(object.getString("birthday"));
        } catch (JSONException e) {
            e.printStackTrace();
            user.setBirthday(null);
        }

        Log.e("[FB]BIRTHDAY", user.getBirthday() + "");
        Log.e("[FB]LOCATION", user.getLocation() + "");
        Log.e("[FB]relationship_status", user.getRelationshipStatus() + "");
        Log.e("[FB]ID", user.getID() + "");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    public boolean checkActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("Internet Error", "Error: ", e);
            }
        } else {
            Log.d("Internet Error", "No network present");
        }
        return false;
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

    private void buttonAnimation(View v, int i) {
        AlphaAnimation buttonClick = new AlphaAnimation(1.0F, 0.1F);
        buttonClick.setDuration(i);
        v.startAnimation(buttonClick);
    }

}