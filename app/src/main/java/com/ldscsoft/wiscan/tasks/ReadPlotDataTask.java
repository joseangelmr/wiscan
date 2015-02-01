package com.ldscsoft.wiscan.tasks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.ldscsoft.wiscan.activities.DataPlotActivity;
import com.ldscsoft.wiscan.database.RedesDBHelper;

import static com.ldscsoft.wiscan.database.RedesContract.Red;

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
        Cursor c = db.query(Red.TABLE_NAME,
                new String[]{ /*Columnas a seleccionar*/
                        Red.COLUMN_NAME_INTENSIDAD,
                        Red.COLUMN_NAME_PROBABILIDAD,
                        Red.COLUMN_NAME_NUMSCAN
                },

                Red.COLUMN_NAME_BSSID+"=?",
                new String [] {actividad.getBSSID()},
                null,
                null,
                Red.COLUMN_NAME_NUMSCAN+" ASC");
        intensidad = new ArrayList<Integer>();
        num_scan = new ArrayList<Integer>();
        probabilidad = new ArrayList<Float>();
        Log.v("PRUEBA LOAD PLOT DATA","VALORES EN CURSOR: "+c.getCount());
        int index_intensidad = c.getColumnIndex(Red.COLUMN_NAME_INTENSIDAD);
        int index_prob = c.getColumnIndex(Red.COLUMN_NAME_PROBABILIDAD);
        int index_num_scan = c.getColumnIndex(Red.COLUMN_NAME_NUMSCAN);
        while(c.moveToNext()){

            /*TODO Corregir error de la probabilidad para los scan donde se detecta la red*/

            int inte = c.getInt(index_intensidad);
            float prob = c.getFloat(index_prob);
            int n_scan = c.getInt(index_num_scan);
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