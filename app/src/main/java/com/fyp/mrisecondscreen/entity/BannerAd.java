package com.fyp.mrisecondscreen.entity;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class BannerAd {

    private int offerId;
    private int songId;
    private String offerTitle;
    private String offerBrand;
    private String offerContent;
    private String offerLink;
    private String offerImage;

    public BannerAd(JSONObject response) {
        try {

            offerId = response.getInt("offerid");
            songId = response.getInt("song_id");
            offerTitle = response.getString("offertitle");
            offerBrand = response.getString("brand");
            offerContent = response.getString("offercontent");
            offerImage = response.getString("offerimage");
            offerLink = response.getString("offerlink");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public BannerAd(int offerId, int songId, String offerTitle, String offerBrand, String offerContent, String offerLink, String offerImage) {
        this.offerId = offerId;
        this.songId = songId;
        this.offerTitle = offerTitle;
        this.offerBrand = offerBrand;
        this.offerContent = offerContent;
        this.offerLink = offerLink;
        this.offerImage = offerImage;
    }

    public BannerAd() {
    }



    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getOfferBrand() {
        return offerBrand;
    }

    public void setOfferBrand(String offerBrand) {
        this.offerBrand = offerBrand;
    }

    public String getOfferContent() {
        return offerContent;
    }

    public void setOfferContent(String offerContent) {
        this.offerContent = offerContent;
    }

    public String getOfferLink() {
        return offerLink;
    }

    public void setOfferLink(String offerLink) {
        this.offerLink = offerLink;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
    }
}
