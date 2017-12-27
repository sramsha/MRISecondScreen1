package com.fyp.mrisecondscreen.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.entity.Offers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SupportedAds extends NavDrawerActivity {

    String[] Title;
    String[] Content;
    ListView list;
    Offers offer;
    ArrayList<Offers> offers = new ArrayList<>();

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

                                /*offer = new Offers();
                                offer.setTitle(offerTitle);
                                offer.setContent(offerContent);
                                Log.e("Title", offerTitle);
                                Log.e("Content", offerContent);
                                offers.add(offer);*/
                            }

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

        /* If i use these hard coded values, it works fine */
        offer = new Offers();
        offer.setTitle("Ad1");
        offer.setContent("Advertisement #01 Description");
        offers.add(offer);

        offer = new Offers();
        offer.setTitle("Ad2");
        offer.setContent("Advertisement #02 Description");
        offers.add(offer);

        offer = new Offers();
        offer.setTitle("Ad3");
        offer.setContent("Advertisement #03 Description");
        offers.add(offer);

        offer = new Offers();
        offer.setTitle("Ad4");
        offer.setContent("Advertisement #04 Description");
        offers.add(offer);

        list.setAdapter(new MyAdapter(getApplicationContext(), offers));
        //list.deferNotifyDataSetChanged();

    }

    private class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Offers> offers;

        public MyAdapter(Context context, ArrayList<Offers> offers) {
            this.context = context;
            this.offers = offers;
        }

        @Override
        public int getCount() {
            return offers.size();
        }

        @Override
        public Object getItem(int position) {
            return offers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TwoLineListItem twoLineListItem;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                twoLineListItem = (TwoLineListItem) inflater.inflate(
                        android.R.layout.simple_list_item_2, null);
            } else {
                twoLineListItem = (TwoLineListItem) convertView;
            }

            TextView text1 = twoLineListItem.getText1();
            TextView text2 = twoLineListItem.getText2();

            text1.setText(offers.get(position).getTitle());
            text2.setText(offers.get(position).getContent());

            return twoLineListItem;
        }
    }
}

