package com.example.catalogos.owners_package.owners_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.services.DataConvert;

import java.text.ParseException;
import java.util.UUID;

import static com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "subasta"
 */
public class Owner {
    public static final String OWNER_FOLDER = "images/owners";
    public static final String OWNER_FILE_PATH = "file://" + FOLDER_PATH + "/" + OWNER_FOLDER + "/";


    private String id;
    private String name;
    private String avatarUri;

    public Owner(String name, String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.avatarUri = avatarUri;
    }

    public Owner(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(OwnerEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(OwnerEntry.NAME));
        avatarUri = cursor.getString(cursor.getColumnIndex(OwnerEntry.AVATAR_URI));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(OwnerEntry.ID, id);
        values.put(OwnerEntry.NAME, name);
        values.put(OwnerEntry.AVATAR_URI, avatarUri);
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
