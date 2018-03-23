package com.fyp.mrisecondscreen.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fyp.mrisecondscreen.entity.Ads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SupportedAdsDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SupportedAdsDB";
    private static final String TABLE_NAME = "Ads";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String[] COLUMNS = { KEY_ID, KEY_TITLE, KEY_CONTENT };

    public SupportedAdsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Ads ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "title TEXT, "
                + "content TEXT )";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void deleteOne(Ads supportedAds) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(supportedAds.getId()) });
        db.close();
    }

    public Ads getAds(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Ads ads = new Ads();
        ads.setId(Integer.parseInt(cursor != null ? cursor.getString(0) : null));
        ads.setTitle(cursor != null ? cursor.getString(1) : null);
        ads.setContent(cursor != null ? cursor.getString(2) : null);

        if (cursor != null) {
            cursor.close();
        }

        return ads;
    }

    public List<HashMap<String, String>> allAds() {

        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        List<HashMap<String, String>> listItems = new ArrayList<>();

        Log.e("allAds()", "Outside if");

        if (cursor.moveToFirst()) {
            Log.e("allAds()", "Inside if");
            do {

                HashMap<String, String> resultsMap = new HashMap<>();
                resultsMap.put("First Line", cursor.getString(1));
                resultsMap.put("Second Line", cursor.getString(2));
                Log.e("CURSOR DB1:", cursor.getString(1));
                Log.e("CURSOR DB2:", cursor.getString(2));
                listItems.add(resultsMap);
            } while (cursor.moveToNext());

            cursor.close();

            return listItems;
        }
        else
            return null;
    }

    public void addAd(Ads ad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, ad.getTitle());
        values.put(KEY_CONTENT, ad.getContent());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public int updateAd(Ads ad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, ad.getTitle());
        values.put(KEY_CONTENT, ad.getContent());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(ad.getId()) });

        db.close();

        return i;
    }

}