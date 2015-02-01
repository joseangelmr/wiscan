package com.ldscsoft.wiscan;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ListView;

import com.ldscsoft.wiscan.activities.MainActivity;
import com.ldscsoft.wiscan.database.RedesContract;
import com.ldscsoft.wiscan.database.RedesDBHelper;
import com.ldscsoft.wiscan.helpers.MyScanResult;
import com.ldscsoft.wiscan.tasks.InsertDataTask;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import static com.ldscsoft.wiscan.database.RedesContract.Red;
/**
 * Created by David on 15/01/2015.
 */
public class WifiReceiver extends BroadcastReceiver{

    private WifiManager mainWifiObj;
    private ListView wifiList;
    private RedesDBHelper mDbHelper;
    private long time_ini;
    private long time_fin;
    private Location mLocation_ini;
    private Location mLocation_fin;
    private int num_scan;
    private float discoveryRate =0;
    private int totalNetworks=0;

    private LinkedHashMap<String,MyScanResult> mapaRedes;
    private MainActivity mainActivity;

    public WifiReceiver(MainActivity activity, WifiManager wm, ListView wl){
        mainWifiObj = wm;
        wifiList = wl;
        mDbHelper = activity.getDbHelper();
        mainActivity = activity;
        mapaRedes =new LinkedHashMap<String, MyScanResult>();
    }

    public void updateValues(long time, int numscan, Location ini){
        time_ini = time;
        mLocation_ini = ini;
        num_scan = numscan;
    }

    private void insertData(MyScanResult red,int numeroDeScaneo){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

    /*Valores a insertar en la BD (UN registro completo)*/
        values.put(Red.COLUMN_NAME_NUMSCAN, numeroDeScaneo);
        values.put(Red.COLUMN_NAME_BSSID, red.BSSID);
        values.put(Red.COLUMN_NAME_SSID, red.SSID);
        values.put(Red.COLUMN_NAME_CANAL, red.channel);
        values.put(Red.COLUMN_NAME_INTENSIDAD, red.level);
        values.put(Red.COLUMN_NAME_SEGURIDAD, red.capabilities);
        values.put(Red.COLUMN_NAME_TIEMPO_I, time_ini);
        values.put(Red.COLUMN_NAME_TIEMPO_F, time_fin);
        values.put(Red.COLUMN_NAME_LONGITUD_I, mLocation_ini.getLongitude());
        values.put(Red.COLUMN_NAME_LATITUD_I, mLocation_ini.getLatitude());
        values.put(Red.COLUMN_NAME_LONGITUD_F, mLocation_fin.getLongitude());
        values.put(Red.COLUMN_NAME_LATITUD_F, mLocation_fin.getLatitude());
        values.put(Red.COLUMN_NAME_PROBABILIDAD,red.probability);
        values.put(Red.COLUMN_NAME_DISCOVERY_RATE,discoveryRate);
        values.put(Red.COLUMN_NAME_DETECTADA,Boolean.toString(red.detected));
        values.put(Red.COLUMN_NAME_PROBABILIDAD,red.probability);
        values.put(Red.COLUMN_NAME_TOTAL_REDES,totalNetworks);

        db.insert(RedesContract.Red.TABLE_NAME,null,values);
        db.close();
    }

    private void insertData(MyScanResult red){
        insertData(red,num_scan);
    }

    public void clearNetworks(){
        mapaRedes.clear();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.v("PRUEBA WIFI R","ENTRO EN ONRECEIVE, EN EL SCAN: "+num_scan);
        if(mainActivity.keepScanning()) {
  //          Log.v("PRUEBA WIFI R","SE LANZARA EL HILO EN EL SCAN: "+num_scan);
            time_fin = Calendar.getInstance().getTime().getTime();
            new InsertDataTask().execute(this);
        }
        else
            Log.v("PRUEBA WIFI R","NO SE LANZARA EL HILO Y EL SCAN ES: "+num_scan);

    }
    public void saveInDB(){
        mLocation_fin = LocationServices.
                FusedLocationApi.
                getLastLocation(mainActivity.mGoogleApiClient);

        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
        for (ScanResult result : wifiScanList) {
            MyScanResult aux = mapaRedes.get(result.BSSID);
            if (aux == null) { /*Si NO existe una red con ese BSSID*/
                totalNetworks++;
                Log.v("PRUEBA NUMERO REDES", "VALOR de totalNetworks: " + totalNetworks);
            }
        }

        for (ScanResult result : wifiScanList){
            MyScanResult aux = mapaRedes.get(result.BSSID);
            if(aux!=null) { /*Si ya existe una red con ese BSSID*/
                aux.updateDetectedResult(num_scan, result.level);
                insertData(aux);
                Log.v("PRUEBA TIMES","VALOR de timesDetected: "+aux.timesDetected);
            }
            else {
                aux = new MyScanResult(result, num_scan);
                mapaRedes.put(result.BSSID,aux);
                /*copiar el objeto para poder modificarlo sin
                 cambiar lo que se mostrara en el list view*/

                MyScanResult aux_save = new MyScanResult(aux);
                aux_save.probability = 0;
                aux_save.level=-100; /*Valor minimo en el contexto de RSSI*/
                aux_save.detected=false;
                for(int i =1;i<num_scan;i++){
                    insertData(aux_save,i);
                }
                /*Aqui se inserta la data del scan actual*/
                insertData(aux);
            }
        }

        for(MyScanResult result : mapaRedes.values()){
            if(!result.detected) {
                result.updateNotDetectedResult(num_scan);
                MyScanResult aux_save = new MyScanResult(result);
                aux_save.level=-100;
                insertData(aux_save);
            }
            /*Para que este listo para el proximo scan
            * volvemos a ponerlos en false*/
            result.detected=false;
        }

        /*Solo se verifica por si en el primer scan (y siguientes ) no detecta redes*/
        if(mapaRedes.size()>0) {
            discoveryRate = (float) wifiScanList.size() / mapaRedes.size();
        }
        mainActivity.setTextViewValues(discoveryRate,time_fin-time_ini,mapaRedes.size());
    }

    public void postInUI(){
        WifiListAdapter adapter = (WifiListAdapter) wifiList.getAdapter();
        adapter.clear();
        for (MyScanResult result : mapaRedes.values())
            adapter.add(result);

        adapter.notifyDataSetChanged();
        mainActivity.updateTextViews();
        if(mainActivity.keepScanning()) {
            //Log.v("PRUEBA WIFI R","SIGO SCANEANDO");
            mainActivity.scanearRedes();
        }
        else
            Log.v("PRUEBA WIFI R","YA NO SCANEO MAS");
    }
}