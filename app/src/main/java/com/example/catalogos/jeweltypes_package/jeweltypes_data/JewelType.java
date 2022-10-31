package com.example.catalogos.jeweltypes_package.jeweltypes_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.services.DataConvert;

import java.text.ParseException;
import java.util.UUID;

import static com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "Tipo de Joya"
 */
public class JewelType {
    public static final String JEWEL_TYPE_FOLDER = "images/jewelTypes";
    public static final String JEWEL_TYPE_FILE_PATH = "file://" + FOLDER_PATH + "/" + JEWEL_TYPE_FOLDER + "/";

    private String id;
    private String name;
    private String avatarUri;

    public JewelType(String name, String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.avatarUri = avatarUri;
    }

    public JewelType(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(JewelTypeEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(JewelTypeEntry.NAME));
        avatarUri = cursor.getString(cursor.getColumnIndex(JewelTypeEntry.AVATAR_URI));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(JewelTypeEntry.ID, id);
        values.put(JewelTypeEntry.NAME, name);
        values.put(JewelTypeEntry.AVATAR_URI, avatarUri);
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
