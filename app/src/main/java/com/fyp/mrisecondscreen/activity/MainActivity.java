
package com.fyp.mrisecondscreen.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.db.DatabaseHelper;
import com.fyp.mrisecondscreen.entity.BannerAd;
import com.fyp.mrisecondscreen.utils.AudioRecorder;
import com.fyp.mrisecondscreen.utils.ChatHeadService;
import com.fyp.mrisecondscreen.utils.ImageUtil;
import com.fyp.mrisecondscreen.utils.User;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

@SuppressWarnings("ALL")
public class MainActivity extends NavDrawerActivity {

    User user;
    public static final int RequestPermissionCode = 1;
    public boolean CHAT_HEAD_SERVICE_REQUESTING = false;
    private static final String SERVER_MATCH_URL = "http://lb-89089438.us-east-2.elb.amazonaws.com/api/clip/match";
    private static final String SERVER_MEDIA_URL = "http://lb-89089438.us-east-2.elb.amazonaws.com/admin/uploads/images/";
    private static TextView recordText;
    private static ProgressBar progressBar;
    private static ImageView couponPicture;
    private static ImageView mic;

    private static AudioRecorder recorder;

    SharedPreferences sharedpreferences;
    public static boolean isAppRunning;

    private static final int AD_WATCHED_FIRST_TIME = 5;
    private static final int AD_WATCHED_ALREADY = 1;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    protected void onCreate(Bundle savedInstanceState) {

        final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());

        user = new User(getApplicationContext());
        user.updateProfile();
        isAppRunning = true;

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        /* Chat Head Service */

        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }

        /* Chat Head Service */


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

        navigationView.setCheckedItem(R.id.nav_main);
    /* Code for Nav Drawer Handling */

        //get instance views
        mic = findViewById(R.id.mic);
        recordText = findViewById(R.id.recordText);
        progressBar = findViewById(R.id.progressBar);
        couponPicture = findViewById(R.id.coupon_picture);

        //tap on MicActivity
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mic.setEnabled(false);
                if(checkPermission()) {

                    if (checkActiveInternetConnection())
                    {
                        v.startAnimation(animRotate);
                        //buttonAnimation(v, 5000);
                        recorder = new AudioRecorder();
                        // start recoder
                        try {
                            recorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //display hidden 'recording' label

                        couponPicture.setVisibility(View.INVISIBLE);
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
                                    couponPicture.setVisibility(View.VISIBLE);
                                    recordText.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    mic.setEnabled(true);



                                    // send recorded clip to server via AsyncTask
                                    new GetAdContent(SERVER_MATCH_URL, recorder.getPath(), MainActivity.this).execute("WubbaLubbaDubDub");


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }


                        };

                        countDowntimer.start();
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.coordinatorLayout), "No internet connection!", Snackbar.LENGTH_LONG)
                                .setAction("CLOSE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                });

                        // Changing message text color
                        snackbar.setActionTextColor(Color.RED);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                        mic.setEnabled(true);
                    }
                }
                else {
                    requestPermission();
                    mic.setEnabled(true);
                }

            }
        });

        /* Check if MainActivity is called from ChatHeadService */

        try{
            Bundle bundle = getIntent().getExtras();
            String myPurpose = bundle.getString("whatIsMyPurpose");
            Log.e("MAIN:myPurpose", myPurpose);

            if (Objects.equals(myPurpose, "youDisplayVoucherDialog")) {
                Log.e("MAIN:", "inside my purpose");

                displayVoucherDialog(bundle.getString("offerTitle"), bundle.getString("offerContent"),
                        bundle.getLong("status"), bundle.getInt("offerID"));
            }

            else if (Objects.equals(myPurpose, "youBringMeRecordingPermissions")) {
                CHAT_HEAD_SERVICE_REQUESTING = true;
                requestPermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    /* Check if MainActivity is called from ChatHeadService */

    }


    private void doExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Session deletion
                //session.logoutUser();
                // Facebook logout
                LoginManager.getInstance().logOut();
                System.exit(0);
            }
        });

        alertDialog.setNegativeButton("No", null);

        alertDialog.setMessage("Do you want to exit the application?");
        alertDialog.setTitle("Media Icon");
        alertDialog.show();
        isAppRunning = false;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                        if (CHAT_HEAD_SERVICE_REQUESTING) {
                            CHAT_HEAD_SERVICE_REQUESTING = false;
                            startChatHeadService();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
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

    public void onBackPressed() {
        startChatHeadService();
    }

    // AsyncTask to get AdContent form server
    private class GetAdContent extends AsyncTask<String, Void, String> {
        private Context ctx;
        private String TAG = "GETADCONTENT";
        private String url;
        private ProgressDialog p;
        private String filename;

        /*
             Constructor
         */
        public GetAdContent(String url, String filename, Context ctx) {
            Log.v(TAG, "Url Passed");
            this.url = url;
            this.ctx = ctx;
            this.filename = filename;
            this.p = new ProgressDialog(ctx);
            Log.v(TAG, "Constructor Passed");
        }

        /*
            Runs on the UI thread before doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "Showing ProgressDIalog Passed");
            p.setMessage("Fetching offers and discounts for you");
            p.setIndeterminate(true);
            //p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            p.setCancelable(true);
            p.setCanceledOnTouchOutside(false);
            p.setMax(100);
            p.show();
            //Toast.makeText(getApplicationContext(), "Here to Server", Toast.LENGTH_LONG).show();
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
                    p.setProgress(10);
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
                    p.setProgress(20);
                    //creating new dataoutputstream
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());

                    //writing bytes to data outputstream
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + filename + "\"" + lineEnd);

                    dataOutputStream.writeBytes(lineEnd);

                    Log.v(TAG, "Sent file");

                    p.setProgress(50);

                    //returns no. of bytes present in fileInputStream
                    bytesAvailable = fileInputStream.available();
                    //selecting the buffer size as minimum of available bytes or 1 MB
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    p.setProgress(60);
                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0) {
                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    p.setProgress(70);
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

                    p.setProgress(100);

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
            p.dismiss();
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


                        // Make a AdDialog
                        //AdDialog adDialog = new AdDialog();

                        // Populate the AdDialog with BannerAd
                        //adDialog.showDialog(MainActivity.this, runningAd);

                        displayVoucherDialog(runningAd.getOfferTitle(), runningAd.getOfferContent(), status, runningAd.getOfferId());
                        // Finally delete the sample clip
                        recorder.deleteClip();

                    } else {
                        Toast.makeText(ctx, "The Brand/Content not enrolled with us as yet", Toast.LENGTH_SHORT).show();
                        // Finally delete the sample clip
                        recorder.deleteClip();
                    }

                    //Toast.makeText(ctx,"Offer saved successfully",Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    recorder.deleteClip();
                }

            } else {
                Toast.makeText(ctx, "Try again! " + result, Toast.LENGTH_SHORT).show();
                recorder.deleteClip();
            }
        }
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

    private void buttonAnimation(View v, int i) {
        AlphaAnimation buttonClick = new AlphaAnimation(1.0F, 0.1F);
        buttonClick.setDuration(i);
        v.startAnimation(buttonClick);
    }

    private void startChatHeadService() {
        startService(new Intent(MainActivity.this, ChatHeadService.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                startChatHeadService();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void displayVoucherDialog(final String offerTitle, final String offerContent, final long status, final int offerID) {

        Log.e("MAIN:", "inside displayVoucherDialog");
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.banner_layout);

        TextView title = dialog.findViewById(R.id.banner_title);
        title.setText(offerTitle);

        TextView text = dialog.findViewById(R.id.banner_text);
        text.setText(offerContent);

        dialog.show();

        Button laterButton = dialog.findViewById(R.id.banner_cancel);
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Offer/Voucher Redeemed!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, OffersActivity.class);
                startActivity(intent);
                dialog.dismiss();
                finish();
                // TODO: Handle further logic
            }
        });

        Button fb_share = dialog.findViewById(R.id.banner_fb_share);
        fb_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                                /*ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                                        .setContentTitle("Media Icon - Catch & Win")
                                        .setContentDescription("Ad details") // runningAd.getOfferTitle()
                                        //.setContentUrl(Uri.parse("http://mediaicon.net/index.html"))
                                        //.setImageUrl(Uri.parse("http://mediaicon.net/wp-content/uploads/2018/01/Android_5.png"))
                                        .build();
                                ShareDialog.show(MainActivity.this, shareLinkContent);*/

                CallbackManager callbackManager = CallbackManager.Factory.create();
                final ShareDialog shareDialog = new ShareDialog(MainActivity.this);

                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(getApplicationContext(), "Successfully shared", Toast.LENGTH_LONG).show();
                        Log.e("FB:SHARE", "SHARED SUCCESSFULLY!!!!!!!!!!!!!!!!!!");
                        //TODO: Give user some points
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), "Error on Sharing", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Sharing cancelled", Toast.LENGTH_LONG).show();
                    }
                });


                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setQuote("I just got voucher from " + offerTitle + ".\n" + offerContent)
                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.fyp.mrisecondscreen"))
                            .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag("#MediaIcon")
                                    .build())
                            .build();
                    shareDialog.show(linkContent);
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        final String URL_OFFERS_VIEWED = "http://lb-89089438.us-east-2.elb.amazonaws.com/api/offers/view";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_OFFERS_VIEWED,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("POST api/offers/view", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("POST api/offers/view", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateTime = sdf.format(new Date());

                Map<String, String>  params = new HashMap<String, String>();

                if (status==-1)
                    params.put("offerid", String.valueOf(offerID));
                else
                    params.put("offerid", String.valueOf(status));

                params.put("userid", user.getID());
                params.put("watched_at", currentDateTime);
                params.put("points", String.valueOf(user.getPoints()));
                Log.e("POINTS:UPDATED", String.valueOf(user.getPoints()));

                return params;
            }
        };
        queue.add(postRequest);
    }

}
