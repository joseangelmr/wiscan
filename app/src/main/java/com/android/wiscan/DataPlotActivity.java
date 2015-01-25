package com.android.wiscan;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TabHost;

import com.android.wiscan.tasks.ReadPlotDataTask;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A straightforward example of using AndroidPlot to plot some data.
 */
public class DataPlotActivity extends Activity
{

    private TabHost tabs;
    private XYPlot plotIntensidad;
    private XYPlot plotProbabilidad;
    private String BSSID;
    public String getBSSID() {
        return BSSID;
    }



    private void createTabs() {
        //        Resources res = getResources();

        tabs=(TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();

        /*Se agrega el primer TAB*/
        TabHost.TabSpec spec=tabs.newTabSpec("Intensidad");
        spec.setContent(R.id.plotIntensidad);
        spec.setIndicator("Intensidad");
        /*spec.setIndicator("",
                res.getDrawable(android.R.drawable.ic_btn_speak_now));*/
        tabs.addTab(spec);

        /*Se agrega el segundo TAB*/
        spec=tabs.newTabSpec("Probabilidad");
        spec.setContent(R.id.plotProbabilidad);
        spec.setIndicator("Probabilidad");
        /*spec.setIndicator("TAB2",
                res.getDrawable(android.R.drawable.ic_dialog_map));*/
        tabs.addTab(spec);
        tabs.setCurrentTab(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dataplotmain);
        BSSID = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        Log.v("PRUEBA PLOT", "BSSID A PLOTEAR: " + BSSID);

        createTabs();

        configurePlots();

        new ReadPlotDataTask().execute(this);
    }
    public void setPlotData(ArrayList<? extends Number> domain,ArrayList<? extends Number> range,boolean isIntensidad){
        String nombre;
        int formatter_id;
        if(isIntensidad) {
            nombre = "Intensidad";
            formatter_id = Color.BLUE;
        }
        else {
            nombre = "Probabilidad";
            formatter_id = Color.GREEN;
        }

        XYSeries serie = new SimpleXYSeries(domain,range,nombre);

        /*LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
        seriesFormat.configure(getApplicationContext(),formatter_id);*/
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, formatter_id, Shader.TileMode.MIRROR));


        StepFormatter seriesFormat = new StepFormatter(Color.rgb(0, 0,0), formatter_id);

        seriesFormat.getLinePaint().setStrokeWidth(5);
        seriesFormat.getLinePaint().setAntiAlias(false);
        seriesFormat.setFillPaint(lineFill);

        if(isIntensidad)
            plotIntensidad.addSeries(serie, seriesFormat);
        else
            plotProbabilidad.addSeries(serie, seriesFormat);

    }

    private void configurePlots() {
        plotIntensidad = (XYPlot) tabs.getTabContentView().getChildAt(0).findViewById(R.id.mySimpleXYPlot);
        plotProbabilidad = (XYPlot) tabs.getTabContentView().getChildAt(1).findViewById(R.id.mySimpleXYPlot);

        plotIntensidad.setTitle("Intensidad vs Tiempo");
        plotProbabilidad.setTitle("Probabilidad de aparición");

        plotIntensidad.setDomainLabel("Número de Scan");
        plotIntensidad.setRangeLabel("Intensidad");

        plotProbabilidad.setDomainLabel("Número de Scan");
        plotProbabilidad.setRangeLabel("Probabilidad");

        plotIntensidad.setDomainStep(XYStepMode.INCREMENT_BY_VAL,1);
        plotIntensidad.setDomainValueFormat(new DecimalFormat("#"));

        plotProbabilidad.setTicksPerRangeLabel(2);
        plotProbabilidad.setDomainStep(XYStepMode.INCREMENT_BY_VAL,1);
        plotProbabilidad.setRangeStep(XYStepMode.INCREMENT_BY_VAL,0.05);
        plotProbabilidad.setDomainValueFormat(new DecimalFormat("#"));
        plotProbabilidad.setRangeValueFormat(new DecimalFormat("#.###"));
        plotProbabilidad.setRangeBoundaries(0,1,BoundaryMode.FIXED);
    }
}