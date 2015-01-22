package com.android.wiscan;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wiscan.database.RedesContract;
import com.android.wiscan.database.RedesDBHelper;
import com.android.wiscan.preferencias.PreferenciasActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity {

    private WifiManager mainWifiObj;
    private WifiReceiver wifiReceiver;
    private ListView wifiList;
    private TextView scaneos;
    private Location location_ini;

    public GoogleApiClient mGoogleApiClient;
    private int num_scan=0;
    int max_scan_pref;

    //private Timer timer;

    private  void buildGoogleApiClient() {
        GooglePlayCallbacks gpCallbacks;
        gpCallbacks = new GooglePlayCallbacks(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(gpCallbacks)
                .addOnConnectionFailedListener(gpCallbacks)
                .addApi(LocationServices.API)
                .build();
    }

    private void configurarWifiList() {
        wifiList = (ListView)findViewById(R.id.wifiListView);
        wifiList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        try {
        WifiListAdapter adapter = new WifiListAdapter(this,R.layout.wifiitem,new ArrayList<ScanResult>());
        wifiList.setAdapter(adapter);
  //      }
    //    catch (NullPointerException e){
      //      Log.v("PRUEBA NULL DATA",e.toString());
        //}


        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //TODO Implementar funcionalidad correcta
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"SE SELECCIONO LA POSICION: "+position,Toast.LENGTH_SHORT).show();
                String aux = ((ScanResult)adapterView.getItemAtPosition(position)).toString();
                Log.v("PRUEBA-ITEM-SELECT",aux);
                Intent intent = new Intent(getApplicationContext(),SimpleXYPlotActivity.class);
                String message = "MENSAJE DE PRUEBA";
                intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(intent);
            }
        });
    }

    private void updateNumScan() {
        scaneos.setText("Scan: "+String.valueOf(num_scan)+" / "+String.valueOf(max_scan_pref));
    }

    public void scanearRedes() {
        if(num_scan<max_scan_pref) {
            location_ini = LocationServices.
                    FusedLocationApi.
                    getLastLocation(mGoogleApiClient);
            if (location_ini != null) {
                long seconds = Calendar.getInstance().getTime().getTime();
                if (mainWifiObj.startScan()) {
                    num_scan++;
                    updateNumScan();
                    wifiReceiver.updateValues(seconds, num_scan, location_ini);
                }
            }
        }
        else
            //TODO crear metodo para mostrar el cuadro de exportar la data
            imprimirDBenLog();
    }

    private void imprimirDBenLog() {
        RedesDBHelper DbHelper;
        DbHelper = new RedesDBHelper(this);
        SQLiteDatabase db= DbHelper.getReadableDatabase();


        Cursor c = db.query(
                RedesContract.Red.TABLE_NAME,  // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        while(c.moveToNext()){
            String BSSID = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_BSSID));
                    String SSID = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_SSID));
                    String NIVEL = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_INTENSIDAD));
                    String FREC = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_FRECUENCIA));
                    String SEGU = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_SEGURIDAD));
                    String LONGI_I = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LONGITUD_I));
                    String LATI_I = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LATITUD_I));
                    String LONGI_F = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LONGITUD_F));
                    String LATI_F = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LATITUD_F));
                    String TIME = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_TIEMPO));
                    String NS = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_NUMSCAN));

                    Log.v("PRUEBA SCAN",BSSID+" "+
                            LONGI_I+" "+
                            LATI_I+" "+
                            SSID+" "+
                            NIVEL+" "+
                            FREC+" "+
                            SEGU+" "+
                            TIME+" "+
                            LONGI_F+" "+
                            LATI_F+" "+
                            NS
                    );
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configurarWifiList();

        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!mainWifiObj.isWifiEnabled()){
            Toast.makeText(this,"Encendiendo Wifi",Toast.LENGTH_SHORT).show();
            mainWifiObj.setWifiEnabled(true);
        }
        buildGoogleApiClient();
        wifiReceiver = new WifiReceiver(this,mainWifiObj,wifiList);
        scaneos = (TextView)findViewById(R.id.scan_actual);
    }

    //Ojo prueba de scan periodico
    //NO USADO. Se desea scanear apenas termine otro scan
//    public void Asyncwifi() {
//        final Handler  whandler = new Handler();
//        timer = new Timer();
//        TimerTask doAsynchronousTask = new TimerTask() {
//
//            @Override
//            public void run() {
//                whandler.post(new Runnable() {
//                    public void run() {
//                        try {
//                            //mainWifiObj.startScan();
//                            mGoogleApiClient.reconnect();
//                        }catch (Exception e) {}
//                    }
//                });
//            }
//        };
//        timer.schedule(doAsynchronousTask, 0, 5000); // Repeate in every 5 sec
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(1).setEnabled(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_start:
                mGoogleApiClient.connect();
                invalidateOptionsMenu();
                //TODO Implementar funcionalidad correcta
                mainWifiObj.startScan();
                return true;
            case R.id.action_stop:
                //TODO Implementar funcionalidad correcta
                //timer.cancel();
                return true;
            case R.id.action_options:
                startActivity(new Intent(MainActivity.this,
                                        PreferenciasActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        max_scan_pref = Integer.valueOf(pref.getString("max_scan","100"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReceiver,
                        new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
}