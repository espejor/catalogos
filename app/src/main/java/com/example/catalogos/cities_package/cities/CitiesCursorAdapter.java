package com.example.catalogos.cities_package.cities;

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
import com.example.catalogos.cities_package.cities_data.CitiesContract.CityEntry;
import com.example.catalogos.services.DataConvert;

import static com.example.catalogos.cities_package.cities_data.City.CITY_FILE_PATH;

/**
 * Adaptador de subastas
 */
public class CitiesCursorAdapter extends CursorAdapter {
    public CitiesCursorAdapter(Context context, Cursor c) {
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
        TextView nameText = view.findViewById(R.id.tv_name);
        final ImageView avatarImage = view.findViewById(R.id.iv_avatar);

        // Get valores.
        String entryName =  CityEntry.NAME;
        String entryAvatar =  CityEntry.AVATAR_URI;
        int i = cursor.getColumnIndex(entryName);
        String name = cursor.getString(i);
        i = cursor.getColumnIndex(entryAvatar);
        String avatarUri = cursor.getString(i);
//        String avatarUri = cursor.getString(cursor.getColumnIndex(CityEntry.AVATAR_URI));

        // Setup.
        nameText.setText(name);
//        String folderPath = App.getContext ().getFilesDir().getAbsolutePath();
        String filePath = CITY_FILE_PATH;

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
