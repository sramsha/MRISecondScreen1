package com.fyp.mrisecondscreen;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText register_name = (EditText) findViewById(R.id.register_name);
        final EditText register_username = (EditText) findViewById(R.id.register_username);
        final EditText register_age = (EditText) findViewById(R.id.register_age);
        final EditText register_email = (EditText) findViewById(R.id.register_email);
        final EditText register_password = (EditText) findViewById(R.id.register_password);
        final RadioButton register_male = (RadioButton) findViewById(R.id.register_male);
        final RadioButton register_female = (RadioButton) findViewById(R.id.register_female);
        final Button register_button = (Button) findViewById(R.id.register_button);

        register_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String name = register_name.getText().toString();
                final String username = register_username.getText().toString();
                final String age = register_age.getText().toString();
                final String email = register_email.getText().toString();
                final String password = register_password.getText().toString();
                final String gender = register_male.isChecked()? "male" : "female";
                final String mac = getMacAddress();
                //final String imei = getIMEINumber();

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
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            RegisterActivity.this.startActivity(intent);
                            RegisterActivity.this.finish();
                        }
                        else if (Objects.equals(serverReply, "USERNAME_NOT_AVAILABLE")) {
                                Toast.makeText(getApplicationContext(), "Please choose a different username as it is not available", Toast.LENGTH_LONG).show();
                            }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setMessage("Registration failed due to unknown reasons")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                            Toast.makeText(getApplicationContext(), "Server Response: "+serverReply, Toast.LENGTH_LONG).show();
                        }

                    }
                };

                if (Objects.equals(name, ""))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter your Name!", Toast.LENGTH_SHORT).show();
                    register_name.setFocusable(true);
                    register_name.requestFocus();
                }
                else if (Objects.equals(username, ""))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter your desirable Username!!", Toast.LENGTH_SHORT).show();
                    register_username.setFocusable(true);
                    register_username.requestFocus();

                }
                else if (Objects.equals(age, ""))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter your Age!", Toast.LENGTH_SHORT).show();
                    register_age.setFocusable(true);
                    register_age.requestFocus();
                }
                else if (Objects.equals(email, ""))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter your Email Address!", Toast.LENGTH_SHORT).show();
                    register_email.setFocusable(true);
                    register_email.requestFocus();
                }
                else if (Objects.equals(password, ""))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter your Password!", Toast.LENGTH_SHORT).show();
                    register_password.setFocusable(true);
                    register_password.requestFocus();
                }
                else if ( ! (register_male.isChecked() || register_female.isChecked()) )
                {
                    Toast.makeText(RegisterActivity.this, "Please select your Gender!", Toast.LENGTH_SHORT).show();
                    register_male.setFocusable(true);
                    register_male.requestFocus();
                    register_female.setFocusable(true);
                    register_female.requestFocus();
                }
                else
                {
                    RegisterRequest registerRequest = new RegisterRequest(name, username, Integer.parseInt(age), email, password, gender, mac, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(registerRequest);
                }

            }
        });

        register_male.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (register_female.isChecked())
                    register_female.setChecked(false);
            }
        });

        register_female.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (register_male.isChecked())
                    register_male.setChecked(false);
            }
        });

    }

    /*private String getIMEINumber() {
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return (tm.getImei());
    }*/

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
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
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