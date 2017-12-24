package com.fyp.mrisecondscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.utils.SessionManagement;
import com.google.firebase.messaging.FirebaseMessaging;

public class StartingActivity extends AppCompatActivity {

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        final SessionManagement session = new SessionManagement(getApplicationContext());
        // Subscribing to the topic for push notification
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        // if user is already logged in, no need to show him the splash screen
        if (session.isLoggedIn()) {
            Intent myIntent = new Intent(StartingActivity.this, MainActivity.class);
            StartingActivity.this.startActivity(myIntent);
            StartingActivity.this.finish();
        } else {
            logo = findViewById(R.id.logo);
            Animation myanimation = AnimationUtils.loadAnimation(this, R.anim.logotransition);
            logo.startAnimation(myanimation);

            Thread timer = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Intent myIntent = new Intent(StartingActivity.this, LoginActivity.class);
                        StartingActivity.this.startActivity(myIntent);
                        StartingActivity.this.finish();

                        // close this activity
                        finish();
                    }
                }
            };

            timer.start();
        }
    }
}
