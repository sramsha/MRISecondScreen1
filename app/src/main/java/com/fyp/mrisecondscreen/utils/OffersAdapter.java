package com.fyp.mrisecondscreen.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.entity.BannerAd;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class OffersAdapter extends ArrayAdapter<BannerAd>{

    Context ctx;

    public OffersAdapter(Context context, ArrayList<BannerAd> offers) {
        super(context, 0, offers);
        this.ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for current position
        BannerAd offer = getItem(position);
        Log.e("Lalala", "!!!!! Image name getofferimage EEE "+getItem(position).getOfferId()+"!!!!!");
        Log.e("Lalala", "!!!!! Image name getofferimage EEE "+offer.getOfferId()+"!!!!!");
        // Fetch offer image form AppLocalData
        // TODO : move this method to ImageUtil
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        String extension = ImageUtil.getImageExtension(offer.getOfferImage());
        String imageName = offer.getOfferBrand()+String.valueOf(offer.getOfferId());
        Log.e("Lalala", "!!!!! Image name from detail view "+imageName+"!!!!!");
        File myImageFile = new File(directory, imageName+"."+extension);


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView tvHeading = (TextView) convertView.findViewById(R.id.offer_heading);
        ImageView offerImage = (ImageView) convertView.findViewById(R.id.offer_image);

        // Populate the data into the template view using the data object
        tvHeading.setText(offer.getOfferTitle());
        Log.e("Lalala", "!!!!! Going to load "+myImageFile.getPath()+"!!!!!");
        Picasso.with(ctx).load(myImageFile).into(offerImage);
        //offerImage.setImageResource(R.drawable.com_facebook_button_icon);
        // Return the completed view to render on screen
        return convertView;
    }
}
