package com.example.catalogos.lots_package.lots;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.catalogos.R;
import com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import com.example.catalogos.designers_package.designers_data.DesignersContract.DesignerEntry;
import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import com.example.catalogos.jewels_package.jewels_data.JewelsContract.JewelEntry;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;
import com.example.catalogos.lots_package.lots_data.LotsContract.LotEntry;
import com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;
import com.example.catalogos.periods_package.periods_data.PeriodsContract.PeriodEntry;
import com.example.catalogos.services.DataConvert;

import static com.example.catalogos.jewels_package.jewels_data.Jewel.JEWEL_FILE_PATH;

/**
 * Adaptador de joyas
 */
public class LotsCursorAdapter extends CursorAdapter {
    public LotsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_lot, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Referencias UI.
        ImageView avatarImage           = view.findViewById(R.id.iv_avatar);
        TextView countryAssayMarkText   = view.findViewById(R.id.tv_country_assay_mark);
        TextView workshopAssayMarkText  = view.findViewById(R.id.tv_workshop_assay_mark);
        TextView lotText                = view.findViewById(R.id.tv_lot);
        TextView serialText             = view.findViewById(R.id.tv_serial);
        TextView jewelTypeText          = view.findViewById(R.id.tv_jewel_type);
        TextView gemstonesCutText       = view.findViewById(R.id.tv_gemstones_cut);
        TextView designerText           = view.findViewById(R.id.tv_designer);
        TextView periodText             = view.findViewById(R.id.tv_period);
        TextView ownersText             = view.findViewById(R.id.tv_owners);
        TextView obsText                = view.findViewById(R.id.tv_obs);


        // Get valores.
        String entryLot = JewelEntry.LOT;
        String entryCountryAssayMark =  JewelEntry.COUNTRY_MARK;
        String entryWorkshopAssayMark =  JewelEntry.WORKSHOP_MARK;
        String entrySerial =  JewelEntry.SERIAL;
        String entryJewelType = JewelTypeEntry.NAME;
//        String entryGemstonesCut = decodeGemstonesCuts(GemstoneEntry.NAME, CutEntry.NAME);
//        String entryOwners = decodeOwners(OwnerEntry.NAME);
        String entryDesigner =  DesignerEntry.NAME;
        String entryPeriod = PeriodEntry.NAME;
        String entryObs =  JewelEntry.OBS;
        String entryAvatar =  JewelEntry.AVATAR_URI;


        String lot = cursor.getString(cursor.getColumnIndex(entryLot));
//        String countryAssayMark = cursor.getString(cursor.getColumnIndex(entryCountryAssayMark));
//        String workshopAssayMark = cursor.getString(cursor.getColumnIndex(entryWorkshopAssayMark));
        String serial           = cursor.getString(cursor.getColumnIndex(entrySerial));
        String jewelType        = cursor.getString(cursor.getColumnIndex(entryJewelType));
//        String gemstonesCut     = cursor.getString(cursor.getColumnIndex(entryGemstonesCut));
//        String owners           = cursor.getString(cursor.getColumnIndex(entryOwners));
        String designer         = cursor.getString(cursor.getColumnIndex(entryDesigner));
        String period           = cursor.getString(cursor.getColumnIndex(entryPeriod));
        String obs              = cursor.getString(cursor.getColumnIndex(entryObs));

        String avatarUri = cursor.getString(cursor.getColumnIndex(entryAvatar));


        // Setup.
        lotText.setText(lot);
//        countryAssayMarkText.setText(countryAssayMark);
//        workshopAssayMarkText.setText(workshopAssayMark);
        serialText      .setText(serial      );
        jewelTypeText   .setText(jewelType   );
//        gemstonesCutText.setText(gemstonesCut);
//        ownersText      .setText(owners     );
        designerText    .setText(designer      );
        periodText      .setText(period    );
        obsText         .setText(obs         );


        String filePath = JEWEL_FILE_PATH;

        if (avatarUri == null) {
            avatarImage.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            String uriStr;
            if (new DataConvert ().isUriFromMemory (avatarUri))
                uriStr = avatarUri;
            else
                uriStr = filePath + avatarUri;

            Uri uri = Uri.parse (uriStr);
            avatarImage.setImageURI (uri);
        }
    }
}
