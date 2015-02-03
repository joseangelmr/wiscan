package com.ldscsoft.wiscan.helpers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TabHost;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.ldscsoft.wiscan.R;
import com.ldscsoft.wiscan.tasks.ReadPlotDataTask;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by David on 02/02/2015.
 */
public abstract class AbstractDataPlot extends ActionBarActivity{

    private TabHost tabs;
    protected XYPlot plotA;
    protected XYPlot plotB;
    private String nameA;
    private String nameB;

    public static final String NAMEA = "nameA";
    public static final String NAMEB = "nameB";

    public abstract void postInUI();
    public abstract void readData();
    protected abstract void configurePlots();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        nameA  = getIntent().getStringExtra(NAMEA);
        nameB  = getIntent().getStringExtra(NAMEB);
        createTabs();
        configurePlots();
        Log.v("PRUEBA ABSTRACTPLOT", "ENTRO AL OnCreate DE ABSTRACT");
    }


    private void createTabs() {
        tabs=(TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();
        /*Se agrega el primer TAB*/
        TabHost.TabSpec spec=tabs.newTabSpec(nameA);
        spec.setContent(R.id.plotA);
        spec.setIndicator(nameA);
        tabs.addTab(spec);

        /*Se agrega el segundo TAB*/
        spec=tabs.newTabSpec(nameB);
        spec.setContent(R.id.plotB);
        spec.setIndicator(nameB);
        tabs.addTab(spec);

        tabs.setCurrentTab(0);

        plotA = (XYPlot) tabs.getTabContentView().getChildAt(0).findViewById(R.id.WiScanPlot);
        plotB = (XYPlot) tabs.getTabContentView().getChildAt(1).findViewById(R.id.WiScanPlot);
    }

    public void setPlotData(ArrayList<? extends Number> domain,ArrayList<? extends Number> range,boolean isA,String nombre){
        int formatter_id;
        if(isA) {
            formatter_id = R.xml.line_point_formatter_with_plf1;
        }
        else {
            formatter_id = R.xml.line_point_formatter_with_plf2;
        }
        XYSeries serie = new SimpleXYSeries(domain,range,nombre);
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.configure(getApplicationContext(),formatter_id);
        if(isA)
            plotA.addSeries(serie, seriesFormat);
        else
            plotB.addSeries(serie, seriesFormat);
    }
}