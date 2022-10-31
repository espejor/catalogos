package com.example.catalogos.designers_package.designers_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.services.DataConvert;

import java.text.ParseException;
import java.util.UUID;

import static com.example.catalogos.designers_package.designers_data.DesignersContract.DesignerEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "subasta"
 */
public class Designer {
    public static final String DESIGNER_FOLDER = "images/designers";
    public static final String DESIGNER_FILE_PATH = "file://" + FOLDER_PATH + "/" + DESIGNER_FOLDER + "/";

    private String id;
    private String name;
    private String avatarUri;

    public Designer(String name, String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.avatarUri = avatarUri;
    }

    public Designer(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(DesignerEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(DesignerEntry.NAME));
        avatarUri = cursor.getString(cursor.getColumnIndex(DesignerEntry.AVATAR_URI));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(DesignerEntry.ID, id);
        values.put(DesignerEntry.NAME, name);
        values.put(DesignerEntry.AVATAR_URI, avatarUri);
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
