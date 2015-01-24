package com.android.wiscan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.wiscan.helpers.MyScanResult;

import java.util.ArrayList;

/**
 * Created by David on 15/01/2015.
 */
public class WifiListAdapter extends ArrayAdapter<MyScanResult>{

    public WifiListAdapter(Context context, int resource, ArrayList<MyScanResult> objects) {
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
        holder.probabilidad.setText(String.valueOf(this.getItem(position).probability));

        return convertView;
    }

    static class WifiHolder
    {

        public TextView nombre;
        public TextView mac;
        public TextView intensidad;
        public TextView seguridad;
        public TextView frecuencia;
        public TextView probabilidad;

        public WifiHolder(View row) {
            nombre = (TextView)row.findViewById(R.id.wifiNombre);
            mac = (TextView)row.findViewById(R.id.wifiMac);
            intensidad = (TextView)row.findViewById(R.id.wifiIntensidad);
            seguridad = (TextView)row.findViewById(R.id.wifiSeguridad);
            frecuencia = (TextView)row.findViewById(R.id.wifiFrecuencia);
            probabilidad = (TextView)row.findViewById(R.id.wifiProbabilidad);
        }
    }
}