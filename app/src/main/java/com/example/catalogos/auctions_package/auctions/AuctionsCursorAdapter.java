package com.example.catalogos.auctions_package.auctions;

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
import com.example.catalogos.auctions_package.auctionsdata.AuctionsContract.AuctionEntry;
import com.example.catalogos.cities_package.cities_data.CitiesContract;
import com.example.catalogos.services.DataConvert;

import static com.example.catalogos.auctions_package.auctionsdata.Auction.AUCTION_FILE_PATH;

/**
 * Adaptador de subastas
 */
public class AuctionsCursorAdapter extends CursorAdapter {
    public AuctionsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_auction, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Referencias UI.
        TextView nameText = view.findViewById(R.id.tv_name);
        TextView auctionHouseText = view.findViewById(R.id.tv_auction_house);
        TextView cityText = view.findViewById(R.id.tv_city);
        TextView dateText = view.findViewById(R.id.tv_date);
        final ImageView avatarImage = view.findViewById(R.id.iv_avatar);

        // Get valores.
        String entryName =  AuctionEntry.NAME;
        String entryAuctionHouse = AuctionsHouseContract.AuctionHouseEntry.NAME;
        String entryCity =  CitiesContract.CityEntry.NAME;
        String entryDate =  AuctionEntry.DATE;
        String entryAvatar =  AuctionEntry.AVATAR_URI;
        int i = cursor.getColumnIndex(entryName);
        String name = cursor.getString(i);
        i = cursor.getColumnIndex(entryAuctionHouse);
        String auctionHouse = cursor.getString(i);
        i = cursor.getColumnIndex(entryCity);
        String city = cursor.getString(i);
        i = cursor.getColumnIndex(entryDate);
        String date = cursor.getString(i);
        i = cursor.getColumnIndex(entryAvatar);
        String avatarUri = cursor.getString(i);


        // Setup.
        nameText.setText(name);
        auctionHouseText.setText(auctionHouse);
        cityText.setText(city);
        dateText.setText(date);

        String filePath = AUCTION_FILE_PATH;

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
