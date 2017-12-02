package com.fyp.mrisecondscreen.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.utils.ImageUtil;
import com.squareup.picasso.Picasso;

import java.io.File;


public class OfferDetail extends AppCompatActivity {

    private ImageView mOfferImage;
    private TextView mOfferTitle;
    private TextView mOfferContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

        //Get Offer data from intent
        Intent i = getIntent();

        int id = i.getExtras().getInt("OfferId");
        Log.e("OfferId -> ", "!!!!!!"+id);
        String title = i.getExtras().getString("OfferTitle");
        String brand = i.getExtras().getString("OfferBrand");
        String content = i.getExtras().getString("OfferContent");
        String image = i.getExtras().getString("OfferImage");

        //Load image from AppLocalStorage
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        String extension = ImageUtil.getImageExtension(image);
        String imageName = brand+String.valueOf(id);
        File myImageFile = new File(directory, imageName+"."+extension);

        mOfferImage = (ImageView) findViewById(R.id.detail_offer_image);
        mOfferTitle = (TextView) findViewById(R.id.detail_offer_title);
        mOfferContent = (TextView) findViewById(R.id.detail_offer_content);

        //Update views with Offer details
        mOfferTitle.setText(title);
        mOfferContent.setText(content);

        //Put image into view via Picasso library
        Picasso.with(getApplicationContext()).load(myImageFile).into(mOfferImage);
        Bitmap bitmap = BitmapFactory.decodeFile(myImageFile.getPath());
        mOfferImage.setImageBitmap(bitmap);
    }
}
