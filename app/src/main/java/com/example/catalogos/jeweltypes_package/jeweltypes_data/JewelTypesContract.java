package com.example.catalogos.jeweltypes_package.jeweltypes_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para Tipo de Joyas
 */
public class JewelTypesContract {

    public static abstract class JewelTypeEntry implements BaseColumns{
        public static final String TABLE_NAME ="jewelType";

        public static final String ID = "id";
        public static final String NAME = "jeweltype";
        public static final String AVATAR_URI = "avatarUri";
        // Unambigous names
        public static final String ALTER__ID = TABLE_NAME + "." + _ID;
    }
}
