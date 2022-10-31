package com.example.catalogos.lots_package.lots_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.auctions_package.auctionsdata.Auction;
import com.example.catalogos.jewels_package.jewels_data.Jewel;
import com.example.catalogos.lots_package.lots_data.LotsContract.LotEntry;

import java.text.ParseException;
import java.util.UUID;

import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "joya"
 */
public class Lot {
    public static final String LOT_FOLDER = "images/lots";
    public static final String LOT_FILE_PATH = "file://" + FOLDER_PATH + "/" + LOT_FOLDER + "/";
    private int jewelId;
    private String id;
    private String number;
    private int auctionId; // Clave externa
    private Jewel jewel;
    private Auction auction;

//    private ArrayList<String> owners = new ArrayList<> ();
//    private ArrayList<String[]> gemstones = new ArrayList<> ();

    public Lot(String number,
            int auctionId,
            int jewelId) {
        this.id = UUID.randomUUID().toString();
        this.number = number;
        this.auctionId = auctionId;
        this.jewelId = jewelId;
    }

    public Lot(Cursor cursor) throws ParseException{
//        id = cursor.getString(cursor.getColumnIndex(LotEntry.ID));
//        number = cursor.getString(cursor.getColumnIndex(LotEntry.NUMBER));
//        auctionId = cursor.getInt (cursor.getColumnIndex(LotEntry.FK_AUCTION_ID));
//        jewelId = cursor.getInt (cursor.getColumnIndex(LotEntry.FK_JEWEL_ID));
//        jewel = new Jewel (cursor);
//        auction = new Auction (cursor);
//        periodId = cursor.getInt (cursor.getColumnIndex(LotEntry.PERIOD_ID));
//        period = cursor.getString (cursor.getColumnIndex(PeriodEntry.NAME));
//        designerId = cursor.getInt (cursor.getColumnIndex(LotEntry.DESIGNER_ID));
//        designer = cursor.getString (cursor.getColumnIndex(DesignerEntry.NAME));
//        serial = cursor.getString(cursor.getColumnIndex(LotEntry.SERIAL));
//        obs = cursor.getString(cursor.getColumnIndex(LotEntry.OBS));
//        avatarUri = cursor.getString(cursor.getColumnIndex(LotEntry.AVATAR_URI));
//
//        owners = decodeOwners(cursor);
//        gemstones = decodeGemstones(cursor);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
//        values.put(LotEntry.ID, id);
//        values.put(LotEntry.NUMBER, number);
//        values.put(LotEntry.FK_AUCTION_ID, auctionId);
//        values.put(LotEntry.FK_JEWEL_ID, jewelId);

        return values;
    }


//    private ArrayList<String[]> decodeGemstones(Cursor cursor){
//        ArrayList<String[]> list = new ArrayList<> ();
//        try {
//            cursor.moveToPosition (-1);
//            while (cursor.moveToNext()) {
//                String gemstone = cursor.getString (cursor.getColumnIndex(GemstoneEntry.NAME));
//                String cut = cursor.getString (cursor.getColumnIndex(CutEntry.NAME));
//                if(gemstone != null && !list.contains (gemstone))
//                    list.add (new String[] {gemstone, cut});
//            }
//        } finally {
////            if(cursor != null && !cursor.isClosed()){
////                cursor.close();
////            }
//        }
//        return list;
//    }

//    private ArrayList<String> decodeOwners(Cursor cursor){
//        ArrayList<String> list = new ArrayList<> ();
//        try {
//            cursor.moveToPosition (-1);
//            while (cursor.moveToNext()){
//                String owner = cursor.getString (cursor.getColumnIndex(OwnerEntry.NAME));
//                if(owner != null && !list.contains (owner))
//                    list.add (owner);
//            }
//        } finally {
////            if(cursor != null && !cursor.isClosed()){
////                cursor.close();
////            }
//        }
//        return list;
//    }



    public String getId() {
        return id;
    }

    public static String getLotFolder(){
        return LOT_FOLDER;
    }

    public static String getLotFilePath(){
        return LOT_FILE_PATH;
    }

    public int getJewelId(){
        return jewelId;
    }

    public void setJewelId(int jewelId){
        this.jewelId = jewelId;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getNumber(){
        return number;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public int getAuctionId(){
        return auctionId;
    }

    public void setAuctionId(int auctionId){
        this.auctionId = auctionId;
    }

    public Jewel getJewel(){
        return jewel;
    }

    public void setJewel(Jewel jewel){
        this.jewel = jewel;
    }

    public Auction getAuction(){
        return auction;
    }

    public void setAuction(Auction auction){
        this.auction = auction;
    }
}
