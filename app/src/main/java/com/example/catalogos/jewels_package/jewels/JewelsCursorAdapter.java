package com.example.catalogos.jewels_package.jewels;

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
import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract;
import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract.AuctionHouseEntry;
import com.example.catalogos.auctions_package.auctionsdata.AuctionsContract;
import com.example.catalogos.auctions_package.auctionsdata.AuctionsContract.AuctionEntry;
import com.example.catalogos.cities_package.cities_data.CitiesContract;
import com.example.catalogos.cities_package.cities_data.CitiesContract.CityEntry;
import com.example.catalogos.designers_package.designers_data.DesignersContract;
import com.example.catalogos.designers_package.designers_data.DesignersContract.DesignerEntry;
import com.example.catalogos.jewels_package.jewels_data.JewelsContract.JewelEntry;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelType;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;
import com.example.catalogos.services.DataConvert;

import static com.example.catalogos.jewels_package.jewels_data.Jewel.JEWEL_FILE_PATH;

/**
 * Adaptador de joyas
 */
public class JewelsCursorAdapter extends CursorAdapter {
    public JewelsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_jewel, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Referencias UI.
//        TextView nameText = view.findViewById(R.id.tv_name);
        TextView lotText = view.findViewById(R.id.tv_lot);
        TextView auctionText = view.findViewById(R.id.tv_auction);
        TextView auctionHouseText = view.findViewById(R.id.tv_auction_house);
        TextView cityText = view.findViewById(R.id.tv_city);
        TextView dateText = view.findViewById(R.id.tv_date);
        TextView jewelTypeText = view.findViewById(R.id.tv_jewel_type);
        TextView designerText = view.findViewById(R.id.tv_designer);

        final ImageView avatarImage = view.findViewById(R.id.iv_avatar);

        // Get valores.
        String entryLot =  JewelEntry.LOT;
        String entryAuction =  AuctionEntry.NAME;
        String entryAuctionHouse =  AuctionHouseEntry.NAME;
        String entryJewelType = JewelTypeEntry.NAME;
        String entryDesigner =  DesignerEntry.NAME;
        String entryCity =  CityEntry.NAME;
        String entryDate =  AuctionEntry.DATE;

        String entryAvatar =  JewelEntry.AVATAR_URI;

        int i;
        i = cursor.getColumnIndex(entryLot);
        String lot = cursor.getString(i);
        i = cursor.getColumnIndex(entryAuction);
        String auction = cursor.getString(i);
        i = cursor.getColumnIndex(entryAuctionHouse);
        String auctionHouse = cursor.getString(i);
        i = cursor.getColumnIndex(entryJewelType);
        String jewelType = cursor.getString(i);
        i = cursor.getColumnIndex(entryDesigner);
        String designer = cursor.getString(i);
        i = cursor.getColumnIndex(entryCity);
        String city = cursor.getString(i);
        i = cursor.getColumnIndex(entryDate);
        String date = cursor.getString(i);

        i = cursor.getColumnIndex(entryAvatar);
        String avatarUri = cursor.getString(i);


        // Setup.
        lotText.setText(lot);
        auctionText.setText(auction);
        auctionHouseText.setText(auctionHouse);
        jewelTypeText.setText(jewelType);
        designerText.setText(designer);
        cityText.setText(city);
        dateText.setText(date);

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
