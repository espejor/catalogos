package com.example.catalogos.cities_package.cities_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.auctions_package.auctionsdata.AuctionsContract;
import com.example.catalogos.countries_package.countries_data.CountriesContract;
import com.example.catalogos.services.DataConvert;

import java.text.ParseException;
import java.util.UUID;

import static com.example.catalogos.cities_package.cities_data.CitiesContract.CityEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "subasta"
 */
public class City {
    public static final String CITY_FOLDER = "images/cities";
    public static final String CITY_FILE_PATH = "file://" + FOLDER_PATH + "/" + CITY_FOLDER + "/";

    private String country;

    private String id;
    private String name;
    private int countryId; // Clave externa
    private String avatarUri;

    public City(String name, int countryId, String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.countryId = countryId;
        this.avatarUri = avatarUri;
    }

    public City(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(CityEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(CityEntry.NAME));
        countryId = cursor.getInt(cursor.getColumnIndex(CityEntry.FK_COUNTRY_ID));
        country = cursor.getString (cursor.getColumnIndex(CountriesContract.CountryEntry.NAME));
        avatarUri = cursor.getString(cursor.getColumnIndex(CityEntry.AVATAR_URI));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(CityEntry.ID, id);
        values.put(CityEntry.NAME, name);
        values.put(CityEntry.FK_COUNTRY_ID, countryId);
        values.put(CityEntry.AVATAR_URI, avatarUri);
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

    public String getCountry(){
        return country;
    }
    public int getCountryId(){
        return countryId;
    }
}
