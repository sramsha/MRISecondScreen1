package com.fyp.mrisecondscreen.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.activity.MainActivity;
import com.fyp.mrisecondscreen.db.DatabaseHelper;
import com.fyp.mrisecondscreen.entity.BannerAd;

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
import java.util.Objects;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

@SuppressWarnings("ALL")
public class ChatHeadService extends Service {

    private WindowManager mWindowManager;
    private View mChatHeadView;
    protected long _touchStartTime;
    private static AudioRecorder recorder;
    User user;
    public static final int RequestPermissionCode = 1;
    private static final String SERVER_MATCH_URL = "http://lb-89089438.us-east-2.elb.amazonaws.com/api/clip/match";
    private static final String SERVER_MEDIA_URL = "http://lb-89089438.us-east-2.elb.amazonaws.com/admin/uploads/images/";
    private static final int AD_WATCHED_FIRST_TIME = 5;
    private static final int AD_WATCHED_ALREADY = 1;

    public ChatHeadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the chat head layout we created
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_chat_head, null);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the chat head position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);

        //Set the close button.
        ImageView closeButton = (ImageView) mChatHeadView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close the service and remove the chat head from the window
                stopSelf();
            }
        });

        user = new User(getApplicationContext());
        user.updateProfile();

        //Drag and move chat head using user's touch action.
        final ImageView chatHeadImage = mChatHeadView.findViewById(R.id.chat_head_profile_iv);
        final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    _touchStartTime = event.getEventTime();

                    //remember the initial position.
                    initialX = params.x;
                    initialY = params.y;

                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();

                    /*Log.e("Euclidean Dist", String.valueOf(Math.sqrt(Math.pow(Math.abs(initialX - initialTouchX),2)
                            + Math.pow(Math.abs(initialY - initialTouchY),2))));*/
                    //return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP && (event.getEventTime() - _touchStartTime > 1500))
                {
                    {
                        if(checkPermission()) {
                            if (checkActiveInternetConnection())
                            {
                                recorder = new AudioRecorder();
                                // start recoder
                                try {
                                    recorder.start();
                                    Toast.makeText(ChatHeadService.this, "Recording Started.......", Toast.LENGTH_LONG).show();
                                    v.startAnimation(animRotate);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // start timer for 5sec and then stop recorder
                                CountDownTimer countDowntimer = new CountDownTimer(5000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        try {
                                            //Toast.makeText(getApplicationContext(), "Sending audio to Server", Toast.LENGTH_LONG).show();
                                            recorder.stop();

                                            // send recorded clip to server via AsyncTask
                                            new GetAdContent(SERVER_MATCH_URL, recorder.getPath(), ChatHeadService.this).execute("WubbaLubbaDubDub");


                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }


                                };

                                countDowntimer.start();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            stopSelf();
                            Intent intent = new Intent(ChatHeadService.this, MainActivity.class);
                            intent.putExtra("whatIsMyPurpose", "youBringMeRecordingPermissions");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }


                    //close the service and remove the chat heads
                    //stopSelf();
                    //return true;
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    //Calculate the X and Y coordinates of the view.
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);

                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(mChatHeadView, params);
                    /*Log.e("Euclidean Dist", String.valueOf(Math.sqrt(Math.pow(Math.abs(initialX - initialTouchX),2)
                            + Math.pow(Math.abs(initialY - initialTouchY),2))));*/
                    //return true;
                }
                return true;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
    }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
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
                return false;
            }
        } else {
            Log.d("Internet Error", "No network present");
        }
        return false;
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

    private void buttonAnimation(View v, int i) {
        AlphaAnimation buttonClick = new AlphaAnimation(1.0F, 0.1F);
        buttonClick.setDuration(i);
        v.startAnimation(buttonClick);
    }

    // AsyncTask to get AdContent form server
    private class GetAdContent extends AsyncTask<String, Void, String> {
        private Context ctx;
        private String TAG = "GETADCONTENT";
        private String url;
        private String filename;

        /*
             Constructor
         */
        public GetAdContent(String url, String filename, Context ctx) {
            Log.v(TAG, "Url Passed");
            this.url = url;
            this.ctx = ctx;
            this.filename = filename;
            Log.v(TAG, "Constructor Passed");
        }

        /*
            Runs on the UI thread before doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Sending clip to the server!", Toast.LENGTH_SHORT).show();
        }


        private String readStream(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
            for (String line = r.readLine(); line != null; line = r.readLine()) {
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

            int serverResponseCode;
            String serverResponseMessage;
            Log.v(TAG, "Inside BackgroundTHread");
            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";


            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;
            File selectedFile = new File(filename);


            String[] parts = filename.split("/");
            final String fileName = parts[parts.length - 1];

            if (!selectedFile.isFile()) {

                return "Error : File doesn't exists";

            } else {
                try {
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
                    connection.setRequestProperty("uploaded_file", filename);
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
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0) {
                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
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
                } catch (Exception e) {
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
            if (result != null && !Objects.equals(result, "Server Error") && !Objects.equals(result, "File Not Found") && !Objects.equals(result, "Url Error")) {
                Log.e("/match/ Response", result);
                // Do something awesome here
                //Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

                final JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.get("found").equals("true")) {
                        //Parse json response into BannerAd
                        final BannerAd runningAd = new BannerAd(jsonObject);

                        //save offer
                        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this.ctx);
                        final long status = databaseHelper.addWithOnConflict(runningAd);
                        if (-1 != status) {
                            Toast.makeText(ctx, "Offer saved successfully with ID of : " + status, Toast.LENGTH_SHORT).show();
                            //Get offer image from server and save locally
                            String image = runningAd.getOfferImage();
                            String url = SERVER_MEDIA_URL + image;
                            String imgExtension = ImageUtil.getImageExtension(image);
                            String imageName = runningAd.getOfferBrand() + String.valueOf(runningAd.getOfferId());
                            ImageUtil.downloadImage(getApplicationContext(), url, imageName, imgExtension);

                            Toast.makeText(ctx, "Image saved with name : " + imageName, Toast.LENGTH_SHORT).show();

                            user.incrementPoints(AD_WATCHED_FIRST_TIME);
                            user.updateSession();

                        } else {
                            Toast.makeText(ctx, "Ad viewed already", Toast.LENGTH_SHORT).show();
                            user.incrementPoints(AD_WATCHED_ALREADY);
                            user.updateSession();
                        }

                        // Finally delete the sample clip
                        recorder.deleteClip();

                        Intent intent = new Intent(ChatHeadService.this, MainActivity.class);
                        intent.putExtra("whatIsMyPurpose", "youDisplayVoucherDialog");
                        intent.putExtra("offerTitle", runningAd.getOfferTitle());
                        intent.putExtra("offerContent", runningAd.getOfferContent());
                        intent.putExtra("status", status);
                        intent.putExtra("offerID", runningAd.getOfferId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //close the service and remove the chat head from the window
                        stopSelf();


                    } else {
                        Toast.makeText(ctx, "The Brand/Content not enrolled with us as yet", Toast.LENGTH_SHORT).show();
                        // Finally delete the sample clip
                        recorder.deleteClip();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                Toast.makeText(ctx, "Try again! " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}