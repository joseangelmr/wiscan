package com.ldscsoft.wiscan.database;

import android.provider.BaseColumns;

/**
 * Created by David on 16/01/2015.
 */
public class RedesContract {
    //Constructor para prevenir que se instancie la clase
    private  RedesContract() {}

    public static abstract class Red implements BaseColumns {
        public static final String TABLE_NAME = "redes";

        public static final String COLUMN_NAME_BSSID = "bssid";
        public static final String COLUMN_NAME_SSID = "ssid";
        public static final String COLUMN_NAME_SEGURIDAD = "seguridad";
        public static final String COLUMN_NAME_CANAL = "canal";
        public static final String COLUMN_NAME_INTENSIDAD = "intensidad";
        public static final String COLUMN_NAME_LATITUD_I = "latitud_i";
        public static final String COLUMN_NAME_LONGITUD_I = "longitud_i";
        public static final String COLUMN_NAME_LATITUD_F = "latitud_f";
        public static final String COLUMN_NAME_LONGITUD_F = "longitud_f";
        public static final String COLUMN_NAME_NUMSCAN = "num_scan";
        public static final String COLUMN_NAME_TIEMPO_I = "tiempo_i";
        public static final String COLUMN_NAME_TIEMPO_F = "tiempo_f";
        public static final String COLUMN_NAME_PROBABILIDAD = "probabilidad";
        public static final String COLUMN_NAME_DISCOVERY_RATE = "discovery_rate";
        public static final String COLUMN_NAME_DETECTADA = "detectada";
        public static final String COLUMN_NAME_TOTAL_REDES = "total_redes";
    }
}