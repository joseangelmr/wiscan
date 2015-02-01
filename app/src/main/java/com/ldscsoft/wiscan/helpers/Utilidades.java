package com.ldscsoft.wiscan.helpers;

/**
 * Created by David on 31/01/2015.
 */
public class Utilidades {

    private Utilidades(){}

    public static int calcularCanal(int frecuencia){
        int mod = frecuencia % 2412;
        int channel = (mod/5)+1;
        return channel;
    }

    public static int[] toInt(Integer[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return null;
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
    }

    public static float[] toFloat(Float[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return null;
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
    }
       /* public void imprimirDBenLog() {
        RedesDBHelper DbHelper;
        DbHelper = new RedesDBHelper(this);
        SQLiteDatabase db= DbHelper.getReadableDatabase();


        Cursor c = db.query(
                RedesContract.Red.TABLE_NAME,  // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        while(c.moveToNext()){
            String BSSID = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_BSSID));
                    String SSID = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_SSID));
                    String NIVEL = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_INTENSIDAD));
                    String FREC = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_CANAL));
                    String SEGU = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_SEGURIDAD));
                    String LONGI_I = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LONGITUD_I));
                    String LATI_I = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LATITUD_I));
                    String LONGI_F = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LONGITUD_F));
                    String LATI_F = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_LATITUD_F));
                    String TIME = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_TIEMPO_I));
                    String NS = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_NUMSCAN));
                    String PROB = c.getString(c.getColumnIndex(RedesContract.Red.COLUMN_NAME_PROBABILIDAD));

                    Log.v("PRUEBA SCAN",BSSID+" "+
                            LONGI_I+" "+
                            LATI_I+" "+
                            SSID+" "+
                            NIVEL+" "+
                            FREC+" "+
                            SEGU+" "+
                            TIME+" "+
                            LONGI_F+" "+
                            LATI_F+" "+
                            PROB+" "+
                            NS
                    );
        }
    }*/

}
