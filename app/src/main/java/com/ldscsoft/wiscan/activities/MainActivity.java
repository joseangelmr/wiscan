package com.ldscsoft.wiscan.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.ldscsoft.wiscan.R;
import com.ldscsoft.wiscan.WifiListAdapter;
import com.ldscsoft.wiscan.WifiReceiver;
import com.ldscsoft.wiscan.database.RedesDBHelper;
import com.ldscsoft.wiscan.helpers.AbstractDataPlot;
import com.ldscsoft.wiscan.helpers.DialogHelper;
import com.ldscsoft.wiscan.helpers.GooglePlayCallbacks;
import com.ldscsoft.wiscan.helpers.MyScanResult;
import com.ldscsoft.wiscan.preferencias.PreferenciasActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity {

    private WifiManager mainWifiObj;
    private WifiReceiver wifiReceiver;
    private ListView wifiList;
    private TextView scaneos;
    private TextView discovery_rate;
    private TextView scan_time;
    private TextView network_count;

    private LinearLayout boton_graficar;
    public boolean semaforo =false;
    public boolean semaforoLocation=false;


    public RedesDBHelper getDbHelper() {
        return dbHelper;
    }

    private RedesDBHelper dbHelper;
    private boolean keep_scaning = false;
    private int max_scan_pref;

    public GoogleApiClient mGoogleApiClient;

    private int num_scan=0;
    private float discoveryRate=0;
    private int networkCount=0;
    private float scanTime=0;

    private LocationRequest mLocationRequest;

    private GooglePlayCallbacks gpCallbacks;

    public void setTextViewValues(float discoveryrate,float scantime,int networkcount) {
        this.discoveryRate = discoveryrate;
        this.scanTime = scantime;
        this.networkCount = networkcount;

        /*Log.v("PRUEBA DE LISTAS","VALOR DE NUM SCAN: "+num_scan);
        discoveryRatetList.add(num_scan-1,discoveryRate);
        networkCountList.add(num_scan-1,networkCount);*/

    }

    public boolean keepScanning() {
        return keep_scaning;
    }

    private  void buildGoogleApiClient() {
        gpCallbacks = new GooglePlayCallbacks(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(gpCallbacks)
                .addOnConnectionFailedListener(gpCallbacks)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
    }

    private void configurarWifiList() {
        wifiList = (ListView)findViewById(R.id.wifiListView);
        wifiList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        WifiListAdapter adapter = new WifiListAdapter(this,R.layout.wifiitem,new ArrayList<MyScanResult>());
        wifiList.setAdapter(adapter);
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MyScanResult aux = ((MyScanResult)adapterView.getItemAtPosition(position));
                Intent intent = new Intent(getApplicationContext(),NetworkPlotActivity.class);
                /*Se pasa el BSSID para graficar la data de ese AP especifico*/
                intent.putExtra(Intent.EXTRA_TEXT, aux.BSSID);
                intent.putExtra(AbstractDataPlot.NAMEA, "Intensidad");
                intent.putExtra(AbstractDataPlot.NAMEB, "Probabilidad");
                startActivity(intent);
            }
        });
    }

    public void updateTextViews() {
        scaneos.setText("Escaneo actual: "+String.valueOf(num_scan)+" / "+String.valueOf(max_scan_pref));
        discovery_rate.setText(String.format("Discovery rate: %.2f",discoveryRate));
        network_count.setText("Redes detectadas.: " + String.valueOf(networkCount));
        scan_time.setText(String.format("Tiempo de escaneo(s): %.2f",scanTime/1000));
    }

    public void deleteDB(){
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 1);
    }


    private void checkWifiState(){
        if(!mainWifiObj.isWifiEnabled()){
            Toast.makeText(this,"Encendiendo Wifi",Toast.LENGTH_SHORT).show();
            mainWifiObj.setWifiEnabled(true);
            Log.v("PRUEBA BUCLE","ANTES DEL WHILE");
            while(mainWifiObj.getWifiState()==WifiManager.WIFI_STATE_ENABLING);
            Log.v("PRUEBA BUCLE","DESPUES DEL WHILE");

//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER ))
//            {
//                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
//                startActivity(myIntent);
//            }
        }
    }
    public void scanearRedes() {
        if(num_scan<max_scan_pref && keep_scaning) {
            Location location_ini = LocationServices.
                    FusedLocationApi.
                    getLastLocation(mGoogleApiClient);
            if (location_ini != null) {
                long seconds = Calendar.getInstance().getTime().getTime();
                Log.v("PRUEBA MAIN LOC INI","("+location_ini.getLatitude()+","+location_ini.getLongitude()+")");

                if (mainWifiObj.startScan()) {
                    semaforo = true;
                    num_scan++;
                    Log.v("PRUEBA SCAN","SI SE INICIO EL SCAN: "+num_scan);
                    wifiReceiver.updateValues(seconds, num_scan, location_ini);
                }
                else
                    Log.v("PRUEBA SCAN","NO NO SE INICIO EL SCAN: "+num_scan);
            }
            else {
                //if(semaforoLocation)
                Log.v("PRUEBA SCAN", "LA LOCALIZACION ES NULA EN EL SCAN: " + num_scan);
                semaforoLocation = true;
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, gpCallbacks);
            }
        }
        else {
            stop_scan();
        }
        Log.v("PRUEBA MAIN","SALIENDO DE SCANEAR REDES");
    }

    private void stop_scan() {
//        disconnectLocation();
        keep_scaning = false;
        supportInvalidateOptionsMenu();
        DialogHelper dialogHelper = new DialogHelper(this);
        dialogHelper.showSelectionDialog();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("PRUEBA MAIN","SE LLAMO ONCREATE");

        configurarWifiList();

        dbHelper = new RedesDBHelper(this);



        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        checkWifiState();

        buildGoogleApiClient();
        wifiReceiver = new WifiReceiver(this,mainWifiObj,wifiList);

    /*Inicializacion de los TextView*/
        scaneos = (TextView)findViewById(R.id.scan_actual);
        discovery_rate = (TextView)findViewById(R.id.discovery_rate);
        scan_time= (TextView)findViewById(R.id.scan_time);
        network_count = (TextView)findViewById(R.id.network_count);
        boton_graficar = (LinearLayout) findViewById(R.id.graficar);
        boton_graficar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ExperimentDataPlotActivity.class);
                intent.putExtra(AbstractDataPlot.NAMEA, "Número de redes");
                intent.putExtra(AbstractDataPlot.NAMEB, "Discovery rate");
                startActivity(intent);
            }
        });
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

        boton_graficar.setEnabled(!keep_scaning);
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
                updateTextViews();
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
            case R.id.action_credits:
                startActivity(new Intent(MainActivity.this,
                        AcercaDeActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void disconnectLocation(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,gpCallbacks );
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        max_scan_pref = Integer.valueOf(pref.getString("max_scan","100"));/*100 Por defecto*/
        updateTextViews();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(wifiReceiver);

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }*/
}