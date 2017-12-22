package com.fyp.mrisecondscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.network.ProfileUpdateRequest;
import com.fyp.mrisecondscreen.utils.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

public class ViewProfile extends NavDrawerActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        /* Code for Nav Drawer Handling */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);
    /* Code for Nav Drawer Handling */

        user = new User(getApplicationContext());

        user.updateProfile();
        if (!user.isProfileComplete())
            Toast.makeText(ViewProfile.this, "Please complete your profile and update", Toast.LENGTH_LONG).show();

        final EditText name = findViewById(R.id.name);
        final EditText email = findViewById(R.id.email);
        final EditText ID = findViewById(R.id.ID);
        final EditText password = findViewById(R.id.password);
        final EditText gender = findViewById(R.id.gender);
        final EditText relationshipStatus = findViewById(R.id.relationshipStatus);
        final EditText birthday = findViewById(R.id.birthday);
        final EditText location = findViewById(R.id.location);
        final EditText mobileNumber = findViewById(R.id.mobileNumber);
        final EditText city = findViewById(R.id.city);
        final EditText country = findViewById(R.id.country);
        Button profileViewUpdate = findViewById(R.id.profileViewUpdate);

        name.setText(user.getName()+"", TextView.BufferType.EDITABLE);
        email.setText(user.getEmail()+"", TextView.BufferType.EDITABLE);
        ID.setText(user.getID()+"", TextView.BufferType.EDITABLE);
        password.setText(user.getPassword()+"", TextView.BufferType.EDITABLE);
        gender.setText((!Objects.equals(user.getGender(), "null")) ? user.getGender() : "", TextView.BufferType.EDITABLE);
        relationshipStatus.setText((!Objects.equals(user.getRelationshipStatus(), "null")) ? user.getRelationshipStatus() : "", TextView.BufferType.EDITABLE);
        birthday.setText((!Objects.equals(user.getBirthday(), "null")) ? user.getBirthday() : "", TextView.BufferType.EDITABLE);
        location.setText((!Objects.equals(user.getLocation(), "null")) ? user.getLocation() : "", TextView.BufferType.EDITABLE);
        mobileNumber.setText((!Objects.equals(user.getMobileNumber(), "null")) ? user.getMobileNumber() : "", TextView.BufferType.EDITABLE);
        city.setText((!Objects.equals(user.getCity(), "null")) ? user.getCity() : "", TextView.BufferType.EDITABLE);
        country.setText((!Objects.equals(user.getCountry(), "null")) ? user.getCountry() : "", TextView.BufferType.EDITABLE);

        if (user.loggedInFromFacebook)
        {
            email.setFocusable(false);
            ID.setFocusable(false);
            password.setFocusable(false);
        }

        profileViewUpdate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String nam = name.getText().toString();
                String emai = email.getText().toString();
                String id = ID.getText().toString();
                String passwor = password.getText().toString();
                String gende = gender.getText().toString();
                String relationship_status = relationshipStatus.getText().toString();
                String bday = birthday.getText().toString();
                String loc = location.getText().toString();
                String mobilenum = mobileNumber.getText().toString();
                String cit = city.getText().toString();
                String countr = country.getText().toString();

                if (Objects.equals(relationship_status, "") || !isValidRelationshipStatus(relationship_status))
                {
                    Toast.makeText(ViewProfile.this, "Please specify Single or Married", Toast.LENGTH_LONG).show();
                    relationshipStatus.setFocusable(true);
                    relationshipStatus.requestFocus();
                }
                else if (Objects.equals(bday, ""))
                {
                    Toast.makeText(ViewProfile.this, "Please enter your birthday (MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                    birthday.setFocusable(true);
                    birthday.requestFocus();
                }
                else if (Objects.equals(loc, ""))
                {
                    Toast.makeText(ViewProfile.this, "Please enter your Full Address", Toast.LENGTH_LONG).show();
                    location.setFocusable(true);
                    location.requestFocus();
                }
                else if (Objects.equals(cit, ""))
                {
                    Toast.makeText(ViewProfile.this, "Please enter your City", Toast.LENGTH_LONG).show();
                    city.setFocusable(true);
                    city.requestFocus();
                }
                else if (Objects.equals(countr, ""))
                {
                    Toast.makeText(ViewProfile.this, "Please enter your Country", Toast.LENGTH_LONG).show();
                    country.setFocusable(true);
                    country.requestFocus();
                }
                else if (Objects.equals(mobilenum, ""))
                {
                    Toast.makeText(ViewProfile.this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
                    mobileNumber.setFocusable(true);
                    mobileNumber.requestFocus();
                }
                else
                {
                    int ageValidated = validateAge(bday);

                    if (ageValidated == 1)
                    {
                        Toast.makeText(ViewProfile.this, "Incomplete Birth date (required: MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                        birthday.setFocusable(true);
                        birthday.requestFocus();
                    }
                    else if (ageValidated == 2)
                    {
                        Toast.makeText(ViewProfile.this, "Invalid birthday format (use MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                        birthday.setFocusable(true);
                        birthday.requestFocus();
                    }
                    else if (ageValidated == 3)
                    {
                        Toast.makeText(ViewProfile.this, "Incorrect value of the Month (MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                        birthday.setFocusable(true);
                        birthday.requestFocus();
                    }
                    else if (ageValidated == 4)
                    {
                        Toast.makeText(ViewProfile.this, "Incorrect value of the Day (MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                        birthday.setFocusable(true);
                        birthday.requestFocus();
                    }
                    else if (ageValidated == 5)
                    {
                        Toast.makeText(ViewProfile.this, "Incorrect value of the Year (MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                        birthday.setFocusable(true);
                        birthday.requestFocus();
                    }
                    else if (ageValidated == 6)
                    {
                        user.setName(nam);
                        user.setEmail(emai);
                        user.setID(id);
                        user.setPassword(passwor);
                        user.setGender(gende);
                        user.setRelationshipStatus(relationship_status);
                        user.setBirthday(bday);
                        user.setLocation(loc);
                        user.setMobileNumber(mobilenum);
                        user.setCity(cit);
                        user.setCountry(countr);
                        user.updateProfile();

                        /*Response Listener */
                        Response.Listener<String> responseListener = new Response.Listener<String>() {


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

                                if (Objects.equals(serverReply, "PROFILE_UPDATED")) {
                                    Toast.makeText(getApplicationContext(), "Profile updated successfully!", Toast.LENGTH_LONG).show();

                                    user.updateSession();
                                    if (user.isProfileComplete())
                                        Toast.makeText(getApplicationContext(), "Your profile is now complete!", Toast.LENGTH_LONG).show();

                                } else if (Objects.equals(serverReply, "FAILED")) {
                                    Toast.makeText(getApplicationContext(), "Your profile information is already updated!", Toast.LENGTH_LONG).show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfile.this);
                                    builder.setMessage("Profile Updation failed - unable to connect to the server!")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                    Toast.makeText(getApplicationContext(), "Server Response: " + serverReply, Toast.LENGTH_LONG).show();
                                }

                            }
                        };
                        /*Response Listener */

                        /* make profile update request*/
                        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest(user, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(ViewProfile.this);
                        queue.add(profileUpdateRequest);
                    }
                }
            }
        });

    }

    private boolean isValidRelationshipStatus(String relationship_status) {
        return relationship_status.equalsIgnoreCase("single") || relationship_status.equalsIgnoreCase("married");
    }

    public int validateAge(String age) {
        if (age.length() < 10)
            return 1;

        if ((age.charAt(2) == '/') && (age.charAt(5) == '/'))
        {
            String month = age.substring(0,2);
            String day = age.substring(3,5);
            String year = age.substring(6,10);

            if (Integer.parseInt(month) > 12 || Integer.parseInt(month) < 1)
                return 3;

            if (Integer.parseInt(day) > 31 || Integer.parseInt(day) < 1)
                return 4;

            if (Integer.parseInt(year) > Calendar.getInstance().get(Calendar.YEAR) || Integer.parseInt(year) < 1900)
                return 5;

                // Date is correct and according to the format MM/DD/YYYY
            else
                return 6;
        }

        else
            return 2;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewProfile.this, MainActivity.class);
        startActivity(intent);
        ViewProfile.this.finish();
    }
}
