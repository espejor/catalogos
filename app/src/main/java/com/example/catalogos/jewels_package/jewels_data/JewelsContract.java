package com.example.catalogos.jewels_package.jewels_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para joyas
 */
public class JewelsContract {

    public static abstract class JewelEntry implements BaseColumns{
        public static final String TABLE_NAME ="jewel";
        public static final String VIEW_NAME = "jewelView";
        public static final String JEWEL_LIST_VIEW = "jewelListView";
        public static final String JEWEL_FULL_DATA_VIEW = "jewelFullDataView";

        public static final String ID = "id";
        public static final String NAME = "jewel";
        public static final String LOT = "lot";
        public static final String FK_JEWEL_TYPE_ID = "fkJewelTypeId";
        public static final String FK_PERIOD_ID = "fkPeriodId";
        public static final String FK_AUCTION_ID = "fkAuctionId";
        public static final String FK_DESIGNER_ID = "fkDesignerId";
//        public static final String OWNER_ID = "fkOwnerId";
        public static final String SERIAL = "serial";
        public static final String OBS = "obs";
        public static final String AVATAR_URI = "avatarUri";
        public static final String COUNTRY_MARK = "countryMark";
        public static final String WORKSHOP_MARK = "workshopMark";
        // Unambigous names
        public static final String ALTER__ID = TABLE_NAME + "." + _ID;
        public static final String ALTER_ID = TABLE_NAME + "." + ID;
        public static final String ALTER_AVATAR_URI = TABLE_NAME + "." + AVATAR_URI;

        public static final String ALIAS__ID = "jewel_Id";
        public static final String LIST_DISTINCT_LOTS_VIEW_NAME = "list_distinct_lots_view_name";
        public static final String JEWELS_WITH_AUCTION = "jewelsWithAuction";
    }
}
