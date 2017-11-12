package com.fyp.mrisecondscreen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {

    public static final int RequestPermissionCode = 1;

    private static ImageView mic;
    private static TextView recordText;
    private static ProgressBar progressBar;

    private static AudioRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //get instance views
        mic = (ImageView) findViewById(R.id.mic);
        recordText = (TextView) findViewById(R.id.recordText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //tap on mic
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkPermission()) {

                    recorder = new AudioRecorder();
                    // start recoder
                    try {
                        recorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //display hidden 'recording' label
                    recordText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    // start timer for 10sec and then stop recorder
                    CountDownTimer countDowntimer = new CountDownTimer(10000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            try {
                                Toast.makeText(getApplicationContext(), "Sending audio to Server", Toast.LENGTH_LONG).show();
                                recorder.stop();
                                recordText.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);

                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }


                    };

                    countDowntimer.start();

                }
                else {
                    requestPermission();
                }

            }
        });

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}

