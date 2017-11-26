package com.fyp.mrisecondscreen.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.entity.BannerAd;

import java.util.ArrayList;


public class OffersAdapter extends ArrayAdapter<BannerAd>{

    public OffersAdapter(Context context, ArrayList<BannerAd> offers) {
        super(context, 0, offers);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for current position
        BannerAd offer = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_offer, parent, false);
        }
        // Lookup view for data population
        TextView tvHeading = (TextView) convertView.findViewById(R.id.offer_heading);
        ImageView offerImage = (ImageView) convertView.findViewById(R.id.offer_image);
        // Populate the data into the template view using the data object
        tvHeading.setText(offer.getTitle());
        //offerImage.setImageResource(R.drawable.com_facebook_button_icon);
        // Return the completed view to render on screen
        return convertView;
    }
}
