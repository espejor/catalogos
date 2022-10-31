package com.example.catalogos.database.mix_tables;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class JewelsGemstonesCutsContract {

    public static abstract class JewelsGemstonesCutsEntry implements BaseColumns{
        public static final String TABLE_NAME ="jewels_gemstones";
        public static final String GEMS_CUTS_VIEW_NAME = "gems_cuts";
        public static final String GEMS_CUTS_VIEW_WITH_ID_NAME = "gems_cuts_id";

        public static final String ID = "id";
        public static final String _ID = "_id";
        public static final String FK_GEMSTONE_ID = "fkGemstoneId";
        public static final String FK_CUT_ID = "fkCutId";
        public static final String FK_JEWEL_ID = "fkJewelId";
    }
}
