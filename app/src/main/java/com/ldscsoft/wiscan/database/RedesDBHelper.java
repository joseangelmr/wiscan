package com.ldscsoft.wiscan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.ldscsoft.wiscan.database.RedesContract.Red;

/**
 * Created by David on 16/01/2015.
 */
public class RedesDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "wiscan.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Red.TABLE_NAME + " (" +
                    Red._ID + INTEGER_TYPE+" PRIMARY KEY AUTOINCREMENT," +
                    Red.COLUMN_NAME_NUMSCAN + INTEGER_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_TIEMPO_I + TEXT_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_TIEMPO_F + TEXT_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_BSSID + TEXT_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_SSID + TEXT_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_SEGURIDAD + TEXT_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_CANAL + INTEGER_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_INTENSIDAD + INTEGER_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_LATITUD_I + REAL_TYPE+ COMMA_SEP +
                    Red.COLUMN_NAME_LONGITUD_I + REAL_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_LATITUD_F + REAL_TYPE+ COMMA_SEP +
                    Red.COLUMN_NAME_LONGITUD_F + REAL_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_DISCOVERY_RATE + REAL_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_DETECTADA + TEXT_TYPE+ COMMA_SEP +
                    Red.COLUMN_NAME_TOTAL_REDES + INTEGER_TYPE + COMMA_SEP +
                    Red.COLUMN_NAME_PROBABILIDAD + REAL_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RedesContract.Red.TABLE_NAME;

    /*public static final String SQL_COUNT_NETWORK =
            "SELECT COUNT("+Red.COLUMN_NAME_BSSID+") AS detecciones FROM "+
            Red.TABLE_NAME+" WHERE "+
            Red.COLUMN_NAME_BSSID+" = ?";*/

    /*public static final String SQL_GET_PLOT_DATA =
            "SELECT ("+RedesContract.Red.COLUMN_NAME_BSSID+") AS detecciones FROM "+
                    RedesContract.Red.TABLE_NAME+" WHERE "+
                    RedesContract.Red.COLUMN_NAME_BSSID+" = ?";
    */
    public RedesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}