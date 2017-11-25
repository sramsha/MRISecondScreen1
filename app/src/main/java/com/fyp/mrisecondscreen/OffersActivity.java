package com.fyp.mrisecondscreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OffersActivity extends AppCompatActivity {

    ArrayAdapter<String> offerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        // Get saved offers from DB
        // Right now, using Fake data
        String[] offersArray = {
                "CocaCola - 1000 Rs Credit",
                "Nurpur - Discount Voucher worth Rs 500",
                "Exide - Free Battery Offer",
                "Sprite - Participate to win Rs 1,000,000",
                "Shan - Food Challenge worth 5,000,000"
        };

        // Make ArrayList of loaded Offers
        List<String> offersList = new ArrayList<>(Arrays.asList(offersArray));

        // ArrayAdapter to hold data for ListView
        offerAdapter = new ArrayAdapter<String>(this, R.layout.list_item_offer, R.id.list_item_offer_text, offersList);

        //Bind ListView to Adapter
        ListView offersListView = (ListView) findViewById(R.id.list_offers);
        offersListView.setAdapter(offerAdapter);

    }
}
