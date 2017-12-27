package com.fyp.mrisecondscreen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class SupportedAds extends AppCompatActivity {

    String[] Title;
    String[] Content;
    int[] ID={};
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supported_ads);
        list = findViewById(R.id.list);
        final HashMap<String, String> Offers = new HashMap<>();


    /* Getting Supported Ads from the api*/
        RequestQueue queue = Volley.newRequestQueue(SupportedAds.this);
        final String URL_SUPPORTED_ADS = "http://lb-89089438.us-east-2.elb.amazonaws.com/api/offers";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_SUPPORTED_ADS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonResponse;
                        // response
                        Log.wtf("POST api/offers", response);
                        try {
                            jsonResponse = new JSONArray(response);
                            Title = new String[jsonResponse.length()];
                            Content = new String[jsonResponse.length()];
                            ID = new int[jsonResponse.length()];

                            for(int i=0; i < jsonResponse.length(); i++)
                            {
                                JSONObject jsonobject = jsonResponse.getJSONObject(i);
                                String offerContent       = jsonobject.getString("offercontent");
                                String offerTitle    = jsonobject.getString("offertitle");
                                int offerID = jsonobject.getInt("id");

                                Title[i] = offerTitle;
                                Content[i] = offerContent;
                                ID[i] = offerID;

                                Log.e("OfferContent["+i+"]", offerContent);
                                Log.e("OfferTitle["+i+"]", offerTitle);
                                Log.e("OfferID["+i+"]", String.valueOf(offerID));

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

                Map<String, String>  params = new HashMap<String, String>();

                return params;
            }
        };
        queue.add(postRequest);
    /* Getting Supported Ads from the api*/

        Offers.put(Title[0], Content[0]);
        Offers.put(Title[1], Content[1]);
        Offers.put(Title[2], Content[2]);
        Offers.put(Title[3], Content[3]);
        Offers.put(Title[4], Content[4]);
        Offers.put(Title[5], Content[5]);
        Offers.put(Title[6], Content[6]);
        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
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
    }

}
