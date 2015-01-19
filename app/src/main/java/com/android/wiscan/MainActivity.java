package com.android.wiscan;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private WifiManager mainWifiObj;
    private WifiReceiver wifiReceiver;
    private ListView wifiList;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private int num_scan=0;
    private Timer timer;

    private  void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        wifiList = (ListView)findViewById(R.id.wifiListView);
        wifiList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!mainWifiObj.isWifiEnabled()){
            Toast.makeText(this,"Encendiendo Wifi",Toast.LENGTH_SHORT).show();
            mainWifiObj.setWifiEnabled(true);
        }

        buildGoogleApiClient();
        wifiReceiver = new WifiReceiver(this,mainWifiObj,wifiList);
        //num_scan;
        Asyncwifi();
    }

    //Ojo prueba de scan periodico
    public void Asyncwifi() {
        final Handler  whandler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {

            @Override
            public void run() {
                whandler.post(new Runnable() {
                    public void run() {
                        try {
                            //mainWifiObj.startScan();
                            mGoogleApiClient.reconnect();
                        }catch (Exception e) {}
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); // Repeate in every 5 sec

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_stop:
                item.setTitle("INICIAR");
                timer.cancel();
                return true;
            case R.id.action_nada:
                Toast.makeText(this,"HACIENDO NADA...",Toast.LENGTH_SHORT).show();
                RedesDBHelper mDbHelper = new RedesDBHelper(this);
                SQLiteDatabase db = mDbHelper.getReadableDatabase();


// Define a projection that specifies which columns from the database
// you will actually use after this query.
                String[] projection = null;

// How you want the results sorted in the resulting Cursor
                String sortOrder =null;

                Cursor c = db.query(
                        RedesContract.Red.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        null,                                // The columns for the WHERE clause
                        null,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                );
                while (c.moveToNext()) {
                    String BSSID = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_BSSID));
                    String SSID = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_SSID));
                    String NIVEL = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_INTENSIDAD));
                    String FREC = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_FRECUENCIA));
                    String SEGU = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_SEGURIDAD));
                    String LONGI = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LONGITUD));
                    String LATI = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LATITUD));
                    String TIME = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_TIEMPO));
                    String NS = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_NUMSCAN));

                    Log.v("PRUEBA SCAN",BSSID+" "+
                            SSID+" "+
                            NIVEL+" "+
                            FREC+" "+
                            SEGU+" "+
                            TIME+" "+
                            LONGI+" "+
                            LATI+" "+
                            NS
                    );

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
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
        registerReceiver(wifiReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.v("PRUEBA-LOCALIZACION","LLEGO A LA PARTE DE MLASTLOCATION");
        if (mLastLocation != null) {
            Log.v("PRUEBA-LOCALIZACION",String.valueOf("("+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+")"));
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            Toast.makeText(this,String.valueOf("("+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+")"),Toast.LENGTH_LONG).show();
            Calendar c = Calendar.getInstance();
            long seconds = c.getTime().getTime();
            wifiReceiver.setVars(seconds,num_scan,mLastLocation.getLongitude(),mLastLocation.getLatitude());
            num_scan++;
            mainWifiObj.startScan();
            Log.v("PRUEBA-SCAN","NUMERO DE SCAN: "+num_scan);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"FALLO LA CONEXION A GOOGLE PLAY",Toast.LENGTH_LONG).show();
        Log.v("PRUEBA-LOCALIZACION","FALLO LA CONEXION A GOOGLE PLAY");
    }
}