package com.example.catalogos.cuts_package.cuts_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.services.DataConvert;

import java.text.ParseException;
import java.util.UUID;

import static com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "subasta"
 */
public class Cut {
    public static final String CUT_FOLDER = "images/cuts";
    public static final String CUT_FILE_PATH = "file://" + FOLDER_PATH + "/" + CUT_FOLDER + "/";

    private String id;
    private String name;
    private String avatarUri;

    public Cut(String name, String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.avatarUri = avatarUri;
    }

    public Cut(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(CutEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(CutEntry.NAME));
        avatarUri = cursor.getString(cursor.getColumnIndex(CutEntry.AVATAR_URI));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(CutEntry.ID, id);
        values.put(CutEntry.NAME, name);
        values.put(CutEntry.AVATAR_URI, avatarUri);
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
