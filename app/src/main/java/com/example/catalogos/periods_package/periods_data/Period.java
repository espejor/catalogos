package com.example.catalogos.periods_package.periods_data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.catalogos.services.DataConvert;

import java.text.ParseException;
import java.util.UUID;

import static com.example.catalogos.periods_package.periods_data.PeriodsContract.PeriodEntry;
import static com.example.catalogos.services.ImageSaver.FOLDER_PATH;

/**
 * Entidad "subasta"
 */
public class Period {
    public static final String PERIOD_FILE_PATH = "file://" + FOLDER_PATH + "/images/periods/";

    private String id;
    private String name;

    private int initYear;
    private int finalYear;

    public Period(String name, int initYear, int finalYear) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.initYear = initYear;
        this.finalYear = finalYear;
    }

    public Period(Cursor cursor) throws ParseException{
        id = cursor.getString(cursor.getColumnIndex(PeriodEntry.ID));
        name = cursor.getString(cursor.getColumnIndex(PeriodEntry.NAME));
        initYear = cursor.getInt (cursor.getColumnIndex(PeriodEntry.INIT));
        finalYear = cursor.getInt (cursor.getColumnIndex(PeriodEntry.FINAL));
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(PeriodEntry.ID, id);
        values.put(PeriodEntry.NAME, name);
        values.put(PeriodEntry.INIT, initYear);
        values.put(PeriodEntry.FINAL, finalYear);
        return values;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getInitYear(){
        return initYear;
    }

    public int getFinalYear(){
        return finalYear;
    }
}
