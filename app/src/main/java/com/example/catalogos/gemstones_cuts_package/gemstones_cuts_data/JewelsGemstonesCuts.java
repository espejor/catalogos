package com.example.catalogos.gemstones_cuts_package.gemstones_cuts_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.catalogos.database.mix_tables.JewelsGemstonesCutsContract.JewelsGemstonesCutsEntry;

/**
 * Entidad "joya"
 */
public class JewelsGemstonesCuts {
    private String id;
    private int jewelId; // Clave externa
    private int gemstoneId; // Clave externa
    private int cutId; // Clave externa

    private ArrayList<String> gemstones = new ArrayList<> ();
    private ArrayList<String> cuts = new ArrayList<> ();

    public JewelsGemstonesCuts(
            int jewelId, int gemstoneId,int cutId) {
        this.id = UUID.randomUUID().toString();
        this.jewelId = jewelId;
        this.gemstoneId = gemstoneId;
        this.cutId = cutId;
    }

    public JewelsGemstonesCuts(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(JewelsGemstonesCutsEntry.ID));
        jewelId = cursor.getInt (cursor.getColumnIndex(JewelsGemstonesCutsEntry.FK_JEWEL_ID));
        gemstoneId = cursor.getInt (cursor.getColumnIndex(JewelsGemstonesCutsEntry.FK_GEMSTONE_ID));
        gemstones = decodeGemstones(cursor);
        cutId = cursor.getInt (cursor.getColumnIndex(JewelsGemstonesCutsEntry.FK_CUT_ID));
        cuts = decodeGemstones(cursor);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(JewelsGemstonesCutsEntry.ID, id);
        values.put(JewelsGemstonesCutsEntry.FK_JEWEL_ID, jewelId);
        values.put(JewelsGemstonesCutsEntry.FK_GEMSTONE_ID, gemstoneId);
        if(cutId != 0) values.put(JewelsGemstonesCutsEntry.FK_CUT_ID, cutId);

        return values;
    }


    private ArrayList<String> decodeGemstones(Cursor cursor){
        ArrayList<String> list = new ArrayList<> ();
        try {
            cursor.moveToPosition (-1);
            while (cursor.moveToNext()){
                String gemstone = cursor.getString (cursor.getColumnIndex(GemstoneEntry.NAME));
                if(gemstone != null && !list.contains (gemstone))
                    list.add (gemstone);
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

    public int getGemstoneId(){
        return gemstoneId;
    }

    public void setGemstoneId(int gemstoneId){
        this.gemstoneId = gemstoneId;
    }

    public int getCutId(){
        return cutId;
    }

    public void setCutId(int cutId){
        this.cutId = cutId;
    }
}
