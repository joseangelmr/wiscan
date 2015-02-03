package com.ldscsoft.wiscan.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.ldscsoft.wiscan.database.RedesContract;
import com.ldscsoft.wiscan.database.RedesDBHelper;
import com.ldscsoft.wiscan.helpers.AbstractDataPlot;
import com.androidplot.xy.*;
import com.ldscsoft.wiscan.tasks.ReadPlotDataTask;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NetworkPlotActivity extends AbstractDataPlot{

    private  ArrayList<Integer> intensidad;
    private ArrayList<Float> probabilidad;
    private ArrayList<Integer> num_scan;
    private String BSSID;

    @Override
    public void postInUI() {
        setPlotData(num_scan, intensidad, true, "Intensidad");
        setPlotData(num_scan, probabilidad, false, "Probabilidad de detección");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BSSID = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        Log.v("PRUEBA PLOT", "BSSID A PLOTEAR: " + BSSID);
        intensidad = new ArrayList<Integer>();
        num_scan = new ArrayList<Integer>();
        probabilidad = new ArrayList<Float>();
        new ReadPlotDataTask(this).execute();
    }



    public void readData(){
        RedesDBHelper dbHelper = new RedesDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(RedesContract.Red.TABLE_NAME,
                new String[]{ /*Columnas a seleccionar*/
                        RedesContract.Red.COLUMN_NAME_INTENSIDAD,
                        RedesContract.Red.COLUMN_NAME_PROBABILIDAD,
                        RedesContract.Red.COLUMN_NAME_NUMSCAN
                },
                RedesContract.Red.COLUMN_NAME_BSSID+"=?",
                new String [] {BSSID},
                null,
                null,
                RedesContract.Red.COLUMN_NAME_NUMSCAN+" ASC");

        int index_intensidad = c.getColumnIndex(RedesContract.Red.COLUMN_NAME_INTENSIDAD);
        int index_prob = c.getColumnIndex(RedesContract.Red.COLUMN_NAME_PROBABILIDAD);
        int index_num_scan = c.getColumnIndex(RedesContract.Red.COLUMN_NAME_NUMSCAN);
        while(c.moveToNext()){
            int inte = c.getInt(index_intensidad);
            float prob = c.getFloat(index_prob);
            int n_scan = c.getInt(index_num_scan);
            intensidad.add(inte);
            num_scan.add(n_scan);
            probabilidad.add(prob);
        }
        c.close();
        db.close();
    }

    protected void configurePlots() {

        plotA.setTitle("Intensidad vs Tiempo");
        plotB.setTitle("Probabilidad de aparición");

        plotA.setDomainLabel("Número de Scan");
        plotA.setRangeLabel("Intensidad");

        plotB.setDomainLabel("Número de Scan");
        plotB.setRangeLabel("Probabilidad");

        plotA.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plotA.setDomainValueFormat(new DecimalFormat("#"));

        plotB.setTicksPerRangeLabel(2);
        plotB.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plotB.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.05);
        plotB.setDomainValueFormat(new DecimalFormat("#"));
        plotB.setRangeValueFormat(new DecimalFormat("#.##"));
        plotB.setRangeBoundaries(0, 1, BoundaryMode.FIXED);
    }
}