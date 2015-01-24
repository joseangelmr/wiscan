package com.android.wiscan.tasks;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.android.wiscan.MainActivity;
import com.android.wiscan.database.RedesContract;
import com.android.wiscan.database.RedesDBHelper;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 24/01/2015.
 */
public class ExportDatabaseTask extends AsyncTask<String,Void,Void> {

    private MainActivity mainActivity;
    private ProgressDialog pDialog;
    private String path;

    public ExportDatabaseTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private ArrayList<String[]> readAndFormatData(){
        RedesDBHelper DbHelper;
        DbHelper = new RedesDBHelper(mainActivity);
        SQLiteDatabase db= DbHelper.getReadableDatabase();
        ArrayList<String[]> datos = new ArrayList<String[]>();
        Cursor c = db.query(
                RedesContract.Red.TABLE_NAME,  // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                               // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        String [] header = (RedesContract.Red.COLUMN_NAME_BSSID+"#"+
                RedesContract.Red.COLUMN_NAME_LONGITUD_I+"#"+
                RedesContract.Red.COLUMN_NAME_LATITUD_I+"#"+
                RedesContract.Red.COLUMN_NAME_SSID+"#"+
                RedesContract.Red.COLUMN_NAME_INTENSIDAD+"#"+
                RedesContract.Red.COLUMN_NAME_FRECUENCIA+"#"+
                RedesContract.Red.COLUMN_NAME_SEGURIDAD+"#"+
                RedesContract.Red.COLUMN_NAME_TIEMPO+"#"+
                RedesContract.Red.COLUMN_NAME_LONGITUD_F+"#"+
                RedesContract.Red.COLUMN_NAME_LATITUD_F+"#"+
                RedesContract.Red.COLUMN_NAME_PROBABILIDAD+"#"+
                RedesContract.Red.COLUMN_NAME_NUMSCAN).toUpperCase().split("#");
        datos.add(header);
        while(c.moveToNext()){
            String BSSID = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_BSSID));
            String SSID = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_SSID));
            String NIVEL = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_INTENSIDAD));
            String FREC = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_FRECUENCIA));
            String SEGU = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_SEGURIDAD));
            String LONGI_I = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LONGITUD_I));
            String LATI_I = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LATITUD_I));
            String LONGI_F = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LONGITUD_F));
            String LATI_F = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LATITUD_F));
            String TIME = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_TIEMPO));
            String NS = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_NUMSCAN));
            String PROB = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_PROBABILIDAD));



            String []linea = (BSSID+"#"+
                            LONGI_I+"#"+
                            LATI_I+"#"+
                            SSID+"#"+
                            NIVEL+"#"+
                            FREC+"#"+
                            SEGU+"#"+
                            TIME+"#"+
                            LONGI_F+"#"+
                            LATI_F+"#"+
                            PROB+"#"+
                            NS).split("#");
            datos.add(linea);
        }
        return datos;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(mainActivity);
        pDialog.setMessage("Exportando Data");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        String nombre = params[0];
        CSVWriter writer = null;
        File dir = new File(Environment.getExternalStorageDirectory()+File.separator+"WiScan");
        if (!dir.exists()) {
            dir.mkdir();
        }
    /*Metodo para no sobbreescribir archivo sino guardarlo con el nombre + _(n)*/
        String nombre_aux = nombre+".csv";
        File file = new File(dir,nombre_aux);
        int copy_aux=0;
        while(file.exists()) {
            copy_aux++;
            nombre_aux = nombre + "_(" + Integer.toString(copy_aux) + ").csv";
            file = new File(dir, nombre_aux);
        }
    /*Fin metodo indicado arriba */

        path = dir+File.separator+nombre_aux;
        Log.v("PRUEBA EXPORT","VALOR DE PATH: "+path);

        try {
            writer = new CSVWriter(new FileWriter(path), '|');
            writer.writeAll(readAndFormatData(),false);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (pDialog.isShowing())
            pDialog.dismiss();
        mainActivity.deleteDB();
        Toast.makeText(mainActivity,"Data exportada en el archivo: "+path,Toast.LENGTH_LONG).show();
    }
}