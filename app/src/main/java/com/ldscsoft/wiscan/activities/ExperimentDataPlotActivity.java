package com.ldscsoft.wiscan.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import com.ldscsoft.wiscan.R;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.ldscsoft.wiscan.database.RedesContract;
import com.ldscsoft.wiscan.database.RedesDBHelper;
import com.ldscsoft.wiscan.helpers.AbstractDataPlot;
import com.ldscsoft.wiscan.tasks.ReadPlotDataTask;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by David on 31/01/2015.
 */
public class ExperimentDataPlotActivity extends AbstractDataPlot {


    private ArrayList<Integer> networkCount;
    private ArrayList<Float> discoveryRate;
    private ArrayList<Integer> num_scan;

    @Override
    public void postInUI() {
        setPlotData(num_scan, networkCount, true, "N° de redes");
        setPlotData(num_scan, discoveryRate, false, "Discovery rate");
    }

    @Override
    public void readData(){
        RedesDBHelper dbHelper = new RedesDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(true,RedesContract.Red.TABLE_NAME,
                new String[]{ /*Columnas a seleccionar*/
                        RedesContract.Red.COLUMN_NAME_NUMSCAN,
                        RedesContract.Red.COLUMN_NAME_TOTAL_REDES,
                        RedesContract.Red.COLUMN_NAME_DISCOVERY_RATE
                },
                null,
                null,
                null,
                null,
                RedesContract.Red.COLUMN_NAME_NUMSCAN+" ASC",null);

        int index_total_redes = c.getColumnIndex(RedesContract.Red.COLUMN_NAME_TOTAL_REDES);
        int index_discovery = c.getColumnIndex(RedesContract.Red.COLUMN_NAME_DISCOVERY_RATE);
        int index_num_scan = c.getColumnIndex(RedesContract.Red.COLUMN_NAME_NUMSCAN);
        Log.v("PRUEBA LEER EXPERIMENT","CANTIDAD DE VALORES EN EL CURSOR: "+c.getCount());
        while(c.moveToNext()){
            int total = c.getInt(index_total_redes);
            float disco = c.getFloat(index_discovery);
            int n_scan = c.getInt(index_num_scan);
            Log.v("PRUEBA LEER EXPERIMENT",total+" ** "+disco+" ** "+n_scan);
            networkCount.add(total);
            num_scan.add(n_scan);
            discoveryRate.add(disco);
        }
        c.close();
        db.close();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("PRUEBA PLOT", "EXPERIMENT DATA: ");
        networkCount = new ArrayList<Integer>();
        num_scan = new ArrayList<Integer>();
        discoveryRate = new ArrayList<Float>();
        new ReadPlotDataTask(this).execute();
    }

    protected void configurePlots() {

        plotA.setDomainLabel("Número de Scan");
        plotA.setRangeLabel("N° de redes");

        plotB.setDomainLabel("Número de Scan");
        plotB.setRangeLabel("Discovery rate");

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
