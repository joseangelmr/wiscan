package com.ldscsoft.wiscan.helpers;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.ldscsoft.wiscan.activities.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by David on 22/01/2015.
 */
public class GooglePlayCallbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private MainActivity mActivity;

    public GooglePlayCallbacks(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("PRUEBA GOOGLECALLBACKS","SE LLAMO AL METODO ON CONNECTED ANTES");
        mActivity.scanearRedes();
        Log.v("PRUEBA GOOGLECALLBACKS","SE LLAMO AL METODO ON CONNECTED DESPUES");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("PRUEBA GOOGLECALLBACKS","SE LLAMO AL METODO CONECTION SUSPENDED");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(mActivity,"FALLO LA CONEXION A GOOGLE PLAY SERVICES",Toast.LENGTH_LONG).show();
        Log.v("PRUEBA GOOGLECALLBACKS","SE LLAMO AL METODO CONECTION FAILED");
    }

    /*Metodos de LocationListener*/

    @Override
    public void onLocationChanged(Location location) {
        Log.v("PRUEBA GOOGLECALLBACKS","SE LLAMO AL METODO ON LOCATION CHANGED");
        Log.v("PRUEBA GOOGLECALLBACKS","("+location.getLatitude()+","+location.getLongitude()+")");
        //mActivity.disconnectLocation();
        if(mActivity.semaforoLocation) {
            mActivity.semaforoLocation=false;
            mActivity.disconnectLocation();
            mActivity.scanearRedes();
        }
    }
}