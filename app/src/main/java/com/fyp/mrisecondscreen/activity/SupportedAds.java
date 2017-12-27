package com.fyp.mrisecondscreen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.Map;

public class SupportedAds extends AppCompatActivity {

    String[] Title={};
    String[] Content={};
    int[] ID={};
    ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supported_ads);
        list = findViewById(R.id.list);
        arrayList = new ArrayList<String>();

        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2, arrayList);

        // Here, you set the data in your ListView
        list.setAdapter(adapter);

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

                                // this line adds the data of your EditText and puts in your array
                                arrayList.add(Title[i]);
                                arrayList.add(Content[i]);
                                // next thing you have to do is check if your adapter has changed
                                adapter.notifyDataSetChanged();
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
    }

}
