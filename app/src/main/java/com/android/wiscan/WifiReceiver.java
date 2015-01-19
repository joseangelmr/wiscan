package com.android.wiscan;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by David on 15/01/2015.
 */
public class WifiReceiver extends BroadcastReceiver{
    private WifiManager mainWifiObj;
    private String wifis[];
    private Context context;
    private ListView wifiList;
    private RedesDBHelper mDbHelper;
    private long timestamp;
    private double longitud;
    private double latitud;
    private int num_scan;

    WifiReceiver(Context c, WifiManager wm, ListView wl){
        mainWifiObj = wm;
        wifiList = wl;
        context =c;
        mDbHelper = new RedesDBHelper(c);
    }

    public void setVars(long time,int numscan,double longi,double lati){
        timestamp = time;
        longitud = longi;
        latitud = lati;
        num_scan = numscan;
    }



    private long insertData(ScanResult red){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(RedesContract.Red.COLUMN_NAME_TIEMPO, timestamp);
        values.put(RedesContract.Red.COLUMN_NAME_NUMSCAN, num_scan);
        values.put(RedesContract.Red.COLUMN_NAME_BSSID, red.BSSID);
        values.put(RedesContract.Red.COLUMN_NAME_SSID, red.SSID);
        values.put(RedesContract.Red.COLUMN_NAME_FRECUENCIA, red.frequency);
        values.put(RedesContract.Red.COLUMN_NAME_INTENSIDAD, red.level);
        values.put(RedesContract.Red.COLUMN_NAME_SEGURIDAD, red.capabilities);
        values.put(RedesContract.Red.COLUMN_NAME_LONGITUD, longitud);
        values.put(RedesContract.Red.COLUMN_NAME_LATITUD, latitud);


// Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                RedesContract.Red.TABLE_NAME,
                null,
                values);
        return newRowId;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
        //mDbHelper.onUpgrade(mDbHelper.getReadableDatabase(),1,1);
        for (ScanResult result : wifiScanList){
            insertData(result);
        }
        // Gets the data repository in write mode
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,)

            ScanResult[] wifis = new ScanResult[wifiScanList.size()];
            wifiScanList.toArray(wifis);
            WifiListAdapter adapter = new WifiListAdapter(this.context, R.layout.wifiitem, wifis);
            wifiList.setAdapter(adapter);
    }
}
