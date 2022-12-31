package com.example.catalogos.database;

import static android.provider.BaseColumns._ID;
import static com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract.AuctionHouseEntry;
import static com.example.catalogos.auctions_package.auctionsdata.AuctionsContract.AuctionEntry;
import static com.example.catalogos.countries_package.countries_data.CountriesContract.CountryEntry;
import static com.example.catalogos.database.mix_tables.JewelsHallmarksContract.JewelsHallmarksEntry;
import static com.example.catalogos.designers_package.designers_data.DesignersContract.DesignerEntry;
import static com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_AUCTION_HOUSE;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_CITY;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_COUNTRY;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_CUT;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_DATE_FROM;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_DATE_TO;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_DESIGNER;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_GEMSTONE;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_HALLMARK;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_JEWEL_TYPE;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_OBS;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_OWNER;
import static com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment.BUNDLE_DATA_PERIOD;
import static com.example.catalogos.jewels_package.jewels_data.JewelsContract.JewelEntry;
import static com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;
import static com.example.catalogos.periods_package.periods_data.PeriodsContract.PeriodEntry;
import static com.example.catalogos.services.DataConvert.formatUS;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;

import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionHouse;
import com.example.catalogos.auctions_package.auctionsdata.Auction;
import com.example.catalogos.cities_package.cities_data.CitiesContract.CityEntry;
import com.example.catalogos.cities_package.cities_data.City;
import com.example.catalogos.countries_package.countries_data.Country;
import com.example.catalogos.cuts_package.cuts_data.Cut;
import com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import com.example.catalogos.database.mix_tables.JewelsGemstonesCutsContract.JewelsGemstonesCutsEntry;
import com.example.catalogos.database.mix_tables.JewelsOwnersContract.JewelsOwnersEntry;
import com.example.catalogos.designers_package.designers_data.Designer;
import com.example.catalogos.gemstones_cuts_package.gemstones_cuts_data.JewelsGemstonesCuts;
import com.example.catalogos.gemstones_package.gemstones_data.Gemstone;
import com.example.catalogos.hallmarks_package.hallmarks_data.Hallmark;
import com.example.catalogos.hallmarks_package.hallmarks_data.HallmarksContract.HallmarkEntry;
import com.example.catalogos.jewels_hallmarks_package.jewels_data.JewelsHallmarks;
import com.example.catalogos.jewels_owners_package.jewels_data.JewelsOwners;
import com.example.catalogos.jewels_package.jewels_data.Jewel;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelType;
import com.example.catalogos.owners_package.owners_data.Owner;
import com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;
import com.example.catalogos.periods_package.periods_data.Period;

import java.util.ArrayList;
import java.util.Date;

/**
 * Manejador de la base de datos
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 38;
    public static final String DATABASE_NAME = "catalogue.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            }
        }
    }


    // ------------------------------------------
    // ------------- Tables ----------------------
    // ------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creación de la tabla de SUBASTAS
        db.execSQL("CREATE TABLE IF NOT EXISTS " + AuctionEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AuctionEntry.ID + " TEXT NOT NULL,"
                + AuctionEntry.NAME + " TEXT NOT NULL,"
                + AuctionEntry.FK_AUCTION_HOUSE_ID + " INTEGER,"
                + AuctionEntry.FK_CITY_ID + " INTEGER,"
                + AuctionEntry.DATE + " TEXT NOT NULL,"
                + AuctionEntry.AVATAR_URI + " TEXT,"
                + "FOREIGN KEY (" + AuctionEntry.FK_AUCTION_HOUSE_ID + ") REFERENCES " + AuctionHouseEntry.TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + AuctionEntry.FK_CITY_ID + ") REFERENCES " + CityEntry.TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "UNIQUE (" + AuctionEntry.ID + "))");

        // Creación de la tabla de JOYAS
        db.execSQL("CREATE TABLE IF NOT EXISTS " + JewelEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + JewelEntry.ID + " TEXT NOT NULL,"
                + JewelEntry.NAME + " TEXT,"
                + JewelEntry.SERIAL + " TEXT,"
                + JewelEntry.LOT + " TEXT,"
                + JewelEntry.FK_JEWEL_TYPE_ID + " INTEGER NOT NULL,"
                + JewelEntry.FK_DESIGNER_ID + " INTEGER,"
                + JewelEntry.FK_PERIOD_ID + " INTEGER,"
                + JewelEntry.FK_AUCTION_ID + " INTEGER,"
                + JewelEntry.OBS + " TEXT,"
                + JewelEntry.AVATAR_URI + " TEXT,"
                + "FOREIGN KEY (" + JewelEntry.FK_JEWEL_TYPE_ID + ") REFERENCES " + JewelTypeEntry.TABLE_NAME + "(" + JewelTypeEntry._ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + JewelEntry.FK_PERIOD_ID + ") REFERENCES " + PeriodEntry.TABLE_NAME + "(" + PeriodEntry._ID + ") ON DELETE SET NULL ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + JewelEntry.FK_DESIGNER_ID + ") REFERENCES " + DesignerEntry.TABLE_NAME + "(" + DesignerEntry._ID + ") ON DELETE SET NULL ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + JewelEntry.FK_AUCTION_ID + ") REFERENCES " + AuctionEntry.TABLE_NAME + "(" + AuctionEntry._ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "UNIQUE (" + JewelEntry.ID + "))");

        // Creación de la tabla de CASA DE SUBASTAS
        db.execSQL("CREATE TABLE IF NOT EXISTS " + AuctionHouseEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AuctionHouseEntry.ID + " TEXT NOT NULL,"
                + AuctionHouseEntry.NAME + " TEXT NOT NULL,"
                + AuctionHouseEntry.AVATAR_URI + " TEXT,"
                + "UNIQUE (" + AuctionHouseEntry.ID + "))");

        // Creación de la tabla de PAISES
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CountryEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CountryEntry.ID + " TEXT NOT NULL,"
                + CountryEntry.NAME + " TEXT NOT NULL,"
                + CountryEntry.AVATAR_URI + " TEXT,"
                + "UNIQUE (" + CountryEntry.ID + "))");


        // Creación de la tabla de CIUDADES
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CityEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CityEntry.ID + " TEXT NOT NULL,"
                + CityEntry.NAME + " TEXT NOT NULL,"
                + CityEntry.FK_COUNTRY_ID + " INTEGER,"
                + CityEntry.AVATAR_URI + " TEXT,"
                + "FOREIGN KEY (" + CityEntry.FK_COUNTRY_ID + ") REFERENCES " + CountryEntry.TABLE_NAME + "(" + CountryEntry._ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "UNIQUE (" + CityEntry.ID + "))");


        // Creación de la tabla de TIPO DE JOYA
        db.execSQL("CREATE TABLE IF NOT EXISTS " + JewelTypeEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + JewelTypeEntry.ID + " TEXT NOT NULL,"
                + JewelTypeEntry.NAME + " TEXT NOT NULL,"
                + JewelTypeEntry.AVATAR_URI + " TEXT,"
                + "UNIQUE (" + JewelTypeEntry.ID + "))");

        // Creación de la tabla de DISEÑADOR
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DesignerEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DesignerEntry.ID + " TEXT NOT NULL,"
                + DesignerEntry.NAME + " TEXT NOT NULL,"
                + DesignerEntry.AVATAR_URI + " TEXT,"
                + "UNIQUE (" + DesignerEntry.ID + "))");


        // Creación de la tabla de TIPO DE CORTE
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CutEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CutEntry.ID + " TEXT NOT NULL,"
                + CutEntry.NAME + " TEXT NOT NULL,"
                + CutEntry.AVATAR_URI + " TEXT,"
                + "UNIQUE (" + CutEntry.ID + "))");

        // Creación de la tabla de PROPIETARIO
        db.execSQL("CREATE TABLE IF NOT EXISTS " + OwnerEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + OwnerEntry.ID + " TEXT NOT NULL,"
                + OwnerEntry.NAME + " TEXT NOT NULL,"
                + OwnerEntry.AVATAR_URI + " TEXT,"
                + "UNIQUE (" + OwnerEntry.ID + "))");

        // Creación de la tabla de SELLOS
        db.execSQL("CREATE TABLE IF NOT EXISTS " + HallmarkEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HallmarkEntry.ID + " TEXT NOT NULL,"
                + HallmarkEntry.NAME + " TEXT NOT NULL,"
                + HallmarkEntry.AVATAR_URI + " TEXT,"
                + "UNIQUE (" + HallmarkEntry.ID + "))");


        // Creación de la tabla de TIPO DE PIEDRA PRECIOSA
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GemstoneEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + GemstoneEntry.ID + " TEXT NOT NULL,"
                + GemstoneEntry.NAME + " TEXT NOT NULL,"
                + GemstoneEntry.NULL_FIELD + " TEXT,"
                + GemstoneEntry.NULL_ID + " INTEGER,"
                + GemstoneEntry.AVATAR_URI + " TEXT,"
                + "UNIQUE (" + GemstoneEntry.ID + "))");

        // Creación de la tabla de PERIODO HISTÓRICO
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PeriodEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PeriodEntry.ID + " TEXT NOT NULL,"
                + PeriodEntry.NAME + " TEXT NOT NULL,"
                + PeriodEntry.INIT + " INTEGER,"
                + PeriodEntry.FINAL + " INTEGER,"
                + "UNIQUE (" + PeriodEntry.ID + "))");



        // ------------------------------------------
        // ------------- TABLAS VARIOS-VARIOS -------
        // ------------------------------------------

        // Creación de la tabla de PROPIETARIOS DE JOYAS Y VICEVERSA
        db.execSQL ("CREATE TABLE IF NOT EXISTS " + JewelsOwnersEntry.TABLE_NAME+ " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + JewelsOwnersEntry.ID + " TEXT NOT NULL,"
                + JewelsOwnersEntry.FK_JEWEL_ID + " INTEGER,"
                + JewelsOwnersEntry.FK_OWNER_ID + " INTEGER,"

                + "FOREIGN KEY (" + JewelsOwnersEntry.FK_OWNER_ID + ") REFERENCES " + OwnerEntry.TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + JewelsOwnersEntry.FK_JEWEL_ID + ") REFERENCES " + JewelEntry.TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"

                + "UNIQUE (" + JewelsOwnersEntry.ID + "))");


        // Creación de la tabla de Sellos y Joyas
        db.execSQL ("CREATE TABLE IF NOT EXISTS " + JewelsHallmarksEntry.TABLE_NAME+ " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + JewelsHallmarksEntry.ID + " TEXT NOT NULL,"
                + JewelsHallmarksEntry.FK_JEWEL_ID + " INTEGER,"
                + JewelsHallmarksEntry.FK_HALLMARK_ID + " INTEGER,"

                + "FOREIGN KEY (" + JewelsHallmarksEntry.FK_HALLMARK_ID + ") REFERENCES " + HallmarkEntry.TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + JewelsHallmarksEntry.FK_JEWEL_ID + ") REFERENCES " + JewelEntry.TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"

                + "UNIQUE (" + JewelsHallmarksEntry.ID + "))");


        // Creación de la tabla de JOYAS, TIPOS DE GEMAS Y CORTES
        db.execSQL ("CREATE TABLE IF NOT EXISTS " + JewelsGemstonesCutsEntry.TABLE_NAME+ " ("
                + JewelsGemstonesCutsEntry.ID + " TEXT,"
                + JewelsGemstonesCutsEntry.FK_JEWEL_ID + " INTEGER,"
                + JewelsGemstonesCutsEntry.FK_GEMSTONE_ID + " INTEGER,"
                + JewelsGemstonesCutsEntry.FK_CUT_ID + " INTEGER,"

                + "FOREIGN KEY (" + JewelsGemstonesCutsEntry.FK_CUT_ID + ") REFERENCES " + CutEntry.TABLE_NAME + "(" + _ID + ") ON DELETE SET NULL ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + JewelsGemstonesCutsEntry.FK_GEMSTONE_ID + ") REFERENCES " + GemstoneEntry.TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + JewelsGemstonesCutsEntry.FK_JEWEL_ID + ") REFERENCES " + JewelEntry.TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE ON UPDATE CASCADE)");

//        // Creación de la tabla de lOTES
//        db.execSQL ("CREATE TABLE IF NOT EXISTS " + LotEntry.TABLE_NAME+ " ("
//                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + LotEntry.ID + " TEXT NOT NULL,"
//                + LotEntry.FK_JEWEL_ID + " INTEGER,"
//                + LotEntry.FK_AUCTION_ID + " INTEGER,"
//                + LotEntry.NUMBER + " TEXT,"
//
//                + "FOREIGN KEY (" + LotEntry.FK_AUCTION_ID + ") REFERENCES " + AuctionEntry.TABLE_NAME + "(" + _ID + ") ON UPDATE CASCADE,"
//                + "FOREIGN KEY (" + LotEntry.FK_JEWEL_ID + ") REFERENCES " + JewelEntry.TABLE_NAME + "(" + _ID + ") ON UPDATE CASCADE,"
//
//                + "UNIQUE (" + LotEntry.ID + "))");
//


        // ------------------------------------------
        // ------------- Views ----------------------
        // ------------------------------------------

        // Creación de la vista de Ciudades

        db.execSQL ("CREATE VIEW IF NOT EXISTS " + CityEntry.VIEW_NAME
                + " AS SELECT "
                + CityEntry.ALTER__ID +","
                + CityEntry.ALTER_ID +","
                + CityEntry.NAME + ","
                + CityEntry.FK_COUNTRY_ID + ","
                + CountryEntry.NAME + ","
                + CityEntry.ALTER_AVATAR_URI
                + " FROM " + CityEntry.TABLE_NAME
                + " INNER JOIN " + CountryEntry.TABLE_NAME + " ON "
                + CityEntry.FK_COUNTRY_ID + "=" + CountryEntry.ALTER__ID
        );


        // Creación de la vista de SUBASTAS
        db.execSQL ("CREATE VIEW IF NOT EXISTS " + AuctionEntry.VIEW_NAME
                + " AS SELECT "
                + AuctionEntry.ALTER__ID +","
                + AuctionEntry.ALTER_ID +","
                + AuctionEntry.NAME + ","
                + AuctionEntry.DATE + ","
                + AuctionEntry.FK_CITY_ID + ","
                + AuctionEntry.FK_AUCTION_HOUSE_ID + ","
                + AuctionHouseEntry.NAME + ","
                + CityEntry.NAME + ","
                + CountryEntry.NAME + ","
                + AuctionEntry.ALTER_AVATAR_URI
                + " FROM " + AuctionEntry.TABLE_NAME
                + " INNER JOIN " + AuctionHouseEntry.TABLE_NAME + " ON "
                + AuctionEntry.FK_AUCTION_HOUSE_ID + "=" + AuctionHouseEntry.ALTER__ID
                + " INNER JOIN " + CityEntry.VIEW_NAME + " ON "
                + AuctionEntry.FK_CITY_ID + "=" + CityEntry.ALTER_VIEW__ID
        );

        // Creación de la vista de JOYAS
        db.execSQL ("CREATE VIEW IF NOT EXISTS " + JewelEntry.VIEW_NAME
                + " AS SELECT "
                + JewelEntry.ALTER__ID +","
                + JewelEntry.ALTER_ID +","
                + JewelEntry.NAME + ","
                + JewelEntry.SERIAL + ","
                + JewelEntry.LOT + ","
                + JewelEntry.OBS + ","

                + JewelEntry.FK_JEWEL_TYPE_ID + ","
                + JewelEntry.FK_PERIOD_ID + ","
                + JewelEntry.FK_DESIGNER_ID + ","
                + JewelEntry.FK_AUCTION_ID + ","

                + JewelTypeEntry.NAME + ","
                + PeriodEntry.NAME + ","
                + DesignerEntry.NAME + ","

                + JewelEntry.ALTER_AVATAR_URI

                + " FROM " + JewelEntry.TABLE_NAME

                + " INNER JOIN " + AuctionEntry.TABLE_NAME + " ON "
                + JewelEntry.FK_AUCTION_ID + "=" + AuctionEntry.ALTER__ID

                + " INNER JOIN " + JewelTypeEntry.TABLE_NAME + " ON "
                + JewelEntry.FK_JEWEL_TYPE_ID + "=" + JewelTypeEntry.ALTER__ID

                + " LEFT JOIN " + PeriodEntry.TABLE_NAME + " ON "
                + JewelEntry.FK_PERIOD_ID + "=" + PeriodEntry.ALTER__ID

                + " LEFT JOIN " + DesignerEntry.TABLE_NAME + " ON "
                + JewelEntry.FK_DESIGNER_ID + "=" + DesignerEntry.ALTER__ID
        );

        // Creación de la vista de LISTA DE JOYAS PARA UNA LISTA
        db.execSQL ("CREATE VIEW " + JewelEntry.JEWEL_LIST_VIEW
                + " AS SELECT "
                + JewelEntry.ALTER__ID +","
                + JewelEntry.ALTER_ID +","
                + JewelEntry.NAME + ","
                + JewelTypeEntry.NAME + ","
                + DesignerEntry.NAME + ","

                + JewelEntry.ALTER_AVATAR_URI

                + " FROM " + JewelEntry.TABLE_NAME

                + " INNER JOIN " + JewelTypeEntry.TABLE_NAME + " ON "
                + JewelEntry.FK_JEWEL_TYPE_ID + "=" + JewelTypeEntry.ALTER__ID

                + " LEFT JOIN " + DesignerEntry.TABLE_NAME + " ON "
                + JewelEntry.FK_DESIGNER_ID + "=" + DesignerEntry.ALTER__ID
        );

        // Creación de la vista de LISTA DE JOYAS PARA UNA LISTA COMPLETA
//        db.execSQL ("DROP VIEW " + JewelEntry.FULL_VIEW_NAME);
        db.execSQL ("CREATE VIEW IF NOT EXISTS " + JewelEntry.JEWEL_FULL_DATA_VIEW
                + " AS SELECT "
                + JewelEntry.VIEW_NAME +".*,"

                + JewelEntry.VIEW_NAME +"." + JewelEntry._ID + " AS " + JewelEntry.ALIAS__ID  + ","
                + OwnerEntry.TABLE_NAME +"." + OwnerEntry._ID + " AS " + OwnerEntry.ALIAS__ID  + ","
                + HallmarkEntry.TABLE_NAME +"." + HallmarkEntry._ID + " AS " + HallmarkEntry.ALIAS__ID  + ","
                + GemstoneEntry.TABLE_NAME +"." + GemstoneEntry._ID + " AS " + GemstoneEntry.ALIAS__ID  + ","
                + CutEntry.TABLE_NAME +"." + CutEntry._ID + " AS " + CutEntry.ALIAS__ID  + ","

                + OwnerEntry.NAME + ","
                + HallmarkEntry.NAME + ","
                + GemstoneEntry.NAME + ","
                + CutEntry.NAME

                + " FROM " + JewelEntry.VIEW_NAME

                + " LEFT JOIN " + JewelsOwnersEntry.TABLE_NAME + " ON "
                + JewelsOwnersEntry.TABLE_NAME + "." + JewelsOwnersEntry.FK_JEWEL_ID  + "=" + JewelEntry.ALIAS__ID

                + " LEFT JOIN " + OwnerEntry.TABLE_NAME + " ON "
                + JewelsOwnersEntry.FK_OWNER_ID  + "=" + OwnerEntry.ALIAS__ID

                + " LEFT JOIN " + JewelsHallmarksEntry.TABLE_NAME + " ON "
                + JewelsHallmarksEntry.TABLE_NAME + "." + JewelsHallmarksEntry.FK_JEWEL_ID  + "=" + JewelEntry.ALIAS__ID

                + " LEFT JOIN " + HallmarkEntry.TABLE_NAME + " ON "
                + JewelsHallmarksEntry.FK_HALLMARK_ID  + "=" + HallmarkEntry.ALIAS__ID


                + " LEFT JOIN " + JewelsGemstonesCutsEntry.TABLE_NAME + " ON "
                + JewelsGemstonesCutsEntry.TABLE_NAME + "." + JewelsGemstonesCutsEntry.FK_JEWEL_ID  + "=" + JewelEntry.ALIAS__ID

                + " LEFT JOIN " + GemstoneEntry.TABLE_NAME + " ON "
                + JewelsGemstonesCutsEntry.FK_GEMSTONE_ID  + "=" + GemstoneEntry.ALIAS__ID

                + " LEFT JOIN " + CutEntry.TABLE_NAME + " ON "
                + JewelsGemstonesCutsEntry.FK_CUT_ID  + "=" + CutEntry.ALIAS__ID
        );


        // Creación de la vista de LISTA DE JOYAS GEMAS Y CORTES

        db.execSQL (
                "CREATE VIEW " + JewelsGemstonesCutsEntry.GEMS_CUTS_VIEW_NAME + " AS"
                        + " SELECT "
                        + GemstoneEntry.TABLE_NAME + "." + _ID + " AS " + GemstoneEntry.ALIAS__ID + ","
                        + CutEntry.TABLE_NAME + "." + _ID + " AS " + CutEntry.ALIAS__ID + ","
                        + GemstoneEntry.NAME + ","
                        + CutEntry.NAME
                        + " FROM " + GemstoneEntry.TABLE_NAME
                        + " LEFT JOIN " + CutEntry.TABLE_NAME
                        + " UNION "
                        + "SELECT "
                        + GemstoneEntry.TABLE_NAME + "." + _ID + " AS " + GemstoneEntry.ALIAS__ID + ","
                        + GemstoneEntry.NULL_ID + ","
                        + GemstoneEntry.NAME + ","
                        + GemstoneEntry.NULL_FIELD
                        + " FROM " + GemstoneEntry.TABLE_NAME
                        + " ORDER BY " + GemstoneEntry.NAME);


//        // Creación de la vista de LISTA DE JOYAS LOTES Y SUBASTAS
//        db.execSQL (
//                "CREATE VIEW " + LotEntry.LIST_LOTS_VIEW_NAME + " AS"
//                        + " SELECT "
//                        + JewelEntry.FULL_VIEW_NAME + ".*,"
//                        + LotEntry.NUMBER + ","
//                        + AuctionEntry.VIEW_NAME +  "." + AuctionHouseEntry.NAME + ","
//                        + AuctionEntry.VIEW_NAME +  "." + CityEntry.NAME + ","
//                        + AuctionEntry.DATE
//                        + " FROM " + JewelEntry.FULL_VIEW_NAME
//                        + " LEFT JOIN " + LotEntry.TABLE_NAME+ " ON "
//                        + JewelEntry.ALIAS__ID + "=" + LotEntry.FK_JEWEL_ID
//                        + " LEFT JOIN " + AuctionEntry.VIEW_NAME+ " ON "
//                        + AuctionEntry.ALTER_VIEW__ID + "=" + LotEntry.FK_AUCTION_ID);


        // Creación de la vista de LISTA DE JOYAS CON EL LOTE y SUBASTA  PARA UNA LISTA
//        db.execSQL (
//                "CREATE VIEW " + JewelEntry.LIST_DISTINCT_LOTS_VIEW_NAME + " AS"
//                        + " SELECT "
//                        + JewelEntry.JEWEL_FULL_DATA_VIEW + ".*,"
////                        + JewelEntry.LOT + ","
//                        + AuctionEntry.VIEW_NAME +  "." + AuctionHouseEntry.NAME + ","
//                        + AuctionEntry.VIEW_NAME +  "." + CityEntry.NAME + ","
//                        + AuctionEntry.DATE
//                        + " FROM " + JewelEntry.JEWEL_FULL_DATA_VIEW
//                        + " GROUP BY " + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + "_id");


        // Creación de la vista de LISTA DE JOYAS CON EL LOTE POR NOMBRE
        db.execSQL (
                "CREATE VIEW " + JewelsGemstonesCutsEntry.GEMS_CUTS_VIEW_WITH_ID_NAME + " AS"
                        + " SELECT "
                        + "(select count(*) from " + JewelsGemstonesCutsEntry.GEMS_CUTS_VIEW_NAME + " b"
                        + " where case when a." + GemstoneEntry.NAME + "|| a." + CutEntry.NAME + " is NULL  then "
                        + "a." + GemstoneEntry.NAME + " else a."  + GemstoneEntry.NAME + "|| a." + CutEntry.NAME  + " end  >= "
                        + "case when b." + GemstoneEntry.NAME + "|| b." + CutEntry.NAME + " is NULL  then "
                        + "b." + GemstoneEntry.NAME + " else b."  + GemstoneEntry.NAME + "|| b." + CutEntry.NAME  + " end)"
                        + " as _id,"
                        + "a.*"
                        + " FROM "  + JewelsGemstonesCutsEntry.GEMS_CUTS_VIEW_NAME + " a");


        // Creación de la vista de LISTA DE JOYAS LOTES Y SUBASTAS
        db.execSQL (
            "CREATE VIEW " + JewelEntry.JEWELS_WITH_AUCTION + " AS"
                + " SELECT "
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + JewelEntry.ID + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + JewelTypeEntry.NAME + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + DesignerEntry.NAME + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + JewelEntry.FK_AUCTION_ID + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + JewelEntry.LOT + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + GemstoneEntry.NAME + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + CutEntry.NAME + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + OwnerEntry.NAME + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + HallmarkEntry.NAME + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + PeriodEntry.NAME + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + JewelEntry.OBS + ","
                + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + JewelEntry.AVATAR_URI + ","
                + AuctionEntry.ALTER_VIEW__ID + ","
                + AuctionEntry.VIEW_NAME + "." + AuctionEntry.NAME + ","
                + AuctionEntry.VIEW_NAME + "." + AuctionHouseEntry.NAME + ","
                + AuctionEntry.VIEW_NAME + "." + CityEntry.NAME + ","
                + AuctionEntry.VIEW_NAME + "." + AuctionEntry.DATE
                + " FROM " + JewelEntry.JEWEL_FULL_DATA_VIEW
                + " JOIN " + AuctionEntry.VIEW_NAME + " ON "
                + AuctionEntry.ALTER_VIEW__ID + "=" + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + JewelEntry.FK_AUCTION_ID
                + " ORDER BY "
                    + JewelEntry.FK_AUCTION_ID + ","
                    + JewelEntry.LOT + ","
                    + JewelEntry.JEWEL_FULL_DATA_VIEW + "." + JewelEntry.ID + ","
                    + OwnerEntry.NAME + ","
                    + HallmarkEntry.NAME + ","
                    + GemstoneEntry.NAME + ","
                    + CutEntry.NAME
        );

        onUpgrade (db,1,DATABASE_VERSION);
    }


    private void updateVer29(SQLiteDatabase db){
    }

    private void updateVer30(SQLiteDatabase db){
    }


    private void updateVer31(SQLiteDatabase db){
    }

    private void updateVer32(SQLiteDatabase db){
    }


    private void updateVer33(SQLiteDatabase db){
    }

    private void updateVer34(SQLiteDatabase db){
    }

    private void updateVer35(SQLiteDatabase db){
    }

    private void updateVer37(SQLiteDatabase db){
    }



    private void updateVer38(SQLiteDatabase db){
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion)
        {
            switch (upgradeTo){

                case 29:
                    updateVer29 (db);
                    break;
                case 30:
                    updateVer30 (db);
                    break;
                case 31:
                    updateVer31 (db);
                    break;
                case 32:
                    updateVer32 (db);
                    break;
                case 33:
                    updateVer33 (db);
                    break;
                case 34:
                    updateVer34 (db);
                    break;
                case 35:
                    updateVer35 (db);
                    break;
                case 37:
                    updateVer37 (db);
                    break;
                case 38:
                    updateVer38 (db);
                    break;
            }
            upgradeTo++;
        }
    }


    // Auction

    public long saveAuction(Auction auction) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                AuctionEntry.TABLE_NAME,
                null,
                auction.toContentValues());

    }

    public Cursor getAllAuctions() {
        return getReadableDatabase()
                .query(
                        AuctionEntry.VIEW_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getAuctionById(String auctionId) {
        return getReadableDatabase().query(
                AuctionEntry.VIEW_NAME,
                null,
                AuctionEntry.ID + " LIKE ?",
                new String[]{auctionId},
                null,
                null,
                null);
    }

    public int deleteAuction(String auctionId) {
        return getWritableDatabase().delete(
                AuctionEntry.TABLE_NAME,
                AuctionEntry.ID + " LIKE ?",
                new String[]{auctionId});
    }

    public int updateAuction(Auction auction, String auctionId) {
        return getWritableDatabase().update(
                AuctionEntry.TABLE_NAME,
                auction.toContentValues(),
                AuctionEntry.ID + " LIKE ?",
                new String[]{auctionId}
        );
    }

    // Jewel

    public long saveJewel(Jewel jewel) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                JewelEntry.TABLE_NAME,
                null,
                jewel.toContentValues());

    }

    public Cursor getAllJewels() {
        return getReadableDatabase()
                .query(
                        JewelEntry.VIEW_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getAllJewelsForList(Bundle bundle, boolean group) {
        String strSelection = null;
        String[] selectionArgs = null;
//        String strGroupBy = null;
        String strGroupBy = JewelEntry.JEWELS_WITH_AUCTION + "." + JewelEntry.ID;

        if(bundle != null && !bundle.isEmpty ()){
            ArrayList<String> arraySelectionArg;
            strSelection = "";
            arraySelectionArg = new ArrayList<> ();

            if(bundle.getString (BUNDLE_DATA_JEWEL_TYPE) != null && ! bundle.getString (BUNDLE_DATA_JEWEL_TYPE).equals ("")){
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + JewelTypeEntry.NAME + " LIKE ?";
                arraySelectionArg.add (bundle.getString (BUNDLE_DATA_JEWEL_TYPE));
            }

            if(bundle.getString (BUNDLE_DATA_AUCTION_HOUSE) != null && ! bundle.getString (BUNDLE_DATA_AUCTION_HOUSE).equals ("")){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + AuctionHouseEntry.NAME + " LIKE ?";
                arraySelectionArg.add (bundle.getString (BUNDLE_DATA_AUCTION_HOUSE));
            }

            if(bundle.getString (BUNDLE_DATA_CITY) != null && ! bundle.getString (BUNDLE_DATA_CITY).equals ("")){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + CityEntry.NAME + " LIKE ?";
                arraySelectionArg.add (bundle.getString (BUNDLE_DATA_CITY));
            }

            if(bundle.getString (BUNDLE_DATA_COUNTRY) != null && ! bundle.getString (BUNDLE_DATA_COUNTRY).equals ("")){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + CountryEntry.NAME + " LIKE ?";
                arraySelectionArg.add (bundle.getString (BUNDLE_DATA_COUNTRY));
            }

            if(bundle.getString (BUNDLE_DATA_DESIGNER) != null && ! bundle.getString (BUNDLE_DATA_DESIGNER).equals ("")){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + DesignerEntry.NAME + " LIKE ?";
                arraySelectionArg.add (bundle.getString (BUNDLE_DATA_DESIGNER));
            }

            if(bundle.getString (BUNDLE_DATA_OWNER) != null && ! bundle.getString (BUNDLE_DATA_OWNER).equals ("")){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + OwnerEntry.NAME + " LIKE ?";
                arraySelectionArg.add (bundle.getString (BUNDLE_DATA_OWNER));
                strGroupBy += "," + JewelEntry.JEWELS_WITH_AUCTION + "." + OwnerEntry.NAME;
            }

            if(bundle.getString (BUNDLE_DATA_HALLMARK) != null && ! bundle.getString (BUNDLE_DATA_HALLMARK).equals ("")){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + HallmarkEntry.NAME + " LIKE ?";
                arraySelectionArg.add (bundle.getString (BUNDLE_DATA_HALLMARK));
                strGroupBy += "," + JewelEntry.JEWELS_WITH_AUCTION + "." + HallmarkEntry.NAME;
            }

            if(bundle.getString (BUNDLE_DATA_GEMSTONE) != null && ! bundle.getString (BUNDLE_DATA_GEMSTONE).equals ("")){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + GemstoneEntry.NAME + " LIKE ?";
                arraySelectionArg.add (bundle.getString (BUNDLE_DATA_GEMSTONE));
                strGroupBy += "," + JewelEntry.JEWELS_WITH_AUCTION + "." + GemstoneEntry.NAME;
            }

            if(bundle.getString (BUNDLE_DATA_CUT) != null && ! bundle.getString (BUNDLE_DATA_CUT).equals ("")){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + CutEntry.NAME + " LIKE ?";
                arraySelectionArg.add (bundle.getString (BUNDLE_DATA_CUT));
                strGroupBy += "," + JewelEntry.JEWELS_WITH_AUCTION + "." + CutEntry.NAME;
            }

            if(bundle.getString (BUNDLE_DATA_PERIOD) != null && ! bundle.getString (BUNDLE_DATA_PERIOD).equals ("")){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + PeriodEntry.NAME + " LIKE ?";
                arraySelectionArg.add (bundle.getString (BUNDLE_DATA_PERIOD));
            }

            if(bundle.getString (BUNDLE_DATA_OBS) != null && ! bundle.getString (BUNDLE_DATA_OBS).equals ("")){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + JewelEntry.OBS + " LIKE ?";
                arraySelectionArg.add ("%" + bundle.getString (BUNDLE_DATA_OBS) + "%");
            }

            String dateFrom = "1900-01-01";
            String dateTo = formatUS.format (new Date ());
            if(bundle.getString (BUNDLE_DATA_DATE_FROM) != null && ! bundle.getString (BUNDLE_DATA_DATE_FROM).equals (""))
                dateFrom = bundle.getString (BUNDLE_DATA_DATE_FROM);
            if(bundle.getString (BUNDLE_DATA_DATE_TO) != null && ! bundle.getString (BUNDLE_DATA_DATE_TO).equals (""))
                dateTo = bundle.getString (BUNDLE_DATA_DATE_TO);

            if(bundle.getString (BUNDLE_DATA_DATE_FROM) != null || bundle.getString (BUNDLE_DATA_DATE_TO) != null){
                if(!strSelection.equals ("") )
                    strSelection += " AND ";
                strSelection += JewelEntry.JEWELS_WITH_AUCTION +"." + AuctionEntry.DATE + " BETWEEN ? AND ?";
                arraySelectionArg.add (dateFrom);
                arraySelectionArg.add (dateTo);
            }

            selectionArgs = (String[]) arraySelectionArg.toArray (new String[arraySelectionArg.size()]);
        }
        if(!group)
            strGroupBy = null;

        return getReadableDatabase()
                .query(
                        JewelEntry.JEWELS_WITH_AUCTION,
                        null,
                        strSelection,
                        selectionArgs,
                        strGroupBy,
                        null,
                        JewelEntry.JEWELS_WITH_AUCTION + "." + AuctionEntry.NAME + "," + JewelEntry.JEWELS_WITH_AUCTION + "." + JewelEntry.LOT,
                        null);
    }

    public Cursor getJewelById(String jewelId) {
        return getReadableDatabase().query(
                JewelEntry.JEWEL_FULL_DATA_VIEW,
                null,
                JewelEntry.JEWEL_FULL_DATA_VIEW +"." + JewelEntry.ID + " LIKE ?",
                new String[]{jewelId},
                null,
                null,
                null);
    }



    public Cursor getJewelByLot(int auctionId, String lot, boolean idConstraint) {
        String selection = "";
        ArrayList<String> values = new ArrayList<> ();
        if(auctionId != 0) {
            selection += JewelEntry.FK_AUCTION_ID + " = ? AND ";
            values.add (String.valueOf (auctionId));
        }
        selection += JewelEntry.JEWEL_FULL_DATA_VIEW +"." + JewelEntry.LOT + " LIKE ?";
        values.add (String.valueOf (lot));
        String [] selectArgs =  new String[values.size ()];
        selectArgs = values.toArray (selectArgs);
        return getReadableDatabase().query(
                JewelEntry.JEWEL_FULL_DATA_VIEW,
                null,
                selection,
                selectArgs,
                idConstraint?JewelEntry.JEWEL_FULL_DATA_VIEW +"." + JewelEntry.ID:null,
                null,
                JewelEntry.JEWEL_FULL_DATA_VIEW +"." + JewelEntry.LOT);
    }

    public int deleteJewel(String jewelId) {
        return getWritableDatabase().delete(
                JewelEntry.TABLE_NAME,
                JewelEntry.ID + " LIKE ?",
                new String[]{jewelId});
    }

    public int updateJewel(Jewel jewel, String jewelId) {
        return getWritableDatabase().update(
                JewelEntry.TABLE_NAME,
                jewel.toContentValues(),
                JewelEntry.ID + " LIKE ?",
                new String[]{jewelId}
        );
    }


    public Cursor getAllJewelsByAuctionId(int fkAuctionId,Boolean grouped) {
        String group = JewelEntry.JEWELS_WITH_AUCTION + "." + JewelEntry.ID;
        if(!grouped)
            group = null;
        return getReadableDatabase()
                .query(
                        JewelEntry.JEWELS_WITH_AUCTION,
                        null,
                        JewelEntry.JEWELS_WITH_AUCTION + "." + JewelEntry.FK_AUCTION_ID + " = ?",
                        new String[]{String.valueOf (fkAuctionId)},
                        group,
                        null,
                        JewelEntry.JEWELS_WITH_AUCTION + "." + JewelEntry.LOT,
                        null);
    }

    public boolean lotNumberExists(Editable lotNumber,int fkAuctionId){
        Cursor c = getReadableDatabase().rawQuery (
                "SELECT COUNT(*) " +
                " FROM " + JewelEntry.TABLE_NAME +
                " WHERE " + JewelEntry.TABLE_NAME +"." + JewelEntry.LOT + " LIKE '" + lotNumber.toString () + "'" +
                    " AND " + JewelEntry.TABLE_NAME +"." + JewelEntry.FK_AUCTION_ID + " = " + fkAuctionId,
                null
                );
        c.moveToFirst ();
        return c.getInt (0) != 0;
    }

    //---------------- AuctionHouse

    public long saveAuctionHouse(AuctionHouse auctionHouse) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                AuctionHouseEntry.TABLE_NAME,
                null,
                auctionHouse.toContentValues());

    }

    public Cursor getAllAuctionsHouse() {
        return getReadableDatabase()
                .query(
                        AuctionHouseEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getAuctionHouseById(String auctionHouseId) {
        return getReadableDatabase().query(
                AuctionHouseEntry.TABLE_NAME,
                null,
                AuctionHouseEntry.ID + " LIKE ?",
                new String[]{auctionHouseId},
                null,
                null,
                null);
    }

    public int deleteAuctionHouse(String auctionHouseId) {
        return getWritableDatabase().delete(
                AuctionHouseEntry.TABLE_NAME,
                AuctionHouseEntry.ID + " LIKE ?",
                new String[]{auctionHouseId});
    }

    public int updateAuctionHouse(AuctionHouse auctionHouse, String auctionHouseId) {
        return getWritableDatabase().update(
                AuctionHouseEntry.TABLE_NAME,
                auctionHouse.toContentValues(),
                AuctionHouseEntry.ID + " LIKE ?",
                new String[]{auctionHouseId}
        );
    }

    //---------------- City

    public long saveCity(City city) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                CityEntry.TABLE_NAME,
                null,
                city.toContentValues());

    }

    public Cursor getAllCities() {
        return getReadableDatabase()
                .query(
                        CityEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getAllDataFiltered(String tableName,String fieldName,String constraint) {
        if(constraint.equals (""))
            return getReadableDatabase().rawQuery(
                    "SELECT * FROM " + tableName
                    + " ORDER BY " + fieldName + " ASC"
                    ,null);
        return getReadableDatabase().rawQuery(
            "SELECT * FROM " + tableName
                +  " WHERE " + fieldName + " LIKE '%" + constraint + "%' "
                + " ORDER BY " + fieldName + " ASC"
                ,null);
    }


    public Cursor getAllData(String tableName,String fieldName) {
        return getReadableDatabase().rawQuery(
            "SELECT * FROM " + tableName
                    + " ORDER BY " + fieldName + " ASC"
            ,null);
    }

    public Cursor getViewCityById(String CityId) {
        return getReadableDatabase().query(
                CityEntry.VIEW_NAME,
                null,
                CityEntry.ID + " LIKE ?", new String[]{CityId},
                null,
                null,
                null);
    }


    public Cursor getCityById(String CityId) {
        return getReadableDatabase().query(
                CityEntry.TABLE_NAME,
                null,
                CityEntry.ID + " LIKE ?", new String[]{CityId},
                null,
                null,
                null);
    }

    public int deleteCity(String CityId) {
        return getWritableDatabase().delete(
                CityEntry.TABLE_NAME,
                CityEntry.ID + " LIKE ?",
                new String[]{CityId});
    }

    public int updateCity(City city, String CityId) {
        return getWritableDatabase().update(
                CityEntry.TABLE_NAME,
                city.toContentValues(),
                CityEntry.ID + " LIKE ?",
                new String[]{CityId}
        );
    }

    //---------------- Country

    public long saveCountry(Country country) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                CountryEntry.TABLE_NAME,
                null,
                country.toContentValues());

    }

    public Cursor getAllCountries() {
        return getReadableDatabase()
                .query(
                        CountryEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getCountryById(String CountryId) {
        return getReadableDatabase().query(
                CountryEntry.TABLE_NAME,
                null,
                CountryEntry.ID + " LIKE ?",
                new String[]{CountryId},
                null,
                null,
                null);
    }

    public int deleteCountry(String CountryId) {
        return getWritableDatabase().delete(
                CountryEntry.TABLE_NAME,
                CountryEntry.ID + " LIKE ?",
                new String[]{CountryId});
    }

    public int updateCountry(Country country, String CountryId) {
        return getWritableDatabase().update(
                CountryEntry.TABLE_NAME,
                country.toContentValues(),
                CountryEntry.ID + " LIKE ?",
                new String[]{CountryId}
        );
    }

    //---------------- JewelType

    public long saveJewelType(JewelType city) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                JewelTypeEntry.TABLE_NAME,
                null,
                city.toContentValues());

    }

    public Cursor getAllJewelTypes() {
        return getReadableDatabase()
                .query(
                        JewelTypeEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getJewelTypeById(String JewelTypeId) {
        return getReadableDatabase().query(
                JewelTypeEntry.TABLE_NAME,
                null,
                JewelTypeEntry.ID + " LIKE ?",
                new String[]{JewelTypeId},
                null,
                null,
                null);
    }

    public int deleteJewelType(String JewelTypeId) {
        return getWritableDatabase().delete(
                JewelTypeEntry.TABLE_NAME,
                JewelTypeEntry.ID + " LIKE ?",
                new String[]{JewelTypeId});
    }

    public int updateJewelType(JewelType city, String JewelTypeId) {
        return getWritableDatabase().update(
                JewelTypeEntry.TABLE_NAME,
                city.toContentValues(),
                JewelTypeEntry.ID + " LIKE ?",
                new String[]{JewelTypeId}
        );
    }

    //---------------- Designer

    public long saveDesigner(Designer designer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                DesignerEntry.TABLE_NAME,
                null,
                designer.toContentValues());

    }

    public Cursor getAllDesigners() {
        return getReadableDatabase()
                .query(
                        DesignerEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getDesignerById(String DesignerId) {
        return getReadableDatabase().query(
                DesignerEntry.TABLE_NAME,
                null,
                DesignerEntry.ID + " LIKE ?",
                new String[]{DesignerId},
                null,
                null,
                null);
    }

    public int deleteDesigner(String DesignerId) {
        return getWritableDatabase().delete(
                DesignerEntry.TABLE_NAME,
                DesignerEntry.ID + " LIKE ?",
                new String[]{DesignerId});
    }

    public int updateDesigner(Designer designer, String DesignerId) {
        return getWritableDatabase().update(
                DesignerEntry.TABLE_NAME,
                designer.toContentValues(),
                DesignerEntry.ID + " LIKE ?",
                new String[]{DesignerId}
        );
    }


    //---------------- Cut

    public long saveCut(Cut cut) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                CutEntry.TABLE_NAME,
                null,
                cut.toContentValues());

    }

    public Cursor getAllCuts() {
        return getReadableDatabase()
                .query(
                        CutEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getCutById(String CutId) {
        return getReadableDatabase().query(
                CutEntry.TABLE_NAME,
                null,
                CutEntry.ID + " LIKE ?",
                new String[]{CutId},
                null,
                null,
                null);
    }

    public int deleteCut(String CutId) {
        return getWritableDatabase().delete(
                CutEntry.TABLE_NAME,
                CutEntry.ID + " LIKE ?",
                new String[]{CutId});
    }

    public int updateCut(Cut cut, String CutId) {
        return getWritableDatabase().update(
                CutEntry.TABLE_NAME,
                cut.toContentValues(),
                CutEntry.ID + " LIKE ?",
                new String[]{CutId}
        );
    }

    //---------------- Owner

    public long saveOwner(Owner designer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                OwnerEntry.TABLE_NAME,
                null,
                designer.toContentValues());

    }

    public Cursor getAllOwners() {
        return getReadableDatabase()
                .query(
                        OwnerEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getOwnerById(String OwnerId) {
        return getReadableDatabase().query(
                OwnerEntry.TABLE_NAME,
                null,
                OwnerEntry.ID + " LIKE ?",
                new String[]{OwnerId},
                null,
                null,
                null);
    }

    public int deleteOwner(String OwnerId) {
        return getWritableDatabase().delete(
                OwnerEntry.TABLE_NAME,
                OwnerEntry.ID + " LIKE ?",
                new String[]{OwnerId});
    }

    public int updateOwner(Owner designer, String OwnerId) {
        return getWritableDatabase().update(
                OwnerEntry.TABLE_NAME,
                designer.toContentValues(),
                OwnerEntry.ID + " LIKE ?",
                new String[]{OwnerId}
        );
    }

    //---------------- Hallmark

    public long saveHallmark(Hallmark designer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                HallmarkEntry.TABLE_NAME,
                null,
                designer.toContentValues());

    }

    public Cursor getAllHallmarks() {
        return getReadableDatabase()
                .query(
                        HallmarkEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getHallmarkById(String HallmarkId) {
        return getReadableDatabase().query(
                HallmarkEntry.TABLE_NAME,
                null,
                HallmarkEntry.ID + " LIKE ?",
                new String[]{HallmarkId},
                null,
                null,
                null);
    }

    public int deleteHallmark(String HallmarkId) {
        return getWritableDatabase().delete(
                HallmarkEntry.TABLE_NAME,
                HallmarkEntry.ID + " LIKE ?",
                new String[]{HallmarkId});
    }

    public int updateHallmark(Hallmark designer, String HallmarkId) {
        return getWritableDatabase().update(
                HallmarkEntry.TABLE_NAME,
                designer.toContentValues(),
                HallmarkEntry.ID + " LIKE ?",
                new String[]{HallmarkId}
        );
    }

    //---------------- Gemstone

    public long saveGemstone(Gemstone designer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                GemstoneEntry.TABLE_NAME,
                null,
                designer.toContentValues());

    }

    public Cursor getAllGemstones() {
        return getReadableDatabase()
                .query(
                        GemstoneEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getGemstoneById(String GemstoneId) {
        return getReadableDatabase().query(
                GemstoneEntry.TABLE_NAME,
                null,
                GemstoneEntry.ID + " LIKE ?",
                new String[]{GemstoneId},
                null,
                null,
                null);
    }

    public int deleteGemstone(String GemstoneId) {
        return getWritableDatabase().delete(
                GemstoneEntry.TABLE_NAME,
                GemstoneEntry.ID + " LIKE ?",
                new String[]{GemstoneId});
    }

    public int updateGemstone(Gemstone designer, String GemstoneId) {
        return getWritableDatabase().update(
                GemstoneEntry.TABLE_NAME,
                designer.toContentValues(),
                GemstoneEntry.ID + " LIKE ?",
                new String[]{GemstoneId}
        );
    }


    //---------------- Period

    public long savePeriod(Period designer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                PeriodEntry.TABLE_NAME,
                null,
                designer.toContentValues());

    }

    public Cursor getAllPeriods() {
        return getReadableDatabase()
                .query(
                        PeriodEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getPeriodById(String PeriodId) {
        return getReadableDatabase().query(
                PeriodEntry.TABLE_NAME,
                null,
                PeriodEntry.ID + " LIKE ?",
                new String[]{PeriodId},
                null,
                null,
                null);
    }

    public int deletePeriod(String PeriodId) {
        return getWritableDatabase().delete(
                PeriodEntry.TABLE_NAME,
                PeriodEntry.ID + " LIKE ?",
                new String[]{PeriodId});
    }

    public int updatePeriod(Period designer, String PeriodId) {
        return getWritableDatabase().update(
                PeriodEntry.TABLE_NAME,
                designer.toContentValues(),
                PeriodEntry.ID + " LIKE ?",
                new String[]{PeriodId}
        );
    }


    //---------------- JewelsOwners

    public long saveJewelsOwners(JewelsOwners jewelsOwners) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                JewelsOwnersEntry.TABLE_NAME,
                null,
                jewelsOwners.toContentValues());

    }

    public Cursor getAllJewelsOwners() {
        return getReadableDatabase()
                .query(
                        JewelsOwnersEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getJewelsOwnersById(int jewelId) {
        return getReadableDatabase().rawQuery (
                "SELECT * FROM "
                + JewelsOwnersEntry.TABLE_NAME
                + " WHERE "
                + JewelsOwnersEntry.FK_JEWEL_ID + " = " + jewelId,null);
    }

    public int deleteJewelsOwners(int jewelId) {
        Cursor c = getReadableDatabase().rawQuery (
                "DELETE FROM "
                        + JewelsOwnersEntry.TABLE_NAME
                        + " WHERE "
                        + JewelsOwnersEntry.FK_JEWEL_ID + " = " + jewelId,null);
        return c.getCount ();
    }

    public int updateJewelsOwners(JewelsOwners[] jewelsOwners, int jewelId) {
        int count = 0;
        for (JewelsOwners jewelsOwner : jewelsOwners) {
            getWritableDatabase ().update (
                    JewelsOwnersEntry.TABLE_NAME,
                    jewelsOwner.toContentValues (),
                    JewelsOwnersEntry.FK_JEWEL_ID + " = ?",
                    new String[]{String.valueOf (jewelId)}
            );
            count++;
        }
        return count;
    }


    //---------------- JewelsHallmarks

    public long saveJewelsHallmarks(JewelsHallmarks jewelsHallmarks) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                JewelsHallmarksEntry.TABLE_NAME,
                null,
                jewelsHallmarks.toContentValues());

    }

    public Cursor getAllJewelsHallmarks() {
        return getReadableDatabase()
                .query(
                        JewelsHallmarksEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getJewelsHallmarksById(int jewelId) {
        return getReadableDatabase().rawQuery (
                "SELECT * FROM "
                        + JewelsHallmarksEntry.TABLE_NAME
                        + " WHERE "
                        + JewelsHallmarksEntry.FK_JEWEL_ID + " = " + jewelId,null);
    }

    public int deleteJewelsHallmarks(int jewelId) {
        Cursor c = getReadableDatabase().rawQuery (
                "DELETE FROM "
                        + JewelsHallmarksEntry.TABLE_NAME
                        + " WHERE "
                        + JewelsHallmarksEntry.FK_JEWEL_ID + " = " + jewelId,null);
        return c.getCount ();
    }

    public int updateJewelsHallmarks(JewelsHallmarks[] jewelsHallmarks, int jewelId) {
        int count = 0;
        for (JewelsHallmarks jewelsHallmark : jewelsHallmarks) {
            getWritableDatabase ().update (
                    JewelsHallmarksEntry.TABLE_NAME,
                    jewelsHallmark.toContentValues (),
                    JewelsHallmarksEntry.FK_JEWEL_ID + " = ?",
                    new String[]{String.valueOf (jewelId)}
            );
            count++;
        }
        return count;
    }


    //---------------- JewelsGemstonesCuts

    public long saveJewelsGemstonesCuts(JewelsGemstonesCuts jewelsGemstonesCuts) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                JewelsGemstonesCutsEntry.TABLE_NAME,
                null,
                jewelsGemstonesCuts.toContentValues());

    }

    public Cursor getAllJewelsGemstonesCuts() {
        return getReadableDatabase()
                .query(
                        JewelsGemstonesCutsEntry.GEMS_CUTS_VIEW_WITH_ID_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getJewelsGemstonesCutsById(int jewelId) {
        return getReadableDatabase().rawQuery (
                "SELECT * FROM "
                        + JewelsGemstonesCutsEntry.TABLE_NAME
                        + " WHERE "
                        + JewelsGemstonesCutsEntry.FK_JEWEL_ID + " = " + jewelId,null);
    }

    public int deleteJewelsGemstonesCuts(int jewelId) {
        Cursor c = getReadableDatabase().rawQuery (
                "DELETE FROM "
                        + JewelsGemstonesCutsEntry.TABLE_NAME
                        + " WHERE "
                        + JewelsGemstonesCutsEntry.FK_JEWEL_ID + " = " + jewelId,null);
        return c.getCount ();
    }

    public int updateJewelsGemstonesCuts(JewelsGemstonesCuts[] jewelsGemstonesCuts, int jewelId) {
        int count = 0;
        for (JewelsGemstonesCuts jewelsGemstonesCut : jewelsGemstonesCuts) {
            getWritableDatabase ().update (
                    JewelsGemstonesCutsEntry.TABLE_NAME,
                    jewelsGemstonesCut.toContentValues (),
                    JewelsGemstonesCutsEntry.FK_JEWEL_ID + " = ?",
                    new String[]{String.valueOf (jewelId)}
            );
            count++;
        }
        return count;
    }

}
