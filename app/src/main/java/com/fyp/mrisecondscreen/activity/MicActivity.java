package com.fyp.mrisecondscreen.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.db.DatabaseHelper;
import com.fyp.mrisecondscreen.entity.BannerAd;
import com.fyp.mrisecondscreen.utils.AudioRecorder;
import com.fyp.mrisecondscreen.utils.ImageUtil;

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

public class MicActivity extends AppCompatActivity {

    private static final String SERVER_MATCH_URL = "http://192.168.8.100:5000/match/";
    private static final String SERVER_MEDIA_URL = "http://192.168.8.100:5000/uploads/images/";

    public static final int RequestPermissionCode = 1;
    private static TextView recordText;
    private static ProgressBar progressBar;
    private static ImageView mic;

    private static AudioRecorder recorder;

    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic);

        //get instance views
        mic = (ImageView) findViewById(R.id.mic);
        recordText = (TextView) findViewById(R.id.recordText);
        rl = findViewById(R.id.mic_relative_layout);



        //tap on MicActivity
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mic.setEnabled(false);
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
                    CountDownTimer countDowntimer = new CountDownTimer(5000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            try {
                                //Toast.makeText(getApplicationContext(), "Sending audio to Server", Toast.LENGTH_LONG).show();
                                recorder.stop();
                                recordText.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                mic.setEnabled(true);



                                // send recorded clip to server via AsyncTask
                                new GetAdContent(SERVER_MATCH_URL, recorder.getPath(), MicActivity.this).execute("WubbaLubbaDubDub");


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
                    mic.setEnabled(true);
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
            //Toast.makeText(getApplicationContext(), "Here to Server", Toast.LENGTH_LONG).show();
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
            String serverResponseMessage=null;
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
                    serverResponseMessage = readStream(is);

                    Log.v("Server Response Code", String.valueOf(serverResponseCode));

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
                    return "Server Error";
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
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
            if(result != null && result != "Server Error" && result != "File Not Found" && result != "Url Error")
            {
                Log.e("/match/ Response", result);
                // Do something awesome here
                //Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

                final JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    if(jsonObject.get("found").equals("true")) {
                        //Parse json response into BannerAd
                        final BannerAd runningAd = new BannerAd(jsonObject);

                        //save offer
                        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this.ctx);
                        long status = databaseHelper.addWithOnConflict(runningAd);
                        if (-1 != status) {
                            Toast.makeText(ctx, "Offer saved successfully with ID of : " + status, Toast.LENGTH_SHORT).show();
                            //Get offer image from server and save locally
                            String image = runningAd.getOfferImage();
                            String url = SERVER_MEDIA_URL + image;
                            String imgExtension = ImageUtil.getImageExtension(image);
                            String imageName = runningAd.getOfferBrand() + String.valueOf(runningAd.getOfferId());
                            ImageUtil.downloadImage(getApplicationContext(), url, imageName, imgExtension);

                            Toast.makeText(ctx, "Image saved with name : " + imageName, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ctx, "Ad viewed already", Toast.LENGTH_SHORT).show();
                            // TODO: Handle further logic
                        }

                        // Make a AdDialog
                        //AdDialog adDialog = new AdDialog();

                        // Populate the AdDialog with BannerAd
                        //adDialog.showDialog(MainActivity.this, runningAd);

                        final Dialog dialog = new Dialog(MicActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.banner_layout);

                        TextView title = (TextView) dialog.findViewById(R.id.banner_title);
                        title.setText(runningAd.getOfferTitle());

                        TextView text = (TextView) dialog.findViewById(R.id.banner_text);
                        text.setText(runningAd.getOfferContent());

                        dialog.show();

                        Button redeemButton = (Button) dialog.findViewById(R.id.banner_redeem);
                        redeemButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MicActivity.this, "Offer/Voucher Redeemed!", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });

                        Button laterButton = (Button) dialog.findViewById(R.id.banner_cancel);
                        laterButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MicActivity.this, "Offer/Voucher Saved!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MicActivity.this, OffersActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                                finish();

                            }
                        });
                    }
                    else {
                        Toast.makeText(ctx,"Fingerprint not in DB or too much noise",Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(ctx,"Offer saved successfully",Toast.LENGTH_SHORT).show();

                    // Finally delete the sample clip
                    recorder.deleteClip();

                } catch (JSONException e) {
                    e.printStackTrace();
                }





            }
            else
            {
                Toast.makeText(ctx,"Try again! " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MicActivity.this, new
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
                        Toast.makeText(MicActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MicActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
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
