package com.example.catalogos.hallmarks_package.hallmarks;

import static com.example.catalogos.hallmarks_package.hallmarks_data.Hallmark.HALLMARK_FILE_PATH;

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
import com.example.catalogos.hallmarks_package.hallmarks_data.HallmarksContract.HallmarkEntry;
import com.example.catalogos.services.DataConvert;

/**
 * Adaptador de subastas
 */
public class HallmarksCursorAdapter extends CursorAdapter {
    boolean multiSelector = false;
    public HallmarksCursorAdapter(Context context, Cursor c) {
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
        String entryName =  HallmarkEntry.NAME;
        String entryAvatar =  HallmarkEntry.AVATAR_URI;

//        cursor.moveToFirst ();
        int i = cursor.getColumnIndex(entryName);
        String name = cursor.getString(i);

        // Referencias UI.
        if (multiSelector) {
            CheckedTextView checkedTextView = view.findViewById (android.R.id.text1);
            checkedTextView.setText (name);
        }else{
            TextView nameText = view.findViewById(R.id.tv_name);
            final ImageView avatarImage = view.findViewById(R.id.iv_avatar);

            i = cursor.getColumnIndex(entryAvatar);
            String avatarUri = cursor.getString(i);
//        String avatarUri = cursor.getString(cursor.getColumnIndex(HallmarkEntry.AVATAR_URI));

            // Setup.
            nameText.setText(name);
//        String folderPath = App.getContext ().getFilesDir().getAbsolutePath();
            String filePath = HALLMARK_FILE_PATH;

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

    public void setMultiSelector(boolean multiSelector){
        this.multiSelector = multiSelector;
    }
}
