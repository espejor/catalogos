package com.example.catalogos.database.mix_tables;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class JewelsOwnersContract {

    public static abstract class JewelsOwnersEntry implements BaseColumns{
        public static final String TABLE_NAME ="jewels_owners";
        public static final String VIEW_NAME = "jewelsOwnersView";

        public static final String ID = "id";
        public static final String _ID = "_id";
        public static final String FK_OWNER_ID = "fkOwnerId";
        public static final String FK_JEWEL_ID = "fkJewelId";
    }
}
