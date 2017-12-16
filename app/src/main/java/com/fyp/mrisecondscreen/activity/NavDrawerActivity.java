package com.fyp.mrisecondscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.utils.User;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    User user;
    View previousView;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        /* Initialize Username and email from here since it loads the navdrawer menu*/
        user = new User(getApplicationContext());

        String name = user.getName(), email = user.getEmail();

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_main) {
            Toast.makeText(getApplicationContext(), " Sync Now", Toast.LENGTH_LONG).show();
            intent = new Intent(NavDrawerActivity.this, MainActivity.class);
            NavDrawerActivity.this.startActivity(intent);
        }

        else if (id == R.id.nav_syncedads) {
            Toast.makeText(getApplicationContext(), " Ads Viewed", Toast.LENGTH_LONG).show();
            intent = new Intent(NavDrawerActivity.this, AdsViewed.class);
            NavDrawerActivity.this.startActivity(intent);
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
            LoginManager.getInstance().logOut();
            intent = new Intent(NavDrawerActivity.this, LoginActivity.class);
            NavDrawerActivity.this.startActivity(intent);
        }
        /* This code is removed because the application lags when activity is switched along with Navdrawer closing
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        No need to animate the closing of Navdrawer when the activity is switched */
        return true;
    }
}