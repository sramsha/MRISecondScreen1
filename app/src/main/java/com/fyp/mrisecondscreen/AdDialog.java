package com.fyp.mrisecondscreen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AdDialog {

    public void showDialog(final Activity activity, BannerAd ad){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.banner_layout);

        TextView title = (TextView) dialog.findViewById(R.id.banner_title);
        title.setText(ad.getTitle());

        TextView text = (TextView) dialog.findViewById(R.id.banner_text);
        text.setText(ad.getAdcontent());

        Button redeemButton = (Button) dialog.findViewById(R.id.banner_redeem);
        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Offer/Voucher Redeemed!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        Button laterButton = (Button) dialog.findViewById(R.id.banner_cancel);
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Offer/Voucher Saved!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(dialog.getContext(), OffersActivity.class);
                dialog.dismiss();
                dialog.getContext().startActivity(intent);


            }
        });

        dialog.show();

    }
}
