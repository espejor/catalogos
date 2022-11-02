package com.example.catalogos.hallmarks_package.hallmarks_data;

import static com.example.catalogos.hallmarks_package.hallmarks_data.HallmarksContract.HallmarkEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.ParseException;
import java.util.UUID;

/**
 * Entidad "subasta"
 */
public class Hallmark {
    public static final String HALLMARK_FOLDER = "images/hallmarks";
    public static final String HALLMARK_FILE_PATH = "file://" + FOLDER_PATH + "/" + HALLMARK_FOLDER + "/";


    private String id;
    private String name;
    private String hallmarkType;
    private String avatarUri;

    public Hallmark(String name, String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.avatarUri = avatarUri;
    }

    public Hallmark(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(HallmarkEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(HallmarkEntry.NAME));
//        hallmarkType = cursor.getString(cursor.getColumnIndex(HallmarkEntry.HALLMARK_TYPE));
        avatarUri = cursor.getString(cursor.getColumnIndex(HallmarkEntry.AVATAR_URI));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(HallmarkEntry.ID, id);
        values.put(HallmarkEntry.NAME, name);
//        values.put(HallmarkEntry.HALLMARK_TYPE, hallmarkType);
        values.put(HallmarkEntry.AVATAR_URI, avatarUri);
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

    public String getHallmarkType(){
        return hallmarkType;
    }

    public void setAvatarUri(String avatarUri){
        this.avatarUri = avatarUri;
    }
}
