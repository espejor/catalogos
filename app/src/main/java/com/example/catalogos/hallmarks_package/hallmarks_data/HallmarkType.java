package com.example.catalogos.hallmarks_package.hallmarks_data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.collection.ArraySet;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Set;

public class HallmarkType {
    private final Set<String> listOfHallmarksTypes;
    private final Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public HallmarkType(Context context){
        this.context = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit ();
        listOfHallmarksTypes = sharedPref.getStringSet ("listOfHallmarkTypes",new ArraySet<> ());
    }

    public void add (String value){
        update (value,value);
    }

    public String get(String key){
        return sharedPref.getString (key,"");
    }

    public void update (String key, String value){
        editor.putString (key,value);
        editor.commit ();
    }

    public void delete(String key){
        editor.remove (key);
        editor.commit ();
    }

    public Set<String> getListOfHallmarksTypes(){
        return listOfHallmarksTypes;
    }


}
