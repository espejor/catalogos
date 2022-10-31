package com.example.catalogos.auctions_package.auctionsdata;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract;
import com.example.catalogos.auctions_package.auctionsdata.AuctionsContract.AuctionEntry;
import com.example.catalogos.cities_package.cities_data.CitiesContract;
import com.example.catalogos.services.DataConvert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "subasta"
 */
public class Auction {
    public static final String AUCTION_FOLDER = "images/auctions";
    public static final String AUCTION_FILE_PATH = "file://" + FOLDER_PATH + "/" + AUCTION_FOLDER + "/";
    private int _id;
    private String id;
    private String name;
    private int auctionHouseId; // Clave externa
    private String auctionHouse;
    private int cityId; // Clave externa
    private String city;
    private Date date;
    private String avatarUri;

    private static SimpleDateFormat formatUS = DataConvert.formatUS;

    public Auction(String name,
                   int auctionHouseId, int cityId,
                   Date date, String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.auctionHouseId = auctionHouseId;
        this.cityId = cityId;
        formatUS.applyPattern ("yyyy-MM-dd");
        this.date = date;
        this.avatarUri = avatarUri;
    }

    public Auction(Cursor cursor) throws ParseException{
        _id = cursor.getInt (cursor.getColumnIndex (AuctionEntry._ID));
        id = cursor.getString(cursor.getColumnIndex(AuctionEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(AuctionEntry.NAME));
        auctionHouseId = cursor.getInt (cursor.getColumnIndex(AuctionEntry.FK_AUCTION_HOUSE_ID));
        auctionHouse = cursor.getString (cursor.getColumnIndex(AuctionsHouseContract.AuctionHouseEntry.NAME));
        cityId = cursor.getInt(cursor.getColumnIndex(AuctionEntry.FK_CITY_ID));
        city = cursor.getString (cursor.getColumnIndex(CitiesContract.CityEntry.NAME));
        date = DataConvert.formatUS.parse (cursor.getString(cursor.getColumnIndex(AuctionEntry.DATE)));
        avatarUri = cursor.getString(cursor.getColumnIndex(AuctionEntry.AVATAR_URI));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(AuctionEntry.ID, id);
        values.put(AuctionEntry.NAME, name);
        values.put(AuctionEntry.FK_AUCTION_HOUSE_ID, auctionHouseId);
        values.put(AuctionEntry.FK_CITY_ID, cityId);
        values.put(AuctionEntry.DATE, String.format ("%1$tY-%1$tm-%1$td",date));
        values.put(AuctionEntry.AVATAR_URI, avatarUri);
        return values;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getAuctionHouse() {
        return auctionHouse;
    }

    public int getAuctionHouseId() {
        return auctionHouseId;
    }

    public int getCityId() {
        return cityId;
    }

    public Date getDate() {
        return date;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri){
        this.avatarUri = avatarUri;
    }

    public int get_id(){
        return _id;
    }
}
