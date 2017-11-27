package com.fyp.mrisecondscreen.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fyp.mrisecondscreen.db.DatabaseHelper;
import com.fyp.mrisecondscreen.entity.BannerAd;
import com.fyp.mrisecondscreen.utils.OffersAdapter;
import com.fyp.mrisecondscreen.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class OffersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        // Get saved offers from DB
        // Right now, using Fake data


        /*// Make ArrayList of loaded Offers
        List<String> offersList = new ArrayList<>(Arrays.asList(offersArray));

        // ArrayAdapter to hold data for ListView
        offerAdapter = new ArrayAdapter<String>(this, R.layout.list_item_offer, R.id.list_item_offer_text, offersList);

        //Bind ListView to Adapter
        ListView offersListView = (ListView) findViewById(R.id.list_offers);
        offersListView.setAdapter(offerAdapter);*/

        // Construct the data source
        ArrayList<BannerAd> arrayOfUsers = new ArrayList<BannerAd>();
        // Create the adapter to convert the array to views
        final OffersAdapter adapter = new OffersAdapter(this, arrayOfUsers);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.list_offers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), adapter.getItem(position).getOfferContent(), Toast.LENGTH_LONG).show();
            }
        });


        //Load Offers from DB
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        List<BannerAd> offers = databaseHelper.getAllOffers();
        // Add DB offers to adapter
        adapter.addAll(offers);

        //adapter.addAll(dbOffers);
        // Or even append an entire new collection
        // Fetching some data, data has now returned
        // If data was JSON, convert to ArrayList of User objects.
        /*JSONArray jsonArray = ...;
        ArrayList<User> newUsers = User.fromJson(jsonArray)
        adapter.addAll(newUsers);*/

    }
}
