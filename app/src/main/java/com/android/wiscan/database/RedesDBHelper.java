package com.android.wiscan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by David on 16/01/2015.
 */
public class RedesDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Redes.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RedesContract.Red.TABLE_NAME + " (" +
                    RedesContract.Red._ID + INTEGER_TYPE+" PRIMARY KEY AUTOINCREMENT," +
                    RedesContract.Red.COLUMN_NAME_TIEMPO + TEXT_TYPE + COMMA_SEP +
                    RedesContract.Red.COLUMN_NAME_NUMSCAN + INTEGER_TYPE + COMMA_SEP +

                    RedesContract.Red.COLUMN_NAME_BSSID + TEXT_TYPE + COMMA_SEP +
                    RedesContract.Red.COLUMN_NAME_SSID + TEXT_TYPE + COMMA_SEP +
                    RedesContract.Red.COLUMN_NAME_SEGURIDAD + TEXT_TYPE + COMMA_SEP +
                    RedesContract.Red.COLUMN_NAME_FRECUENCIA + INTEGER_TYPE + COMMA_SEP +
                    RedesContract.Red.COLUMN_NAME_INTENSIDAD + INTEGER_TYPE + COMMA_SEP +
                    RedesContract.Red.COLUMN_NAME_LATITUD_I + REAL_TYPE+ COMMA_SEP +
                    RedesContract.Red.COLUMN_NAME_LONGITUD_I + REAL_TYPE + COMMA_SEP +
                    RedesContract.Red.COLUMN_NAME_LATITUD_F + REAL_TYPE+ COMMA_SEP +
                    RedesContract.Red.COLUMN_NAME_LONGITUD_F + REAL_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RedesContract.Red.TABLE_NAME;

    public RedesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
