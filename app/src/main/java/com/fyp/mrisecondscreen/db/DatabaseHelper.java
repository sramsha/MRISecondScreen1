package com.fyp.mrisecondscreen.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.fyp.mrisecondscreen.entity.BannerAd;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "ssDatabase";
    private static final int DATABASE_VERSION = 3;

    // Table Names
    private static final String TABLE_OFFERS = "offers";
    //private static final String TABLE_USERS = "users";

    // Post Table Columns
    private static final String COLUMN_OFFER_ID = "id";
    private static final String COLUMN_OFFER_SID = "sid";
    private static final String COLUMN_OFFER_TITLE = "offertitle";
    private static final String COLUMN_OFFER_BRAND = "offerbrand";
    private static final String COLUMN_OFFER_CONTENT = "offercontent";
    private static final String COLUMN_OFFER_IMAGE = "offerimage";
    private static final String COLUMN_OFFER_LINK = "offerlink";

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_OFFERS_TABLE = "CREATE TABLE " + TABLE_OFFERS +
                "(" +
                COLUMN_OFFER_ID + " INTEGER unique," + // Define a unique key
                COLUMN_OFFER_SID + " INTEGER," + // Define a foreign key(currently not a FK)
                COLUMN_OFFER_TITLE + " TEXT," +
                COLUMN_OFFER_BRAND + " TEXT," +
                COLUMN_OFFER_CONTENT + " TEXT," +
                COLUMN_OFFER_IMAGE + " TEXT," +
                COLUMN_OFFER_LINK + " TEXT" +
                ")";

        db.execSQL(CREATE_OFFERS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFERS);
            onCreate(db);
        }
    }

    // Insert a offer into the database | this code does not check for already existing offer
    /*
    public void addOffer(BannerAd offer) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            //long userId = addOrUpdateUser(post.user);

            ContentValues values = new ContentValues();
            values.put(COLUMN_OFFER_ID, offer.getOfferId());
            values.put(COLUMN_OFFER_SID, offer.getSongId());
            values.put(COLUMN_OFFER_BRAND, offer.getOfferBrand());
            values.put(COLUMN_OFFER_TITLE, offer.getOfferTitle());
            values.put(COLUMN_OFFER_CONTENT, offer.getOfferContent());
            values.put(COLUMN_OFFER_IMAGE, offer.getOfferImage());
            values.put(COLUMN_OFFER_LINK, offer.getOfferLink());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            //db.insertOrThrow(TABLE_OFFERS, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }
    */

    // Insert or update offer in the database
    // Since SQLite doesn't support "upsert" we need to write custom logic (in case the
    // offer already exists) optionally followed by an INSERT (in case the offer does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long addWithOnConflict(BannerAd offer) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long offerId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_OFFER_ID, offer.getOfferId());
            values.put(COLUMN_OFFER_SID, offer.getSongId());
            values.put(COLUMN_OFFER_BRAND, offer.getOfferBrand());
            values.put(COLUMN_OFFER_TITLE, offer.getOfferTitle());
            values.put(COLUMN_OFFER_CONTENT, offer.getOfferContent());
            values.put(COLUMN_OFFER_IMAGE, offer.getOfferImage());
            values.put(COLUMN_OFFER_LINK, offer.getOfferLink());


                String usersSelectQuery = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_OFFERS, COLUMN_OFFER_ID);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(offer.getOfferId())});
                try {
                    if (cursor.moveToFirst()) {
                        Log.e(TAG, "!!!!!Offer already in DB with ID of " + cursor.getString(cursor.getColumnIndex(COLUMN_OFFER_TITLE)));

                    }
                    else {

                        // Offer not in DB, so add this offer
                        offerId = db.insertOrThrow(TABLE_OFFERS, null, values);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return offerId;
    }

    // Get all offers in the database
    public List<BannerAd> getAllOffers() {
        List<BannerAd> offers = new ArrayList<>();

        // SELECT * FROM OFFERS

        String OFFERS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_OFFERS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(OFFERS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    BannerAd newOffer= new BannerAd();
                    newOffer.setOfferId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_OFFER_ID))));
                    newOffer.setSongId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_OFFER_SID))));
                    newOffer.setOfferBrand(cursor.getString(cursor.getColumnIndex(COLUMN_OFFER_BRAND)));
                    newOffer.setOfferTitle(cursor.getString(cursor.getColumnIndex(COLUMN_OFFER_TITLE)));
                    newOffer.setOfferContent(cursor.getString(cursor.getColumnIndex(COLUMN_OFFER_CONTENT)));
                    newOffer.setOfferImage(cursor.getString(cursor.getColumnIndex(COLUMN_OFFER_IMAGE)));
                    newOffer.setOfferLink(cursor.getString(cursor.getColumnIndex(COLUMN_OFFER_LINK)));

                    offers.add(newOffer);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get offers from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return offers;
    }

    // Delete all posts and users in the database
    public int deleteOffer(int id) {
        int st=0;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            st=db.delete(TABLE_OFFERS, COLUMN_OFFER_ID+" = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete offer with id : " + id);
        } finally {
            db.endTransaction();
        }
        return st;
    }


    /*
    // Update the user's profile picture url
    public int updateUserProfilePicture(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_PROFILE_PICTURE_URL, user.profilePictureUrl);

        // Updating profile picture url for user with that userName
        return db.update(TABLE_USERS, values, KEY_USER_NAME + " = ?",
                new String[] { String.valueOf(user.userName) });
    }

    // Delete all posts and users in the database
    public void deleteAllPostsAndUsers() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_POSTS, null, null);
            db.delete(TABLE_USERS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }
    */
}
