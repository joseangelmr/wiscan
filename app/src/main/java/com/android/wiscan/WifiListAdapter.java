package com.android.wiscan;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 15/01/2015.
 */
public class WifiListAdapter extends ArrayAdapter<ScanResult>{

    public WifiListAdapter(Context context, int resource, ArrayList<ScanResult> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WifiHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.wifiitem, parent, false);
            holder = new WifiHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (WifiHolder)convertView.getTag();
        }

        holder.nombre.setText(this.getItem(position).SSID);
        holder.mac.setText(this.getItem(position).BSSID);
        holder.intensidad.setText(String.valueOf(this.getItem(position).level));
        holder.seguridad.setText(this.getItem(position).capabilities);
        holder.frecuencia.setText(String.valueOf(this.getItem(position).frequency));

        return convertView;
    }

    static class WifiHolder
    {

        public TextView nombre;
        public TextView mac;
        public TextView intensidad;
        public TextView seguridad;
        public TextView frecuencia;

        public WifiHolder(View row) {
            nombre = (TextView)row.findViewById(R.id.wifiNombre);
            mac = (TextView)row.findViewById(R.id.wifiMac);
            intensidad = (TextView)row.findViewById(R.id.wifiIntensidad);
            seguridad = (TextView)row.findViewById(R.id.wifiSeguridad);
            frecuencia = (TextView)row.findViewById(R.id.wifiFrecuencia);
        }
    }
}