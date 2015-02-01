package com.ldscsoft.wiscan.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TabHost;

import com.ldscsoft.wiscan.R;
import com.ldscsoft.wiscan.tasks.ReadPlotDataTask;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by David on 31/01/2015.
 */
public class MainValuesPlotActivity extends Activity {

    public MainValuesPlotActivity(){}


    private TabHost tabs;
    private XYPlot plotIntensidad;
    private XYPlot plotProbabilidad;

    private Integer networkCountList[];
    private Float discoveryRatetList[];


    private void createTabs() {
        tabs=(TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();
        /*Se agrega el primer TAB*/
        TabHost.TabSpec spec=tabs.newTabSpec("Redes detectadas");
        spec.setContent(R.id.plotIntensidad);
        spec.setIndicator("Redes detectadas");
        tabs.addTab(spec);

        /*Se agrega el segundo TAB*/
        spec=tabs.newTabSpec("Discovery Rate");
        spec.setContent(R.id.plotProbabilidad);
        spec.setIndicator("Discovery Rate");
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dataplotmain);
        /*Se le suma +1 para compensar por el scan cero*/
        int [] intList = getIntent().getIntArrayExtra("NETWORK_COUNT");
        networkCountList = new Integer[intList.length+1];
        networkCountList[0] = 0;
        for(int i=1;i<intList.length+1;i++)
            networkCountList[i] = intList[i];

        float [] floatList = getIntent().getFloatArrayExtra("DISCOVERY_RATE");
        discoveryRatetList = new Float[floatList.length+1];
        discoveryRatetList[0] = 0.0f;
        for(int i=1;i<floatList.length+1;i++)
            discoveryRatetList[i] = floatList[i];

        Log.v("PRUEBA SEGUNDO PLOT","TAMANIO DE LAS LISTAS: "
                +networkCountList.length+" y "+discoveryRatetList.length);

        createTabs();

        configurePlots();

        setPlotData(true);

        setPlotData(false);

    }
    public void setPlotData(boolean isNetworks){
        String nombre;
        int formatter_id;
        XYSeries serie;
        if(isNetworks) {
            nombre = "Redes Detectadas vs Num. de Scan";
            formatter_id = R.xml.line_point_formatter_with_plf1;
            serie = new SimpleXYSeries(Arrays.asList(networkCountList), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,nombre);
        }
        else {
            nombre = "Discovery Rate vs Num. de Scan";
            formatter_id = R.xml.line_point_formatter_with_plf2;
            serie = new SimpleXYSeries(Arrays.asList(discoveryRatetList), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,nombre);
        }


        LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
        seriesFormat.configure(getApplicationContext(),formatter_id);
        if(isNetworks)
            plotIntensidad.addSeries(serie, seriesFormat);
        else
            plotProbabilidad.addSeries(serie, seriesFormat);

    }

    private void configurePlots() {
        plotIntensidad = (XYPlot) tabs.getTabContentView().getChildAt(0).findViewById(R.id.mySimpleXYPlot);
        plotProbabilidad = (XYPlot) tabs.getTabContentView().getChildAt(1).findViewById(R.id.mySimpleXYPlot);

        plotIntensidad.setTitle("Redes Detectadas vs Tiempo");
        plotProbabilidad.setTitle("DIscovery Rate");

        plotIntensidad.setDomainLabel("Número de Scan");
        plotIntensidad.setRangeLabel("Redes Detectadas");

        plotProbabilidad.setDomainLabel("Número de Scan");
        plotProbabilidad.setRangeLabel("Discovery Rate");

        plotIntensidad.setDomainStep(XYStepMode.INCREMENT_BY_VAL,1);
        plotIntensidad.setDomainValueFormat(new DecimalFormat("#"));

        plotProbabilidad.setTicksPerRangeLabel(2);
        plotProbabilidad.setDomainStep(XYStepMode.INCREMENT_BY_VAL,1);
        plotProbabilidad.setRangeStep(XYStepMode.INCREMENT_BY_VAL,0.05);
        plotProbabilidad.setDomainValueFormat(new DecimalFormat("#"));
        plotProbabilidad.setRangeValueFormat(new DecimalFormat("#.###"));
        plotProbabilidad.setRangeBoundaries(0,1, BoundaryMode.FIXED);
    }








}
