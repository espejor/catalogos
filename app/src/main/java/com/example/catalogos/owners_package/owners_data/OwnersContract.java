package com.example.catalogos.owners_package.owners_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class OwnersContract {

    public static abstract class OwnerEntry implements BaseColumns{
        public static final String TABLE_NAME ="owners";

        public static final String ID = "id";
        public static final String NAME = "owner";
        public static final String AVATAR_URI = "avatarUri";
        // Unambigous names
        public static final String ALTER__ID = TABLE_NAME + "." + _ID;
        public static final String ALIAS__ID = "owner_Id";
        public static final String ALIAS_AVATAR = "ownerAvatar";
    }
}
