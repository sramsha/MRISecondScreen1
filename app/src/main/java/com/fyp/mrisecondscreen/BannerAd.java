package com.fyp.mrisecondscreen;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class BannerAd {
    private String title;
    private String brandName;
    private String adcontent;

    public BannerAd(String response) {
        JSONParse(response);
    }

    public BannerAd(String title, String brandName, String adcontent) {
        this.title = title;
        this.brandName = brandName;
        this.adcontent = adcontent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getAdcontent() {
        return adcontent;
    }

    public void setAdcontent(String adcontent) {
        this.adcontent = adcontent;
    }

    void JSONParse(String res) {
        try {
            Log.i("JSON RESPONSE : ", res);
            JSONObject jsonObject = new JSONObject(res);
            title = jsonObject.getString("song_name");
            adcontent = jsonObject.getString("adcontent");
           // brandName = jsonObject.getString("brandname");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
