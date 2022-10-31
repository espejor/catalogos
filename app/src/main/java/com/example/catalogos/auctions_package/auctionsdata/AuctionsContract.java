package com.example.catalogos.auctions_package.auctionsdata;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class AuctionsContract {

    public static abstract class AuctionEntry implements BaseColumns{
        public static final String TABLE_NAME ="auction";
        public static final String VIEW_NAME = "auctionView";

        public static final String _ID = "_id";
        public static final String ID = "id";
        public static final String NAME = "auction";
        public static final String FK_AUCTION_HOUSE_ID = "fkAuctionHouseId";
        public static final String FK_CITY_ID = "fkCityId";
        public static final String AVATAR_URI = "avatarUri";
        public static final String DATE = "date";
        // Unambigous names
        public static final String ALTER__ID = TABLE_NAME + "." + _ID;
        public static final String ALTER_VIEW__ID = VIEW_NAME + "." + _ID;
        public static final String ALTER_ID = TABLE_NAME + "." + ID;
        public static final String ALTER_AVATAR_URI = TABLE_NAME + "." + AVATAR_URI;
    }
}
