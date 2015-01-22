package com.android.wiscan.preferencias;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.wiscan.R;

/**
 * Created by David on 21/01/2015.
 */
public class PreferenciasActivity extends PreferenceActivity {

    private EditTextPreference max_scan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferencias);

// Metodo comentado por no ser usado, ya que la preferencia se ha configurado
//para solo recibir enteros positivos.

//        max_scan = (EditTextPreference)getPreferenceScreen().findPreference("max_scan");
//        max_scan.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                Boolean rtnval = true;
//                Integer aux= Integer.valueOf(newValue.toString());
//
//                if (aux==null || aux<=0) {
////                    final AlertDialog.Builder builder = new AlertDialog.Builder();
////                    builder.setTitle("Valor Invalido");
////                    builder.setMessage("Ingrese un valor entero mayor que cero");
////                    builder.setPositiveButton(android.R.string.ok, null);
////                    builder.show();
//                    //Toast.makeText()
//                    Log.v("PRUEBA PREF",newValue.toString());
//                    Log.v("PRUEBA PREF","VALOR DE AUX: "+aux);
//                    rtnval = false;
//                }
//                return rtnval;
//            }
//        });//        getFragmentManager().beginTransaction()
////                .replace(android.R.id.content, new PreferenciasFragment())
////                .commit();
    }
}