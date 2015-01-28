package com.android.wiscan.helpers;

import android.net.wifi.ScanResult;

/**
 * Created by David on 22/01/2015.
 */
public class MyScanResult {

    public String	BSSID;
    public String	SSID;
    public String	capabilities;
    public int	frequency;
    public int	level;
    public float probability;
    public int channel;
    public int timesDetected;

    public MyScanResult(ScanResult result, float prob) {
        this.BSSID = result.BSSID;
        this.SSID = result.SSID;
        this.capabilities = result.capabilities;
        this.frequency = result.frequency;
        this.level = result.level;
        this.probability = prob;
        this.timesDetected = 0;

        int mod = result.frequency% 2412;

        this.channel = (mod/5)+1;
    }
}
