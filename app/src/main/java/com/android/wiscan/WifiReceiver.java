package com.android.wiscan;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.android.wiscan.database.RedesContract;
import com.android.wiscan.database.RedesDBHelper;
import com.android.wiscan.helpers.MyScanResult;
import com.android.wiscan.tasks.InsertDataTask;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by David on 15/01/2015.
 */
public class WifiReceiver extends BroadcastReceiver{

    private WifiManager mainWifiObj;
    private ListView wifiList;
    private RedesDBHelper mDbHelper;
    private long timestamp;
    private Location mLocation_ini;
    private Location mLocation_fin;
    private int num_scan;
    private ArrayList<MyScanResult> redes;

    LinkedHashMap<String,MyScanResult> mapa_redes;

    private MainActivity mainActivity;

    WifiReceiver(Context c, WifiManager wm, ListView wl){
        mainWifiObj = wm;
        wifiList = wl;
        mDbHelper = new RedesDBHelper(c);
        mainActivity = ((MainActivity) c);
        mapa_redes=new LinkedHashMap<String, MyScanResult>();
    }

    public void updateValues(long time, int numscan, Location ini){
        timestamp = time;
        mLocation_ini = ini;
        num_scan = numscan;
    }

    public int calcularCanal(int frecuencia){
/* CANAL, FRECUENCIA
        1	2412
        2	2417
        3	2422
        4	2427
        5	2432
        6	2437
        7	2442
        8	2447
        9	2452
        10	2457
        11	2462
        12	2467
        13	2472
        */

        int mod = frecuencia % 2412;

        int channel = (mod/5)+1;
        return channel;
    }

    /*Retorna la probabilidad para esa red, durante ese escaneo*/
    private float insertData(ScanResult red){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
    /*Calcular la probabilidad de aparicion de esta red*/
        Cursor c = db.rawQuery(mDbHelper.SQL_COUNT_NETWORK,new String[]{red.BSSID});
        c.moveToNext();
        int detecciones = c.getInt(c.getColumnIndex("detecciones"))+1;
        float prob = (float)detecciones/ num_scan;

        values.put(RedesContract.Red.COLUMN_NAME_TIEMPO, timestamp);
        values.put(RedesContract.Red.COLUMN_NAME_NUMSCAN, num_scan);
        values.put(RedesContract.Red.COLUMN_NAME_BSSID, red.BSSID);
        values.put(RedesContract.Red.COLUMN_NAME_SSID, red.SSID);
        values.put(RedesContract.Red.COLUMN_NAME_FRECUENCIA, red.frequency);
        values.put(RedesContract.Red.COLUMN_NAME_INTENSIDAD, red.level);
        values.put(RedesContract.Red.COLUMN_NAME_SEGURIDAD, red.capabilities);
        values.put(RedesContract.Red.COLUMN_NAME_LONGITUD_I, mLocation_ini.getLongitude());
        values.put(RedesContract.Red.COLUMN_NAME_LATITUD_I, mLocation_ini.getLatitude());
        values.put(RedesContract.Red.COLUMN_NAME_LONGITUD_F, mLocation_fin.getLongitude());
        values.put(RedesContract.Red.COLUMN_NAME_LATITUD_F, mLocation_fin.getLatitude());
        values.put(RedesContract.Red.COLUMN_NAME_PROBABILIDAD, prob);

        db.insert(RedesContract.Red.TABLE_NAME,null,values);
        db.close();
        //En vez de retornar el id de la fila, se retorna la probabilidad para esa red
        return prob;
    }

    public void clearNetworks(){
        mapa_redes.clear();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("PRUEBA WIFI R","ENTRO EN ONRECEIVE, EN EL SCAN: "+num_scan);
        if(mainActivity.keepScanning()) {
            Log.v("PRUEBA WIFI R","SE LANZARA EL HILO EN EL SCAN: "+num_scan);
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
        redes = new ArrayList<MyScanResult>();
        for (ScanResult result : wifiScanList){
            float prob = insertData(result);
            MyScanResult aux = mapa_redes.get(result.BSSID);
            if(aux!=null) { /*Si ya existe una red con ese BSSID*/
                aux.updateDetectedResult(num_scan,result.level);
                Log.v("PRUEBA TIMES","VALOR de timesDetected: "+aux.timesDetected);
            }
            else
                mapa_redes.put(result.BSSID,new MyScanResult(result,prob));

            //redes.add(new MyScanResult(result,prob));
        }
        for(MyScanResult result : mapa_redes.values()){
            result.updateNotDetectedResult(num_scan);
        }
    }

    public void postInUI(){
        /*WifiListAdapter adapter = ((WifiListAdapter) ((HeaderViewListAdapter) wifiList.getAdapter()).getWrappedAdapter());*/
        WifiListAdapter adapter = (WifiListAdapter) wifiList.getAdapter();
        adapter.clear();
        for (MyScanResult result :mapa_redes.values())
            adapter.add(result);
        //adapter.getPosition()
        adapter.notifyDataSetChanged();
        mainActivity.updateNumScan();
        if(mainActivity.keepScanning()) {
            Log.v("PRUEBA WIFI R","SIGO SCANEANDO");
            mainActivity.scanearRedes();
        }
        else
            Log.v("PRUEBA WIFI R","YA NO SCANEO MAS");
    }
}