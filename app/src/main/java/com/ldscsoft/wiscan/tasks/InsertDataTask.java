
package com.ldscsoft.wiscan.tasks;

import android.os.AsyncTask;

import com.ldscsoft.wiscan.WifiReceiver;


/**
* Created by David on 18/01/2015.
*/

public class InsertDataTask extends AsyncTask<WifiReceiver, Void, WifiReceiver> {

    @Override
    protected void onPostExecute(WifiReceiver results) {
        results.postInUI();
    }

    @Override
    protected WifiReceiver doInBackground(WifiReceiver... params) {
        WifiReceiver wifiReceiver = params[0];
        wifiReceiver.saveInDB();
        return wifiReceiver;
    }
}
