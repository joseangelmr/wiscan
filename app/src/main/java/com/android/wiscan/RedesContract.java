package com.android.wiscan;

import android.provider.BaseColumns;

/**
 * Created by David on 16/01/2015.
 */
public class RedesContract {
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
    public RedesContract() {}

    /* Inner class that defines the table contents */
    public static abstract class Red implements BaseColumns {
        public static final String TABLE_NAME = "redes";

        public static final String COLUMN_NAME_BSSID = "bssid";
        public static final String COLUMN_NAME_SSID = "ssid";
        public static final String COLUMN_NAME_SEGURIDAD = "seguridad";
        public static final String COLUMN_NAME_FRECUENCIA = "frecuencia";
        public static final String COLUMN_NAME_INTENSIDAD = "intensidad";
        public static final String COLUMN_NAME_LATITUD = "latitud";
        public static final String COLUMN_NAME_LONGITUD = "longitud";
        public static final String COLUMN_NAME_NUMSCAN = "numscan";
        public static final String COLUMN_NAME_TIEMPO = "tiempo";
    }
}