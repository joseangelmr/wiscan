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

    public MyScanResult(ScanResult result, float prob) {
        this.BSSID = result.BSSID;
        this.SSID = result.SSID;
        this.capabilities = result.capabilities;
        this.frequency = result.frequency;
        this.level = result.level;
        this.probability = prob;
    }
}
