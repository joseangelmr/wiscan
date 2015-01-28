package com.android.wiscan;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Location;
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
import com.android.wiscan.helpers.DialogHelper;
import com.android.wiscan.helpers.GooglePlayCallbacks;
import com.android.wiscan.helpers.MyScanResult;
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
    private RedesDBHelper dbHelper;
    private boolean keep_scaning = false;
    private int num_scan=0;
    private int max_scan_pref;
    public GoogleApiClient mGoogleApiClient;

    public boolean keepScanning() {
        return keep_scaning;
    }

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
        //wifiList.addHeaderView(View.inflate(this, R.layout.wifiheader, null));
        /*wifiList.setOverscrollHeader(getResources().getDrawable(R.drawable.powered_by_google_light));*/
        WifiListAdapter adapter = new WifiListAdapter(this,R.layout.wifiitem,new ArrayList<MyScanResult>());
        wifiList.setAdapter(adapter);
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MyScanResult aux = ((MyScanResult)adapterView.getItemAtPosition(position));
                Intent intent = new Intent(getApplicationContext(),DataPlotActivity.class);
                /*Se pasa el BSSID para graficar la data de ese AP especifico*/
                intent.putExtra(Intent.EXTRA_TEXT, aux.BSSID);
                startActivity(intent);
            }
        });
        //wifiList.addHeaderView(getLayoutInflater().inflate(R.layout.wifiheader,getconte));
    }

    public void updateNumScan() {
        scaneos.setText("Scan: "+String.valueOf(num_scan)+" / "+String.valueOf(max_scan_pref));
    }

    public void deleteDB(){
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 1);
    }

    public void scanearRedes() {
        if(num_scan<max_scan_pref && keep_scaning) {
            Location location_ini = LocationServices.
                    FusedLocationApi.
                    getLastLocation(mGoogleApiClient);
            if (location_ini != null) {
                long seconds = Calendar.getInstance().getTime().getTime();
                if (mainWifiObj.startScan()) {
                    Log.v("PRUEBA SCAN","SI SE INICIO EL SCAN: "+num_scan);
                    num_scan++;
                    wifiReceiver.updateValues(seconds, num_scan, location_ini);
                }
                else
                    Log.v("PRUEBA SCAN","NO NO SE INICIO EL SCAN: "+num_scan);
            }
            else
                Log.v("PRUEBA SCAN","LA LOCALIZACION ES NULA EN EL SCAN: "+num_scan);
        }
        else {
            stop_scan();
        }
    }

    private void stop_scan() {
        keep_scaning = false;
        //num_scan=0;
        supportInvalidateOptionsMenu();
        DialogHelper dialogHelper = new DialogHelper(this);
        dialogHelper.showSelectionDialog();
    }

   /* public void imprimirDBenLog() {
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
                    String PROB = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_PROBABILIDAD));

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
                            PROB+" "+
                            NS
                    );
        }
    }*/

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("PRUEBA MAIN","SE LLAMO ONCREATE");
        configurarWifiList();

        dbHelper = new RedesDBHelper(this);

        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!mainWifiObj.isWifiEnabled()){
            Toast.makeText(this,"Encendiendo Wifi",Toast.LENGTH_SHORT).show();
            mainWifiObj.setWifiEnabled(true);
        }
        buildGoogleApiClient();
        wifiReceiver = new WifiReceiver(this,mainWifiObj,wifiList);
        scaneos = (TextView)findViewById(R.id.scan_actual);

        /*Se registra el receiver para manejar la data del scan*/
        registerReceiver(wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setEnabled(!keep_scaning);
        menu.getItem(1).setEnabled(keep_scaning);
        menu.getItem(2).setEnabled(!keep_scaning);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_start:
                deleteDB();
                wifiReceiver.clearNetworks();
                keep_scaning = true;
                num_scan=0;
                updateNumScan();
                if(mGoogleApiClient.isConnected())
                    mGoogleApiClient.reconnect();
                else
                    mGoogleApiClient.connect();
                supportInvalidateOptionsMenu();
                return true;
            case R.id.action_stop:
                stop_scan();
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
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        max_scan_pref = Integer.valueOf(pref.getString("max_scan","100"));/*100 Por defecto*/
        updateNumScan();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(wifiReceiver);

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }
}