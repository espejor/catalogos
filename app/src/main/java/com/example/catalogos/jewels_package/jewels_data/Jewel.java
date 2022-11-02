package com.example.catalogos.jewels_package.jewels_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import com.example.catalogos.designers_package.designers_data.DesignersContract.DesignerEntry;
import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import com.example.catalogos.hallmarks_package.hallmarks_data.HallmarksContract;
import com.example.catalogos.hallmarks_package.hallmarks_data.HallmarksContract.HallmarkEntry;
import com.example.catalogos.jewels_package.jewels_data.JewelsContract.JewelEntry;
import com.example.catalogos.lots_package.lots_data.LotsContract;
import com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;
import com.example.catalogos.periods_package.periods_data.PeriodsContract.PeriodEntry;
import com.example.catalogos.services.ListFeatures;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "joya"
 */
public class Jewel {
    public static final String JEWEL_FOLDER = "images/jewels";
    public static final String JEWEL_FILE_PATH = "file://" + FOLDER_PATH + "/" + JEWEL_FOLDER + "/";

    private int _id;
    private String id;
    private String name;
    private int jewelTypeId; // Clave externa
    private String jewelType;
    private int periodId; // Clave externa
    private String period;
    private int designerId; // Clave externa
    private String designer;

    private int auctionId; // Clave externa
    private String serial;
    private String obs;
    private String avatarUri;

    private ArrayList<String> owners = new ArrayList<> ();
    private ArrayList<String> hallmarks = new ArrayList<> ();
    private ArrayList<String[]> gemstonesAndCut = new ArrayList<> ();
    private ArrayList ownersKeys = new ArrayList<> ();
    private ArrayList hallmarksKeys = new ArrayList<> ();
    private ArrayList gemstonesAndCutKeys = new ArrayList<> ();
    private String lot;

    public Jewel(String name,
            int jewelTypeId, int periodId, int designerId, int auctionId, String lot,
            String serial,String obs,String avatarUri) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.jewelTypeId = jewelTypeId;
        this.periodId = periodId;
        this.designerId = designerId;
        this.auctionId = auctionId;
        this.serial = serial;
        this.lot = lot;
        this.obs = obs;
        this.avatarUri = avatarUri;
    }

    public Jewel(Cursor cursor) throws ParseException{
        _id = cursor.getInt(cursor.getColumnIndex(JewelEntry._ID));
        id = cursor.getString(cursor.getColumnIndex(JewelEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(JewelEntry.NAME));
        jewelTypeId = cursor.getInt (cursor.getColumnIndex(JewelEntry.FK_JEWEL_TYPE_ID));
        jewelType = cursor.getString (cursor.getColumnIndex(JewelTypeEntry.NAME));
        periodId = cursor.getInt (cursor.getColumnIndex(JewelEntry.FK_PERIOD_ID));
        period = cursor.getString (cursor.getColumnIndex(PeriodEntry.NAME));
        designerId = cursor.getInt (cursor.getColumnIndex(JewelEntry.FK_DESIGNER_ID));
        designer = cursor.getString (cursor.getColumnIndex(DesignerEntry.NAME));
        auctionId = cursor.getInt (cursor.getColumnIndex(JewelEntry.FK_AUCTION_ID));
//        designer = cursor.getString (cursor.getColumnIndex(DesignerEntry.NAME));
        serial = cursor.getString(cursor.getColumnIndex(JewelEntry.SERIAL));
        obs = cursor.getString(cursor.getColumnIndex(JewelEntry.OBS));
        avatarUri = cursor.getString(cursor.getColumnIndex(JewelEntry.AVATAR_URI));
        lot = cursor.getString (cursor.getColumnIndex (JewelEntry.LOT));

        owners = decodeOwners(cursor);
        hallmarks = decodeHallmarks(cursor);
        ownersKeys = decodeOwnersKeys(cursor);
        hallmarksKeys = decodeHallmarksKeys(cursor);

        gemstonesAndCut = decodeGemstones(cursor);
        gemstonesAndCutKeys = decodeGemstonesKeys(cursor);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(JewelEntry.ID, id);
        values.put(JewelEntry.NAME, name);
        values.put(JewelEntry.LOT, lot);
        values.put(JewelEntry.FK_JEWEL_TYPE_ID, jewelTypeId);
        values.put(JewelEntry.FK_AUCTION_ID, auctionId);
        values.put(JewelEntry.FK_PERIOD_ID, periodId == 0? null:periodId);
        values.put(JewelEntry.FK_DESIGNER_ID, designerId == 0? null: designerId);
        values.put(JewelEntry.SERIAL, serial);
        values.put(JewelEntry.OBS, obs);
        values.put(JewelEntry.AVATAR_URI, avatarUri);

        return values;
    }


    private ArrayList<String[]> decodeGemstones(Cursor cursor){
        ArrayList<String[]> list = new ArrayList<> ();
        try {
            cursor.moveToPosition (-1);
            while (cursor.moveToNext()) {
                String gemstone = cursor.getString (cursor.getColumnIndex(GemstoneEntry.NAME));
                String cut = cursor.getString (cursor.getColumnIndex(CutEntry.NAME));
                if(gemstone != null && !ListFeatures.listContains (list,new String [] {gemstone, cut}))
                    list.add (new String[] {gemstone, cut});
            }
        } finally {
//            if(cursor != null && !cursor.isClosed()){
//                cursor.close();
//            }
        }
        return list;
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

    private ArrayList<Integer> decodeOwnersKeys(Cursor cursor){
        ArrayList<Integer> list = new ArrayList<> ();
        try {
            cursor.moveToPosition (-1);
            while (cursor.moveToNext()){
                int ownerKey = cursor.getInt (cursor.getColumnIndex(OwnerEntry.ALIAS__ID));
                if(ownerKey != 0 && !list.contains (ownerKey))
                    list.add (ownerKey);
            }
        } finally {
//            if(cursor != null && !cursor.isClosed()){
//                cursor.close();
//            }
        }
        return list;
    }

    private ArrayList<Integer> decodeHallmarksKeys(Cursor cursor){
        ArrayList<Integer> list = new ArrayList<> ();
        try {
            cursor.moveToPosition (-1);
            while (cursor.moveToNext()){
                int hallmarkKey = cursor.getInt (cursor.getColumnIndex(HallmarkEntry.ALIAS__ID));
                if(hallmarkKey != 0 && !list.contains (hallmarkKey))
                    list.add (hallmarkKey);
            }
        } finally {
//            if(cursor != null && !cursor.isClosed()){
//                cursor.close();
//            }
        }
        return list;
    }


    private ArrayList<int[]> decodeGemstonesKeys(Cursor cursor){
        ArrayList<int[]> list = new ArrayList<> ();
        try {
            cursor.moveToPosition (-1);
            while (cursor.moveToNext()){
                int gemstoneKey = cursor.getInt (cursor.getColumnIndex(GemstoneEntry.ALIAS__ID));
                int cutKey = cursor.getInt (cursor.getColumnIndex(CutEntry.ALIAS__ID));
                if(gemstoneKey != 0 && ! ListFeatures.listContains (list, new int[]{gemstoneKey, cutKey}))
                    list.add (new int[]{gemstoneKey, cutKey});
            }
        } finally {
//            if(cursor != null && !cursor.isClosed()){
//                cursor.close();
//            }
        }
        return list;
    }
//
//    private boolean listContains(ArrayList<int[]> list, int[] element){
//        for (int i = 0; i < list.size (); i++) {
//            boolean match = false;
//
//            for (int j = 0; j < list.get (i).length; j++) {
//                if (list.get (i)[j] != (element[j])) {   // No coincide
//                    match = false;
//                    break;
//                } else {
//                    match = true;
//                }
//            }
//            if (match)
//                return true;
//        }
//        return false;
//    }
//
//
//    private boolean listContains(ArrayList<String[]> list, String[] element){
//        for (int i = 0; i < list.size (); i++) {
//            boolean match = false;
//
//            for (int j = 0; j < list.get (i).length; j++) {
//                if (list.get (i)[j] == null){
//                    if(element[j] == null)
//                        match = true;
//                    else {
//                        match = false;
//                        break;
//                    }
//                }else{
//                    if (!list.get (i)[j].equals (element[j])) {   // No coincide
//                        match = false;
//                        break;
//                    }else {
//                        match = true;
//                    }
//                }
//            }
//            if (match)
//                return true;
//        }
//        return false;
//    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getJewelTypeId(){
        return jewelTypeId;
    }

    public String getJewelType(){
        return jewelType;
    }

    public int getPeriodId(){
        return periodId;
    }

    public String getPeriod(){
        return period;
    }

    public int getDesignerId(){
        return designerId;
    }

    public String getDesigner(){
        return designer;
    }

    public String getSerial(){
        return serial;
    }

    public String getObs(){
        return obs;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri){
        this.avatarUri = avatarUri;
    }

    public ArrayList<String> getOwners(){
        return owners;
    }
    
    public ArrayList<String> getHallmarks(){
        return hallmarks;
    }

    public ArrayList getOwnersKeys(){
        return ownersKeys;
    }

    public ArrayList<String[]> getGemstonesAndCut(){
        return gemstonesAndCut;
    }

    public ArrayList getGemstonesAndCutKeys(){
        return gemstonesAndCutKeys;
    }

    public int get_id(){
        return _id;
    }

    public String getLot(){
        return lot;
    }

    public int getAuctionId(){
        return auctionId;
    }


    public ArrayList getHallmarksKeys(){
        return hallmarksKeys;
    }
}
