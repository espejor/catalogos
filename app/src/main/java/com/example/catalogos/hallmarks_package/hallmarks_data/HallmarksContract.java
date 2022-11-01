package com.example.catalogos.hallmarks_package.hallmarks_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class HallmarksContract {

    public static abstract class HallmarkEntry implements BaseColumns{
        public static final String TABLE_NAME ="hallmarks";

        public static final String ID = "id";
        public static final String NAME = "hallmark";
        public static final String AVATAR_URI = "avatarUri";
        // Unambigous names
        public static final String ALTER__ID = TABLE_NAME + "." + _ID;
        public static final String ALIAS__ID = "hallmark_Id";
        public static final String ALIAS_AVATAR = "hallmarkAvatar";
    }
}
