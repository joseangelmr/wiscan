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


    private void updateResult(int num_scan_actual,boolean detected,int power){
        if(detected) {
            this.timesDetected++;
            this.level = power;
        }
        this.probability = (float)this.timesDetected/num_scan_actual;
    }


    public void updateDetectedResult(int num_scan_actual,int power){
        updateResult(num_scan_actual,true,power);
    }

    public void updateNotDetectedResult(int num_scan_actual){
        updateResult(num_scan_actual,false,0);
    }

    public MyScanResult(ScanResult result, float prob) {
        this.BSSID = result.BSSID;
        this.SSID = result.SSID;
        this.capabilities = result.capabilities;
        this.frequency = result.frequency;
        this.level = result.level;
        this.probability = prob;
        this.timesDetected = 1;

        int mod = result.frequency% 2412;

        this.channel = (mod/5)+1;
    }
}
