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
import android.widget.ListView;

import com.android.wiscan.database.RedesContract;
import com.android.wiscan.database.RedesDBHelper;
import com.google.android.gms.location.LocationServices;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
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
    MainActivity mainActivity;

    WifiReceiver(Context c, WifiManager wm, ListView wl){
        mainWifiObj = wm;
        wifiList = wl;
        mDbHelper = new RedesDBHelper(c);
        mainActivity = ((MainActivity) c);
        mDbHelper.onUpgrade(mDbHelper.getWritableDatabase(),1,1);
    }

    public void updateValues(long time, int numscan, Location ini){
        timestamp = time;
        mLocation_ini = ini;
        num_scan = numscan;
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

    @Override
    public void onReceive(Context context, Intent intent) {
        /*TODO Crear tarea asincrona para guardar en la BD
        asi se previene en caso que sean muchos resultados en un scan*/

        mLocation_fin = LocationServices.
                        FusedLocationApi.
                        getLastLocation(mainActivity.mGoogleApiClient);

        WifiListAdapter adapter = ((WifiListAdapter) wifiList.getAdapter());
        adapter.clear();
        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
        ArrayList<MyScanResult> redes = new ArrayList<MyScanResult>();
        for (ScanResult result : wifiScanList){
            float prob = insertData(result);
            redes.add(new MyScanResult(result,prob));
        }
        adapter.addAll(redes);
        adapter.notifyDataSetChanged();
        mainActivity.updateNumScan();
        if(mainActivity.isScanning())
            mainActivity.scanearRedes();
    }
}