package com.example.catalogos.countries_package.countries_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.services.DataConvert;

import java.text.ParseException;
import java.util.UUID;

import static com.example.catalogos.countries_package.countries_data.CountriesContract.CountryEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "país"
 */
public class Country {
    public static final String COUNTRY_FOLDER = "images/countries";
    public static final String COUNTRY_FILE_PATH = "file://" + FOLDER_PATH + "/" + COUNTRY_FOLDER + "/";

    private String id;
    private String name;
    private String avatarUri;

    public Country(String name, String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.avatarUri = avatarUri;
    }

    public Country(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(CountryEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(CountryEntry.NAME));
        avatarUri = cursor.getString(cursor.getColumnIndex(CountryEntry.AVATAR_URI));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(CountryEntry.ID, id);
        values.put(CountryEntry.NAME, name);
        values.put(CountryEntry.AVATAR_URI, avatarUri);
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
