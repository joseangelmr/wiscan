package com.android.wiscan.helpers;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.wiscan.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

/**
 * Created by David on 22/01/2015.
 */
public class GooglePlayCallbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private MainActivity mActivity;

    public GooglePlayCallbacks(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mActivity.scanearRedes();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(mActivity,"FALLO LA CONEXION A GOOGLE PLAY",Toast.LENGTH_LONG).show();
    }
}