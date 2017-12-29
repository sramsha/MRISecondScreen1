package com.fyp.mrisecondscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.mrisecondscreen.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SupportedAds extends NavDrawerActivity {

    String[] Title;
    String[] Content;
    ListView list;
    final HashMap<String, String> Offers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supported_ads);
        list = findViewById(R.id.list);

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

        navigationView.setCheckedItem(R.id.nav_supported_ads);
    /* Code for Nav Drawer Handling */

    /* Getting Supported Ads from the api*/
        RequestQueue queue = Volley.newRequestQueue(SupportedAds.this);
        final String URL_SUPPORTED_ADS = "http://lb-89089438.us-east-2.elb.amazonaws.com/api/offers";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_SUPPORTED_ADS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonResponse;
                        String offerContent;
                        String offerTitle;
                        // response
                        Log.wtf("POST api/offers", response);
                        try {
                            jsonResponse = new JSONArray(response);
                            Title = new String[jsonResponse.length()];
                            Content = new String[jsonResponse.length()];

                            for(int i=0; i < jsonResponse.length(); i++)
                            {
                                JSONObject jsonobject = jsonResponse.getJSONObject(i);
                                offerContent  = jsonobject.getString("offercontent");
                                offerTitle = jsonobject.getString("offertitle");

                                Offers.put(offerTitle, offerContent);
                            }

                            List<HashMap<String, String>> listItems = new ArrayList<>();
                            SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), listItems, R.layout.supported_ad_list_item,
                                    new String[]{"First Line", "Second Line"},
                                    new int[]{R.id.text1, R.id.text2});


                            Iterator it = Offers.entrySet().iterator();
                            while (it.hasNext())
                            {
                                HashMap<String, String> resultsMap = new HashMap<>();
                                Map.Entry pair = (Map.Entry)it.next();
                                resultsMap.put("First Line", pair.getKey().toString());
                                resultsMap.put("Second Line", pair.getValue().toString());
                                listItems.add(resultsMap);
                            }

                            list.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("POST api/offers", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                return new HashMap<>();
            }
        };
        queue.add(postRequest);
    /* Getting Supported Ads from the api*/

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SupportedAds.this, MainActivity.class);
        startActivity(intent);
        SupportedAds.this.finish();
    }
}

