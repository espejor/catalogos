package com.example.catalogos.cities_package.cities_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class CitiesContract {

    public static abstract class CityEntry implements BaseColumns{
        public static final String TABLE_NAME ="cities";

        public static final String ID = "id";
        public static final String NAME = "city";
        public static final String AVATAR_URI = "avatarUri";
        public static final String FK_COUNTRY_ID = "fkCountryId";
        public static final String VIEW_NAME = "cityView";
        // Unambigous names
        public static final String ALTER__ID = TABLE_NAME + "." + _ID;
        public static final String ALTER_VIEW__ID = VIEW_NAME + "." + _ID;
        public static final String ALTER_ID = TABLE_NAME + "." + ID;
        public static final String ALTER_AVATAR_URI = TABLE_NAME + "." + AVATAR_URI;
    }
}
