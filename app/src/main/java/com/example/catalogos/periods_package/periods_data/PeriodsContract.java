package com.example.catalogos.periods_package.periods_data;

import android.provider.BaseColumns;

/**
 * Esquema de la base de datos para subastas
 */
public class PeriodsContract {

    public static abstract class PeriodEntry implements BaseColumns{
        public static final String TABLE_NAME ="periods";

        public static final String ID = "id";
        public static final String NAME = "period";
        public static final String INIT = "init";
        public static final String FINAL = "final";
        // Unambigous names
        public static final String ALTER__ID = TABLE_NAME + "." + _ID;
    }
}
