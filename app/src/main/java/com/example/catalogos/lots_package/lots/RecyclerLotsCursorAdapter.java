package com.example.catalogos.lots_package.lots;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.catalogos.R;
import com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.jewels_package.jewels_data.JewelsContract.JewelEntry;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;
import com.example.catalogos.recycler_package.CursorRecyclerViewAdapter;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.ListFeatures;

import java.util.ArrayList;

import static com.example.catalogos.designers_package.designers_data.DesignersContract.DesignerEntry;
import static com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import static com.example.catalogos.jewels_package.jewels_data.Jewel.JEWEL_FILE_PATH;
import static com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;
import static com.example.catalogos.periods_package.periods_data.PeriodsContract.PeriodEntry;

class RecyclerLotsCursorAdapter extends CursorRecyclerViewAdapter<RecyclerLotsCursorAdapter.ViewHolder> {

    private Context context;
    int auctionId;

    private Cursor currentCursor = null;
    private String currentLot;
    private String owners;
    private String owner;
    private boolean GEMSTONES_FETCHED;
    private String gemstoneAndCut;

    private DbHelper dbHelper ;

    public Cursor getCurrentCursor(){
        return currentCursor;
    }

    public RecyclerLotsCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatarImage             ;
        public TextView countryAssayMarkText     ;
        public TextView workshopAssayMarkText    ;
        public TextView lotText                  ;
        public TextView serialText               ;
        public TextView jewelTypeText            ;
        public TextView gemstonesCutText         ;
        public TextView designerText             ;
        public TextView periodText               ;
        public TextView ownersText               ;
        public TextView obsText                  ;

        public ViewHolder(View view) {
            super(view);
            avatarImage             = view.findViewById(R.id.iv_avatar);
            countryAssayMarkText    = view.findViewById(R.id.tv_country_assay_mark);
            workshopAssayMarkText   = view.findViewById(R.id.tv_workshop_assay_mark);
            lotText                 = view.findViewById(R.id.tv_lot);
            serialText              = view.findViewById(R.id.tv_serial);
            jewelTypeText           = view.findViewById(R.id.tv_jewel_type);
            gemstonesCutText        = view.findViewById(R.id.tv_gemstones_cut);
            designerText            = view.findViewById(R.id.tv_designer);
            periodText              = view.findViewById(R.id.tv_period);
            ownersText              = view.findViewById(R.id.tv_owners);
            obsText                 = view.findViewById(R.id.tv_obs);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_lot, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        currentCursor = cursor;
        dbHelper = new DbHelper (context);

//        String lot = JewelEntry.LOT;
        currentLot = cursor.getString (cursor.getColumnIndex (JewelEntry.LOT));

        String entryJewelId = JewelEntry.ID;
        String entryAuctionId = JewelEntry.FK_AUCTION_ID;

        String entryGemstone = GemstoneEntry.NAME;
        String entryCut = CutEntry.NAME;
        String entryOwner = OwnerEntry.NAME;

        String entryLotNumber = JewelEntry.LOT;
        String entryCountryAssayMark = JewelEntry.COUNTRY_MARK;
        String entryWorkshopAssayMark = JewelEntry.WORKSHOP_MARK;
        String entrySerial = JewelEntry.SERIAL;
        String entryJewelType = JewelTypeEntry.NAME;
        String entryDesigner = DesignerEntry.NAME;
        String entryPeriod = PeriodEntry.NAME;
        String entryObs = JewelEntry.OBS;
        String entryAvatar = JewelEntry.AVATAR_URI;

        String id = cursor.getString (cursor.getColumnIndex (entryJewelId));
        auctionId = cursor.getInt (cursor.getColumnIndex (entryAuctionId));

        String lot = cursor.getString (cursor.getColumnIndex (entryLotNumber));
//        String countryAssayMark = cursor.getString(cursor.getColumnIndex(entryCountryAssayMark));
//        String workshopAssayMark = cursor.getString(cursor.getColumnIndex(entryWorkshopAssayMark));
        String serial = cursor.getString (cursor.getColumnIndex (entrySerial));
        String jewelType = cursor.getString (cursor.getColumnIndex (entryJewelType));
        String designer = cursor.getString (cursor.getColumnIndex (entryDesigner));
        String period = cursor.getString (cursor.getColumnIndex (entryPeriod));
        String obs = cursor.getString (cursor.getColumnIndex (entryObs));

        String avatarUri = cursor.getString (cursor.getColumnIndex (entryAvatar));

        // Setup.
        viewHolder.lotText.setText (lot);
//        viewHolder.countryAssayMarkText.setText(countryAssayMark);
//        viewHolder.workshopAssayMarkText.setText(workshopAssayMark);
        viewHolder.serialText.setText (serial);
        viewHolder.jewelTypeText.setText (jewelType);
        viewHolder.designerText.setText (designer);
        viewHolder.periodText.setText (period);
        viewHolder.obsText.setText (obs);


        String filePath = JEWEL_FILE_PATH;

        if (avatarUri == null) {
            viewHolder.avatarImage.setImageResource (R.drawable.ic_baseline_error_24);
        } else {
            String uriStr;
            if (new DataConvert ().isUriFromMemory (avatarUri))
                uriStr = avatarUri;
            else
                uriStr = filePath + avatarUri;

            Uri uri = Uri.parse (uriStr);
            viewHolder.avatarImage.setImageURI (uri);
        }

        // -------- Rellenamos los campos múltiples
        owners = getMultipleDataFromLot (id)[0];
        gemstoneAndCut = getMultipleDataFromLot (id)[1];

//        String currentOwner = cursor.getString(cursor.getColumnIndex(entryOwner));
////        while (cursor == currentCursor && cursor.getPosition () < cursor.getCount ()) {   // reiteramos sobre la misma joya
////            owner = cursor.getString(cursor.getColumnIndex(entryOwner));
////            owners += owner;
//            if(currentOwner.equals (owner) && !GEMSTONES_FETCHED){
//                gemstoneAndCut += cursor.getString(cursor.getColumnIndex(entryGemstone)) + " talla " +
//                    cursor.getString(cursor.getColumnIndex(entryCut));
////                owner = cursor.getString(cursor.getColumnIndex(entryOwner));
//            }
//            GEMSTONES_FETCHED = true;
////            owners += owner;
////        }
        viewHolder.gemstonesCutText.setText(gemstoneAndCut);
        viewHolder.ownersText.setText(owners);
    }

    private String[] getMultipleDataFromLot(String id){
        Cursor cursor = dbHelper.getJewelByLot (auctionId, currentLot, false);

        ArrayList<String> listOfOwners = new ArrayList<> ();
        ArrayList<String[]> listOfGemstonesAndCuts = new ArrayList<> ();
        StringBuilder owners = new StringBuilder ();
        StringBuilder gemstonesAndCuts = new StringBuilder ();

        try {
            cursor.moveToPosition (-1);
            while (cursor.moveToNext()){
                String jewelId = cursor.getString (cursor.getColumnIndex(JewelEntry.ID));
                if (jewelId.equals (id)) {
                    String owner = cursor.getString (cursor.getColumnIndex (OwnerEntry.NAME));
                    String gemstone = cursor.getString (cursor.getColumnIndex (GemstoneEntry.NAME));
                    String cut = cursor.getString (cursor.getColumnIndex (CutEntry.NAME));
                    if (owner != null && ! listOfOwners.contains (owner)) {
                        listOfOwners.add (owner);
                        if (cursor.getPosition () > 0)
                            owners.append ("\n");
                        owners.append (owner);
                    }
                    String[] element = new String[]{gemstone, cut};
                    if (gemstone != null && ! ListFeatures.listContains (listOfGemstonesAndCuts, element)) {
                        listOfGemstonesAndCuts.add (element);
                        if (cursor.getPosition () > 0)
                            gemstonesAndCuts.append ("\n");
                        gemstonesAndCuts.append (gemstone);
                        if (cut != null)
                            gemstonesAndCuts.append (" talla ").append (cut);
                    }
                }
            }
        } finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }

        return new String[]{String.valueOf (owners), String.valueOf (gemstonesAndCuts)};
    }

}