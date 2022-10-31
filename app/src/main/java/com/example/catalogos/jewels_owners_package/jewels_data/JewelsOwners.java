package com.example.catalogos.jewels_owners_package.jewels_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import com.example.catalogos.database.mix_tables.JewelsOwnersContract;
import com.example.catalogos.database.mix_tables.JewelsOwnersContract.JewelsOwnersEntry;
import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Entidad "joya"
 */
public class JewelsOwners {
    private String id;
    private int jewelId; // Clave externa
    private int ownerId; // Clave externa

    private ArrayList<String> owners = new ArrayList<> ();

    public JewelsOwners(
            int jewelId, int ownerId) {
        this.id = UUID.randomUUID().toString();
        this.jewelId = jewelId;
        this.ownerId = ownerId;
    }

    public JewelsOwners(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(JewelsOwnersEntry.ID));
        jewelId = cursor.getInt (cursor.getColumnIndex(JewelsOwnersEntry.FK_JEWEL_ID));
        ownerId = cursor.getInt (cursor.getColumnIndex(JewelsOwnersEntry.FK_OWNER_ID));
        owners = decodeOwners(cursor);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(JewelsOwnersEntry.ID, id);
        values.put(JewelsOwnersEntry.FK_JEWEL_ID, jewelId);
        values.put(JewelsOwnersEntry.FK_OWNER_ID, ownerId);

        return values;
    }


    private ArrayList<String> decodeOwners(Cursor cursor){
        ArrayList<String> list = new ArrayList<> ();
        try {
            cursor.moveToPosition (-1);
            while (cursor.moveToNext()){
                String owner = cursor.getString (cursor.getColumnIndex(OwnerEntry.NAME));
                if(owner != null && !list.contains (owner))
                    list.add (owner);
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

    public int getOwnerId(){
        return ownerId;
    }

    public void setOwnerId(int ownerId){
        this.ownerId = ownerId;
    }
}
