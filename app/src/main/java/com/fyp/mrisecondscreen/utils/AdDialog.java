package com.fyp.mrisecondscreen.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.activity.OffersActivity;
import com.fyp.mrisecondscreen.entity.BannerAd;

public class AdDialog {



    public void showDialog(final Activity activity, BannerAd ad){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.banner_layout);

        TextView title = (TextView) dialog.findViewById(R.id.banner_title);
        title.setText(ad.getOfferTitle());

        TextView text = (TextView) dialog.findViewById(R.id.banner_text);
        text.setText(ad.getOfferContent());

        Button laterButton = (Button) dialog.findViewById(R.id.banner_cancel);
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Offer/Voucher Saved!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(dialog.getContext(), OffersActivity.class);
                dialog.getContext().startActivity(intent);
                dialog.dismiss();


            }
        });

        dialog.show();

    }
}
