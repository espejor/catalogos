package com.example.catalogos.gemstones_cuts_package.gemstones_cuts_data;

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
import com.example.catalogos.cuts_package.cuts_data.CutsContract;
import com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import com.example.catalogos.database.mix_tables.JewelsGemstonesCutsContract;
import com.example.catalogos.database.mix_tables.JewelsGemstonesCutsContract.JewelsGemstonesCutsEntry;
import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import com.example.catalogos.services.DataConvert;

import static com.example.catalogos.gemstones_package.gemstones_data.Gemstone.GEMSTONE_FILE_PATH;

/**
 * Adaptador de subastas
 */
public class GemstonesCutsCursorAdapter extends CursorAdapter {
    boolean multiSelector = false;
    public GemstonesCutsCursorAdapter(Context context, Cursor c) {
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
        String entryGemstoneName =  GemstoneEntry.NAME;
        String entryCutName =  CutEntry.NAME;
//        String entryGemstoneId =  JewelsGemstonesCutsEntry.FK_GEMSTONE_ID;
//        String entryCutId =  JewelsGemstonesCutsEntry.FK_CUT_ID;
//        String entryJewelId =  JewelsGemstonesCutsEntry.FK_JEWEL_ID;

//        cursor.moveToFirst ();
        String gemstoneName = cursor.getString(cursor.getColumnIndex(entryGemstoneName));
        String cutName = cursor.getString(cursor.getColumnIndex(entryCutName));
        String text = gemstoneName + (cutName != null ? " talla " + cutName: "");
        // Referencias UI.
        if (multiSelector) {
            CheckedTextView checkedTextView = view.findViewById (android.R.id.text1);
            checkedTextView.setText (text);
        }else {
            TextView nameText = view.findViewById (R.id.tv_name);
            nameText.setText (text);
        }
    }


    public void setMultiSelector(boolean multiSelector){
        this.multiSelector = multiSelector;
    }
}
