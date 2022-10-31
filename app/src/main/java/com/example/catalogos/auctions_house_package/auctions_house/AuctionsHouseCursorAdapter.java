package com.example.catalogos.auctions_house_package.auctions_house;

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
import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract.AuctionHouseEntry;
import com.example.catalogos.services.DataConvert;

import static com.example.catalogos.auctions_house_package.auctions_house_data.AuctionHouse.AUCTION_HOUSE_FILE_PATH;

/**
 * Adaptador de subastas
 */
public class AuctionsHouseCursorAdapter extends CursorAdapter {
    public AuctionsHouseCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_auction_house, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Referencias UI.
        TextView nameText = (TextView) view.findViewById(R.id.tv_name);
        final ImageView avatarImage = (ImageView) view.findViewById(R.id.iv_avatar);

        // Get valores.
        String entryName =  AuctionHouseEntry.NAME;
        String entryAvatar =  AuctionHouseEntry.AVATAR_URI;
        int i = cursor.getColumnIndex(entryName);
        String name = cursor.getString(i);
        i = cursor.getColumnIndex(entryAvatar);
        String avatarUri = cursor.getString(i);
//        String avatarUri = cursor.getString(cursor.getColumnIndex(AuctionHouseEntry.AVATAR_URI));

        // Setup.
        nameText.setText(name);
//        String folderPath = App.getContext ().getFilesDir().getAbsolutePath();
        String filePath = AUCTION_HOUSE_FILE_PATH;

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
//        Glide
//                .with(context)
//                .asBitmap()
//                .load(Uri.parse(filePath + avatarUri))
//                .error(R.drawable.ic_account_circle)
//                .centerCrop()
//                .into(new BitmapImageViewTarget(avatarImage) {
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        RoundedBitmapDrawable drawable
//                                = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
//                        drawable.setCircular(true);
//                        avatarImage.setImageDrawable(drawable);
//                    }
//                });

    }

}
