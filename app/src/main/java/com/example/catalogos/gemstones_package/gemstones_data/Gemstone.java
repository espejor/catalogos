package com.example.catalogos.gemstones_package.gemstones_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.services.DataConvert;

import java.text.ParseException;
import java.util.UUID;

import static com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "subasta"
 */
public class Gemstone {
    public static final String GEMSTONE_FOLDER = "images/gemstones";
    public static final String GEMSTONE_FILE_PATH = "file://" + FOLDER_PATH + "/" + GEMSTONE_FOLDER + "/";

    private String id;
    private String name;
    private String avatarUri;

    public Gemstone(String name, String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.avatarUri = avatarUri;
    }

    public Gemstone(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(GemstoneEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(GemstoneEntry.NAME));
        avatarUri = cursor.getString(cursor.getColumnIndex(GemstoneEntry.AVATAR_URI));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(GemstoneEntry.ID, id);
        values.put(GemstoneEntry.NAME, name);
        values.put(GemstoneEntry.AVATAR_URI, avatarUri);
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
