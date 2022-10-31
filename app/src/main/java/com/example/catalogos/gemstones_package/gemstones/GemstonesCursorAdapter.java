package com.example.catalogos.gemstones_package.gemstones;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.catalogos.R;
import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import com.example.catalogos.owners_package.owners_data.OwnersContract;
import com.example.catalogos.services.DataConvert;

import static com.example.catalogos.gemstones_package.gemstones_data.Gemstone.GEMSTONE_FILE_PATH;

/**
 * Adaptador de subastas
 */
public class GemstonesCursorAdapter extends CursorAdapter {
    boolean multiSelector = false;
    public GemstonesCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(multiSelector)
            return inflater.inflate(android.R.layout.simple_list_item_multiple_choice, viewGroup, false);
        return inflater.inflate(R.layout.list_item_auction_house, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Get valores.
        String entryName =  GemstoneEntry.NAME;
        String entryAvatar =  GemstoneEntry.AVATAR_URI;

//        cursor.moveToFirst ();
        int i = cursor.getColumnIndex(entryName);
        String name = cursor.getString(i);
        // Referencias UI.
        if (multiSelector) {
            CheckedTextView checkedTextView = view.findViewById (android.R.id.text1);
            checkedTextView.setText (name);
        }else {
            TextView nameText = view.findViewById (R.id.tv_name);
            final ImageView avatarImage = view.findViewById (R.id.iv_avatar);

            // Get valores.
//        String entryName =  GemstoneEntry.NAME;
//        String entryAvatar =  GemstoneEntry.AVATAR_URI;
//            int i = cursor.getColumnIndex (entryName);
//        String name = cursor.getString(i);
            i = cursor.getColumnIndex (entryAvatar);
            String avatarUri = cursor.getString (i);
//        String avatarUri = cursor.getString(cursor.getColumnIndex(GemstoneEntry.AVATAR_URI));

            // Setup.
            nameText.setText (name);
//        String folderPath = App.getContext ().getFilesDir().getAbsolutePath();
            String filePath = GEMSTONE_FILE_PATH;

            if (avatarUri == null) {
                avatarImage.setImageResource (R.drawable.ic_baseline_error_24);
            } else {
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


    public void setMultiSelector(boolean multiSelector){
        this.multiSelector = multiSelector;
    }
}
