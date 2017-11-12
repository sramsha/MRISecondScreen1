package com.fyp.mrisecondscreen;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

                                // send recorded clip to server via AsyncTask
                                new GetAdContent("http://192.168.8.100:5000/match/", recorder.getPath(), MainActivity.this).execute("WubbaLubbaDubDub");

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

    // AsyncTask to get AdContent form server
    private class GetAdContent extends AsyncTask<String, Void, String> {
        private Context ctx;
        private String TAG="GETADCONTENT";
        private String url;
        private ProgressDialog p;
        private String filename;

        /*
             Constructor
         */
        public GetAdContent(String url,String filename,Context ctx)
        {
            Log.v(TAG, "Url Passed");
            this.url=url;
            this.ctx=ctx;
            this.filename=filename;
            this.p=new ProgressDialog(ctx);
            Log.v(TAG, "Constructor Passed");
        }

        /*
            Runs on the UI thread before doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "Showing ProgressDIalog Passed");
            p.setMessage("Getting your Offer");
            p.setIndeterminate(false);
            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            p.setCancelable(false);
            p.show();
            Toast.makeText(getApplicationContext(), "Here to Server", Toast.LENGTH_LONG).show();
        }


        private String readStream(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
            for (String line = r.readLine(); line != null; line =r.readLine()){
                sb.append(line);
            }
            is.close();
            return sb.toString();
        }
        /*
             This method to perform a computation on a background thread.
         */
        @Override
        protected String doInBackground(String... strings) {

            int serverResponseCode = 0;
            Log.v(TAG, "Inside BackgroundTHread");
            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";


            int bytesRead,bytesAvailable,bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File selectedFile = new File(filename);


            String[] parts = filename.split("/");
            final String fileName = parts[parts.length-1];

            if (!selectedFile.isFile()){

                return "Error : File doesn't exists";

            }else{
                try{
                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    URL url = new URL(this.url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);//Allow Inputs
                    connection.setDoOutput(true);//Allow Outputs
                    connection.setUseCaches(false);//Don't use a cached Copy
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("uploaded_file",filename);

                    //creating new dataoutputstream
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());

                    //writing bytes to data outputstream
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + filename + "\"" + lineEnd);

                    dataOutputStream.writeBytes(lineEnd);

                    Log.v(TAG, "Sent file");

                    //returns no. of bytes present in fileInputStream
                    bytesAvailable = fileInputStream.available();
                    //selecting the buffer size as minimum of available bytes or 1 MB
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);

                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0){
                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer,0,bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable,maxBufferSize);
                        bytesRead = fileInputStream.read(buffer,0,bufferSize);
                    }

                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    serverResponseCode = connection.getResponseCode();
                    InputStream is = new BufferedInputStream(connection.getInputStream());
                    final String serverResponseMessage = readStream(is);

                    Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);
                    final JSONObject jobj;

                    jobj = new JSONObject(serverResponseMessage);
                    final String title = jobj.getString("song_name");
                    final String content = jobj.getString("adcontent");
                    final String time = jobj.getString("match_time");
                    final String confidence = jobj.getString("confidence");


                    //closing the input and output streams
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();

                    return serverResponseMessage;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return "File Not Found";
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "Url Error";

                } catch (IOException e) {
                    e.printStackTrace();
                    return "Cannot ReadWrite File";
                } catch (JSONException e) {
                    e.printStackTrace();
                    return  "JSON error";
                }

            }
        }

        /*
            Runs on the UI thread after doInBackground
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            p.dismiss();
            if(result != null)
            {
                // Do something awesome here
                Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

            }
            else
            {
                Toast.makeText(ctx,"Request failed, network or server issue",Toast.LENGTH_SHORT).show();
            }
        }
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

