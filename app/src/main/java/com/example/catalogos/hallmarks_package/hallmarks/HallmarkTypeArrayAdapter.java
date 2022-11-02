package com.example.catalogos.hallmarks_package.hallmarks;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.catalogos.R;

public class HallmarkTypeArrayAdapter extends ArrayAdapter {
    public HallmarkTypeArrayAdapter(@NonNull Context context, int resource){
        super (context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String hallmarkType = getItem(position).toString ();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        // Populate the data into the template view using the data object
        tvName.setText(hallmarkType);
        // Return the completed view to render on screen
        return convertView;
    }
}
