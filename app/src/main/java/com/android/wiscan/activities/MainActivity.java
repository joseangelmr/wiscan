package com.android.wiscan.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wiscan.R;
import com.android.wiscan.WifiListAdapter;
import com.android.wiscan.WifiReceiver;
import com.android.wiscan.database.RedesDBHelper;
import com.android.wiscan.helpers.DialogHelper;
import com.android.wiscan.helpers.GooglePlayCallbacks;
import com.android.wiscan.helpers.MyScanResult;
import com.android.wiscan.helpers.Utilidades;
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
    private TextView discovery_rate;
    private TextView scan_time;
    private TextView network_count;

    private ImageButton boton_graficar;

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

    private ArrayList<Integer> networkCountList;
    private ArrayList<Float> discoveryRatetList;

    public void setTextViewValues(float discoveryrate,float scantime,int networkcount) {
        this.discoveryRate = discoveryrate;
        this.scanTime = scantime;
        this.networkCount = networkcount;

        Log.v("PRUEBA DE LISTAS","VALOR DE NUM SCAN: "+num_scan);
        discoveryRatetList.add(num_scan-1,discoveryRate);
        networkCountList.add(num_scan-1,networkCount);

    }

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
    }

    public void updateTextViews() {
        scaneos.setText("Scan: "+String.valueOf(num_scan)+" / "+String.valueOf(max_scan_pref));
        discovery_rate.setText("Disc. rate: " + String.valueOf(discoveryRate));
        network_count.setText("Redes detec.: " + String.valueOf(networkCount));
        scan_time.setText("Tiempo de scan: " + String.valueOf(scanTime));
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
                    //Log.v("PRUEBA SCAN","SI SE INICIO EL SCAN: "+num_scan);
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

        networkCountList = new ArrayList<Integer>();
        discoveryRatetList = new ArrayList<Float>();

        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!mainWifiObj.isWifiEnabled()){
            Toast.makeText(this,"Encendiendo Wifi",Toast.LENGTH_SHORT).show();
            mainWifiObj.setWifiEnabled(true);
            Log.v("PRUEBA BUCLE","ANTES DEL WHILE");
            while(mainWifiObj.getWifiState()==WifiManager.WIFI_STATE_ENABLING);
            Log.v("PRUEBA BUCLE","DESPUES DEL WHILE");
        }
        buildGoogleApiClient();
        wifiReceiver = new WifiReceiver(this,mainWifiObj,wifiList);
    /*Inicializacion de los TextView*/
        scaneos = (TextView)findViewById(R.id.scan_actual);
        discovery_rate = (TextView)findViewById(R.id.discovery_rate);
        scan_time= (TextView)findViewById(R.id.scan_time);
        network_count = (TextView)findViewById(R.id.network_count);

        boton_graficar = (ImageButton) findViewById(R.id.graficar);
        boton_graficar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainValuesPlotActivity.class);

                intent.putExtra("NETWORK_COUNT", Utilidades.toInt((networkCountList.toArray(new Integer[networkCountList.size()]))));
                intent.putExtra("DISCOVERY_RATE", Utilidades.toFloat((discoveryRatetList.toArray(new Float[discoveryRatetList.size()]))));
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
                        CreditosActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
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
}