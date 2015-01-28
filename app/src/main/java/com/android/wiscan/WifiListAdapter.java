package com.android.wiscan;

import android.content.Context;
import android.graphics.Color;
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

    private ArrayList<MyScanResult> items;

    public WifiListAdapter(Context context, int resource, ArrayList<MyScanResult> objects) {
        super(context, resource, objects);
        items = objects;
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

        if(position%2==0)
            convertView.setBackgroundColor(Color.parseColor("#CCCCCC"));
        else
            convertView.setBackgroundColor(Color.WHITE);
//            convertView.setBackgroundColor(Color.parseColor("#FFA500"));

        holder.nombre.setText(this.getItem(position).SSID);
        //holder.mac.setText(this.getItem(position).BSSID);
        holder.intensidad.setText(String.valueOf(this.getItem(position).level));
        //holder.seguridad.setText(this.getItem(position).capabilities);
        //holder.frecuencia.setText(String.valueOf(this.getItem(position).frequency));


        holder.probabilidad.setText(String.format("%.3f",this.getItem(position).probability));
        holder.canal.setText(String.valueOf(this.getItem(position).channel));

        return convertView;
    }

 /*   @Override
    public int getPosition(MyScanResult item) {
    for(MyScanResult red : items){
        if(red.BSSID==item.BSSID)
            //ACTUALIZAR LOS VALORES DE ESA RED PORQUE YA EXISTE Y SALIR DEL LOOP
            break;

    }
        return 0;
    }*/

    static class WifiHolder
    {

        public TextView nombre;
        //public TextView mac;
        public TextView intensidad;
        //public TextView seguridad;
        //public TextView frecuencia;
        public TextView probabilidad;
        public TextView canal;

        public WifiHolder(View row) {
            nombre = (TextView)row.findViewById(R.id.wifiSSID);
          //  mac = (TextView)row.findViewById(R.id.wifiMac);
            intensidad = (TextView)row.findViewById(R.id.wifiPower);
            //seguridad = (TextView)row.findViewById(R.id.wifiSeguridad);
            //frecuencia = (TextView)row.findViewById(R.id.wifiFrecuencia);
            probabilidad = (TextView)row.findViewById(R.id.wifiProb);
            canal = (TextView)row.findViewById(R.id.wifiChannel);
        }
    }
}