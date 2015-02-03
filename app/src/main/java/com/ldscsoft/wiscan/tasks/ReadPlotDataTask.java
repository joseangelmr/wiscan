package com.ldscsoft.wiscan.tasks;

import android.os.AsyncTask;

import com.ldscsoft.wiscan.activities.NetworkPlotActivity;
import com.ldscsoft.wiscan.helpers.AbstractDataPlot;

/**
 * Created by David on 24/01/2015.
 */
public class ReadPlotDataTask extends AsyncTask<Void,Void,Void> {

    private AbstractDataPlot actividad;
    public ReadPlotDataTask(AbstractDataPlot act){
        actividad = act;
    }



    @Override
    protected Void doInBackground(Void...params) {
        actividad.readData();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        actividad.postInUI();
    }
}