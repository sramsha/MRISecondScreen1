package com.fyp.mrisecondscreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        SessionManagement session = new SessionManagement(getApplicationContext());

        if (session.isLoggedIn())
        {
            Intent myIntent = new Intent(StartingActivity.this, MainActivity.class);
            StartingActivity.this.startActivity(myIntent);
        }
        else
        {
            Intent myIntent = new Intent(StartingActivity.this, LoginActivity.class);
            StartingActivity.this.startActivity(myIntent);
        }

        // close this activity
        finish();
    }
}
