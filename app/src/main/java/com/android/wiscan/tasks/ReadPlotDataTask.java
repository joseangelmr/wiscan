package com.android.wiscan.tasks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.android.wiscan.activities.DataPlotActivity;
import com.android.wiscan.database.RedesContract;
import com.android.wiscan.database.RedesDBHelper;

import java.util.ArrayList;

/**
 * Created by David on 24/01/2015.
 */
public class ReadPlotDataTask extends AsyncTask<DataPlotActivity,Void,Void> {

    public ArrayList<Integer> intensidad;
    public ArrayList<Float> probabilidad;
    public ArrayList<Integer> num_scan;

    private DataPlotActivity actividad;


    @Override
    protected Void doInBackground(DataPlotActivity...params) {
        actividad = params[0];

        RedesDBHelper dbHelper = new RedesDBHelper(actividad);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(RedesContract.Red.TABLE_NAME,
                new String[]{ /*Columnas a seleccionar*/
                        RedesContract.Red.COLUMN_NAME_INTENSIDAD,
                        RedesContract.Red.COLUMN_NAME_PROBABILIDAD,
                        RedesContract.Red.COLUMN_NAME_NUMSCAN
                },
                RedesContract.Red.COLUMN_NAME_BSSID+"=?",
                new String [] {actividad.getBSSID()},
                null,
                null,
                RedesContract.Red.COLUMN_NAME_NUMSCAN+" ASC");
        intensidad = new ArrayList<Integer>();
        num_scan = new ArrayList<Integer>();
        probabilidad = new ArrayList<Float>();
        int i=0;
        Log.v("PRUEBA LOAD PLOT DATA","VALORES EN CURSOR: "+c.getCount());
        while(c.moveToNext()){

            /*TODO Corregir error de la probabilidad para los scan donde se detecta la red*/

            int inte = c.getInt(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_INTENSIDAD));
            float prob = c.getFloat(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_PROBABILIDAD));
            int n_scan = c.getInt(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_NUMSCAN));
            Log.v("PRUEBA LOAD PLOT DATA",inte+" || "+prob+" || "+n_scan);
            intensidad.add(inte);
            num_scan.add(n_scan);
            probabilidad.add(prob);
        }
        c.close();
        db.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        actividad.setPlotData(num_scan,intensidad,true);
        actividad.setPlotData(num_scan,probabilidad,false);
    }
}