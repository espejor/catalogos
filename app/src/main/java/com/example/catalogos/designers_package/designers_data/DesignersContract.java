package com.example.catalogos.designers_package.designers_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class DesignersContract {

    public static abstract class DesignerEntry implements BaseColumns{
        public static final String TABLE_NAME ="designers";

        public static final String ID = "id";
        public static final String NAME = "designer";
        public static final String AVATAR_URI = "avatarUri";
        // Unambigous names
        public static final String ALTER__ID = TABLE_NAME + "." + _ID;
    }
}
