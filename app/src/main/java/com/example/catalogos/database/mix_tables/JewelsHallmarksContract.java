package com.example.catalogos.database.mix_tables;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class JewelsHallmarksContract {

    public static abstract class JewelsHallmarksEntry implements BaseColumns{
        public static final String TABLE_NAME ="jewels_hallmarks";
        public static final String VIEW_NAME = "jewelsHallmarksView";

        public static final String ID = "id";
        public static final String _ID = "_id";
        public static final String FK_HALLMARK_ID = "fkHallmarkId";
        public static final String FK_JEWEL_ID = "fkJewelId";
    }
}
