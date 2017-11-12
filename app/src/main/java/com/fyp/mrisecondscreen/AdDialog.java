package com.fyp.mrisecondscreen;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class AdDialog {

    public void showDialog(Activity activity, BannerAd ad){
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
                dialog.dismiss();
            }
        });

        Button cancelButton = (Button) dialog.findViewById(R.id.banner_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
