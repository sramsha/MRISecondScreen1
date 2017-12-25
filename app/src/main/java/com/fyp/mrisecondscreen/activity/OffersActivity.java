package com.fyp.mrisecondscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.db.DatabaseHelper;
import com.fyp.mrisecondscreen.entity.BannerAd;
import com.fyp.mrisecondscreen.utils.OffersAdapter;

import java.util.ArrayList;
import java.util.List;

public class OffersActivity extends NavDrawerActivity {

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

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

        navigationView.setCheckedItem(R.id.nav_syncedads);
    /* Code for Nav Drawer Handling */

        /// Construct the data source
        ArrayList<BannerAd> arrayOfOffers = new ArrayList<BannerAd>();
        // Create the adapter to convert the array to views
        final OffersAdapter adapter = new OffersAdapter(OffersActivity.this, arrayOfOffers);
        // Attach the adapter to a ListView
        final ListView listView = (ListView) findViewById(R.id.list_offers);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {

                    case R.id.offer_cancel:
                        mode.finish(); // Action picked, so close the CAB
                        return true;

                    case R.id.offer_delete:

                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = listView.getCheckedItemPositions();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                BannerAd selecteditem = adapter.getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                int st = databaseHelper.deleteOffer(selecteditem.getOfferId());
                                Toast.makeText(getApplicationContext(), String.valueOf(selecteditem.getOfferId()), Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), "Status + "+String.valueOf(st), Toast.LENGTH_LONG).show();
                                adapter.remove(selecteditem);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        // Close CAB
                        mode.finish();
                        return true;

                    default:
                        return false;
                }
            }


            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.offer_action_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), adapter.getItem(position).getOfferContent(), Toast.LENGTH_LONG).show();
                BannerAd ad = adapter.getItem(position);
                Intent i = new Intent(OffersActivity.this, OfferDetail.class);
                // Pass all data
                i.putExtra("OfferId", ad.getOfferId());
                i.putExtra("OfferTitle", ad.getOfferTitle());
                i.putExtra("OfferBrand", ad.getOfferBrand());
                i.putExtra("OfferContent", ad.getOfferContent());
                i.putExtra("OfferImage", ad.getOfferImage());
                startActivity(i);
            }
        });

        //Load Offers from DB
        databaseHelper = DatabaseHelper.getInstance(this);
        List<BannerAd> offers = databaseHelper.getAllOffers();
        try{
            Log.e("OFFERACTIVITY !!",offers.get(0).getOfferImage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Add DB offers to adapter
        adapter.addAll(offers);



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OffersActivity.this, MainActivity.class);
        startActivity(intent);
        OffersActivity.this.finish();
    }


}
