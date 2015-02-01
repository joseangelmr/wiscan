package com.ldscsoft.wiscan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ldscsoft.wiscan.helpers.MyScanResult;

import java.util.ArrayList;

/**
 * Created by David on 15/01/2015.
 */
public class WifiListAdapter extends ArrayAdapter<MyScanResult>{

    private int colorPrimario;
    private int colorSecundario;

    public WifiListAdapter(Context context, int resource, ArrayList<MyScanResult> objects) {
        super(context, resource, objects);
        colorPrimario= getContext().getResources().getColor(R.color.action_bar_blue);
        colorSecundario = Color.WHITE;
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

        if(position%2==0) {
            convertView.setBackgroundColor(colorPrimario);
            holder.setTextColor(colorSecundario);
        }
        else{
            convertView.setBackgroundColor(colorSecundario);
            holder.setTextColor(colorPrimario);
        }

        holder.nombre.setText(this.getItem(position).SSID);
        holder.intensidad.setText(String.valueOf(this.getItem(position).level));
        holder.probabilidad.setText(String.format("%.3f",this.getItem(position).probability));
        holder.canal.setText(String.valueOf(this.getItem(position).channel));

        return convertView;
    }

     private static class WifiHolder
    {

        public TextView nombre;
        public TextView intensidad;
        public TextView probabilidad;
        public TextView canal;

        public WifiHolder(View row) {
            nombre = (TextView)row.findViewById(R.id.wifiSSID);
            intensidad = (TextView)row.findViewById(R.id.wifiPower);
            probabilidad = (TextView)row.findViewById(R.id.wifiProb);
            canal = (TextView)row.findViewById(R.id.wifiChannel);
        }
        public void setTextColor(int color){
            nombre.setTextColor(color);
            intensidad.setTextColor(color);
            probabilidad.setTextColor(color);
            canal.setTextColor(color);
        }
    }
}