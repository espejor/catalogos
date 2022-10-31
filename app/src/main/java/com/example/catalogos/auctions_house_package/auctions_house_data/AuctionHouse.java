package com.example.catalogos.auctions_house_package.auctions_house_data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import static com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract.AuctionHouseEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

import java.text.ParseException;
import java.util.UUID;

/**
 * Entidad "subasta"
 */
public class AuctionHouse {
    public static final String AUCTION_HOUSE_FOLDER = "images/auctions_house";
    public static final String AUCTION_HOUSE_FILE_PATH = "file://" + FOLDER_PATH + "/" + AUCTION_HOUSE_FOLDER + "/";

    private String id;
    private String name;
    private String avatarUri;

    public AuctionHouse(String name,String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.avatarUri = avatarUri;
    }

    @SuppressLint("Range")
    public AuctionHouse(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(AuctionHouseEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(AuctionHouseEntry.NAME));
        avatarUri = cursor.getString(cursor.getColumnIndex(AuctionHouseEntry.AVATAR_URI));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(AuctionHouseEntry.ID, id);
        values.put(AuctionHouseEntry.NAME, name);
        values.put(AuctionHouseEntry.AVATAR_URI, avatarUri);
        return values;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUri() {
        return avatarUri;
    }


    public void setAvatarUri(String avatarUri){
        this.avatarUri = avatarUri;
    }
}
