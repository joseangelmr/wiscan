package com.ldscsoft.wiscan.tasks;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ldscsoft.wiscan.activities.MainActivity;
import static com.ldscsoft.wiscan.database.RedesContract.Red;
import com.ldscsoft.wiscan.database.RedesDBHelper;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by David on 24/01/2015.
 */
public class ExportDatabaseTask extends AsyncTask<String,Void,Void> {

    private MainActivity mainActivity;
    private ProgressDialog pDialog;
    private String path;
    private final String SEPARATOR="#";

    public ExportDatabaseTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private ArrayList<String[]> readAndFormatData(){
        RedesDBHelper DbHelper = mainActivity.getDbHelper();
        SQLiteDatabase db= DbHelper.getReadableDatabase();
        ArrayList<String[]> datos = new ArrayList<String[]>();
        Cursor c = db.query(
                Red.TABLE_NAME,  // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                               // don't group the rows
                null,                                     // don't filter by row groups
                Red.COLUMN_NAME_NUMSCAN+","+Red.COLUMN_NAME_BSSID+" ASC"                                 // The sort order
        );
        String [] header = (
                Red.COLUMN_NAME_NUMSCAN+SEPARATOR+
                Red.COLUMN_NAME_BSSID+SEPARATOR+
                Red.COLUMN_NAME_SSID+SEPARATOR+
                Red.COLUMN_NAME_CANAL +SEPARATOR+
                Red.COLUMN_NAME_SEGURIDAD+SEPARATOR+
                Red.COLUMN_NAME_INTENSIDAD+SEPARATOR+
                Red.COLUMN_NAME_LONGITUD_I+SEPARATOR+
                Red.COLUMN_NAME_LATITUD_I+SEPARATOR+
                Red.COLUMN_NAME_LONGITUD_F+SEPARATOR+
                Red.COLUMN_NAME_LATITUD_F+SEPARATOR+
                Red.COLUMN_NAME_TIEMPO_I +SEPARATOR+
                Red.COLUMN_NAME_TIEMPO_F +SEPARATOR+
                Red.COLUMN_NAME_PROBABILIDAD+SEPARATOR+
                Red.COLUMN_NAME_DISCOVERY_RATE+SEPARATOR+
                Red.COLUMN_NAME_DETECTADA+SEPARATOR+
                Red.COLUMN_NAME_TOTAL_REDES
                ).toUpperCase().split(SEPARATOR);
        datos.add(header);

        while(c.moveToNext()){
            String NUM_SCAN = c.getString(c.getColumnIndex(Red.COLUMN_NAME_NUMSCAN));
            String BSSID = c.getString(c.getColumnIndex(Red.COLUMN_NAME_BSSID));
            String SSID = c.getString(c.getColumnIndex(Red.COLUMN_NAME_SSID));
            String CANAL = c.getString(c.getColumnIndex(Red.COLUMN_NAME_CANAL));
            String SEGU = c.getString(c.getColumnIndex(Red.COLUMN_NAME_SEGURIDAD));
            String NIVEL = c.getString(c.getColumnIndex(Red.COLUMN_NAME_INTENSIDAD));
            String LONGI_I = c.getString(c.getColumnIndex(Red.COLUMN_NAME_LONGITUD_I));
            String LATI_I = c.getString(c.getColumnIndex(Red.COLUMN_NAME_LATITUD_I));
            String LONGI_F = c.getString(c.getColumnIndex(Red.COLUMN_NAME_LONGITUD_F));
            String LATI_F = c.getString(c.getColumnIndex(Red.COLUMN_NAME_LATITUD_F));
            String TIME_I = c.getString(c.getColumnIndex(Red.COLUMN_NAME_TIEMPO_I));
            String TIME_F = c.getString(c.getColumnIndex(Red.COLUMN_NAME_TIEMPO_F));
            String PROB = c.getString(c.getColumnIndex(Red.COLUMN_NAME_PROBABILIDAD));

            String DISCOVERY = c.getString(c.getColumnIndex(Red.COLUMN_NAME_DISCOVERY_RATE));
            String DETECTADA = c.getString(c.getColumnIndex(Red.COLUMN_NAME_DETECTADA));
            String TOTAL_REDES = c.getString(c.getColumnIndex(Red.COLUMN_NAME_TOTAL_REDES));



            String []linea = (
                    NUM_SCAN+SEPARATOR+
                    BSSID+SEPARATOR+
                    SSID+SEPARATOR+
                    CANAL+SEPARATOR+
                    SEGU+SEPARATOR+
                    NIVEL+SEPARATOR+
                    LONGI_I+SEPARATOR+
                    LATI_I+SEPARATOR+
                    LONGI_F+SEPARATOR+
                    LATI_F+SEPARATOR+
                    TIME_I+SEPARATOR+
                    TIME_F+SEPARATOR+
                    PROB+SEPARATOR+
                    DISCOVERY+SEPARATOR+
                    DETECTADA+SEPARATOR+
                    TOTAL_REDES+SEPARATOR
                            ).split(SEPARATOR);
            datos.add(linea);
        }
        c.close();
        db.close();
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
        Toast.makeText(mainActivity,"Data exportada en el archivo: "+path,Toast.LENGTH_LONG).show();
    }
}