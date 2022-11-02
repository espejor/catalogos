package com.example.catalogos.jewels_hallmarks_package.jewels_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.database.mix_tables.JewelsHallmarksContract.JewelsHallmarksEntry;
import com.example.catalogos.hallmarks_package.hallmarks_data.HallmarksContract.HallmarkEntry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Entidad "joya"
 */
public class JewelsHallmarks {
    private String id;
    private int jewelId; // Clave externa
    private int hallmarkId; // Clave externa

    private ArrayList<String> hallmarks = new ArrayList<> ();

    public JewelsHallmarks(
            int jewelId, int hallmarkId) {
        this.id = UUID.randomUUID().toString();
        this.jewelId = jewelId;
        this.hallmarkId = hallmarkId;
    }

    public JewelsHallmarks(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(JewelsHallmarksEntry.ID));
        jewelId = cursor.getInt (cursor.getColumnIndex(JewelsHallmarksEntry.FK_JEWEL_ID));
        hallmarkId = cursor.getInt (cursor.getColumnIndex(JewelsHallmarksEntry.FK_HALLMARK_ID));
        hallmarks = decodeHallmarks(cursor);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(JewelsHallmarksEntry.ID, id);
        values.put(JewelsHallmarksEntry.FK_JEWEL_ID, jewelId);
        values.put(JewelsHallmarksEntry.FK_HALLMARK_ID, hallmarkId);

        return values;
    }


    private ArrayList<String> decodeHallmarks(Cursor cursor){
        ArrayList<String> list = new ArrayList<> ();
        try {
            cursor.moveToPosition (-1);
            while (cursor.moveToNext()){
                String hallmark = cursor.getString (cursor.getColumnIndex(HallmarkEntry.NAME));
                if(hallmark != null && !list.contains (hallmark))
                    list.add (hallmark);
            }
        } finally {
//            if(cursor != null && !cursor.isClosed()){
//                cursor.close();
//            }
        }
        return list;
    }


    public String getId() {
        return id;
    }

    public int getJewelId(){
        return jewelId;
    }

    public void setJewelId(int jewelId){
        this.jewelId = jewelId;
    }

    public int getHallmarkId(){
        return hallmarkId;
    }

    public void setHallmarkId(int hallmarkId){
        this.hallmarkId = hallmarkId;
    }
}
