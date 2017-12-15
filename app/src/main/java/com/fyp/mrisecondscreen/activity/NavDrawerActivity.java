package com.fyp.mrisecondscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.utils.User;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_navigation_drawer);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        /* Initialize Username and email from here since it loads the navdrawer menu*/
        String name, email;

        user = new User(getApplicationContext());
        name = user.getName();
        email = user.getEmail();

        if (name == null)
            name = "Username";

        if (email == null)
            email = "UserEmail@android.com";

        TextView username = findViewById(R.id.nav_username);
        TextView useremail = findViewById(R.id.nav_useremail);

        username.setText(name);
        useremail.setText(email);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    View previousView;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        View addedView = null;

        RelativeLayout container = (RelativeLayout) findViewById(R.id.content_frame);
        LayoutInflater inflater = getLayoutInflater();

        RelativeLayout rl = findViewById(R.id.mic_relative_layout);

        container.removeView(previousView);

        if (id == R.id.nav_main) {
            Toast.makeText(getApplicationContext(), " Sync Now", Toast.LENGTH_LONG).show();
            addedView = inflater.inflate(R.layout.activity_mic, null);
            container.addView(addedView);
        }

        else if (id == R.id.nav_syncedads) {
            Toast.makeText(getApplicationContext(), " Ads Viewed", Toast.LENGTH_LONG).show();
            addedView = inflater.inflate(R.layout.activity_ads_viewed2, null);
            container.addView(addedView);
            rl.setVisibility(View.INVISIBLE);
        }

        else if (id == R.id.nav_coupons) {
            Toast.makeText(getApplicationContext(), "Start Coupons Activity", Toast.LENGTH_LONG).show();
        }

        else if (id == R.id.nav_manage) {
            Toast.makeText(getApplicationContext(), "Profile Activity", Toast.LENGTH_LONG).show();
        }

        else if (id == R.id.nav_logout) {
            Toast.makeText(getApplicationContext(), "Logging out", Toast.LENGTH_SHORT).show();
            user.logout();
            Toast.makeText(getApplicationContext(), "Redirect to login Activity", Toast.LENGTH_LONG).show();
            Intent logout = new Intent(NavDrawerActivity.this, LoginActivity.class);
            NavDrawerActivity.this.startActivity(logout);
        }

        previousView = addedView;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}