package com.example.catalogos.cuts_package.cuts_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class CutsContract {

    public static abstract class CutEntry implements BaseColumns{
        public static final String TABLE_NAME ="cuts";

        public static final String ID = "id";
        public static final String NAME = "cut";
        public static final String AVATAR_URI = "avatarUri";
        public static final String ALIAS__ID = "cut_Id";
    }
}
