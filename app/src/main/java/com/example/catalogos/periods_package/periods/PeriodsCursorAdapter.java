package com.example.catalogos.periods_package.periods;

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
import com.example.catalogos.periods_package.periods_data.PeriodsContract.PeriodEntry;
import com.example.catalogos.services.DataConvert;

import static com.example.catalogos.periods_package.periods_data.Period.PERIOD_FILE_PATH;

/**
 * Adaptador de subastas
 */
public class PeriodsCursorAdapter extends CursorAdapter {
    public PeriodsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_period, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Referencias UI.
        TextView nameText = view.findViewById(R.id.tv_name);
        TextView initTextField = view.findViewById(R.id.tv_init);
        TextView finalTextField = view.findViewById(R.id.tv_final);

        // Get valores.
        String entryName =  PeriodEntry.NAME;
        String entryInit =  PeriodEntry.INIT;
        String entryFinal =  PeriodEntry.FINAL;
        int i = cursor.getColumnIndex(entryName);
        String name = cursor.getString(i);
        i = cursor.getColumnIndex(entryInit);
        String initYear = cursor.getString(i);
        i = cursor.getColumnIndex(entryFinal);
        String finalYear = cursor.getString(i);

        // Setup.
        nameText.setText(name);
        initTextField.setText(initYear);
        finalTextField.setText(finalYear);
    }

}
