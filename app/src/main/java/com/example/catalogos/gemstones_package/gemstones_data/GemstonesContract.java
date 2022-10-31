package com.example.catalogos.gemstones_package.gemstones_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class GemstonesContract {

    public static abstract class GemstoneEntry implements BaseColumns{
        public static final String TABLE_NAME ="gemstones";

        public static final String ID = "id";
        public static final String NAME = "gemstone";
        public static final String AVATAR_URI = "avatarUri";
        public static final String ALIAS__ID = "gemstone_Id";
        public static final String NULL_FIELD = "nullField";
        public static final String NULL_ID = "null_Id";
    }
}
