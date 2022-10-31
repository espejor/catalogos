package com.example.catalogos.auctions_house_package.auctions_house_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class AuctionsHouseContract {

    public static abstract class AuctionHouseEntry implements BaseColumns{
        public static final String TABLE_NAME ="auctionHouse";

        public static final String ID = "id";
        public static final String NAME = "auctionHouse";
        public static final String AVATAR_URI = "avatarUri";
        // Unambigous names
        public static final String ALTER__ID = TABLE_NAME + "." + _ID;
        public static final String ALTER_AVATAR_URI = TABLE_NAME + "." + AVATAR_URI;
    }
}
