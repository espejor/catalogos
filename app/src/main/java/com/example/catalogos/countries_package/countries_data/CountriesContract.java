package com.example.catalogos.countries_package.countries_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para países
 */
public class CountriesContract {

    public static abstract class CountryEntry implements BaseColumns{
        public static final String TABLE_NAME ="countries";

        public static final String ID = "id";
        public static final String NAME = "country";
        public static final String AVATAR_URI = "avatarUri";
        public static final String ALTER__ID = TABLE_NAME + "." + _ID;
    }
}
