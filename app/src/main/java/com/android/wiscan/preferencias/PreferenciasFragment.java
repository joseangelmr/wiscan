package com.android.wiscan.preferencias;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

import com.android.wiscan.R;

/**
 * Created by David on 21/01/2015.
 */


//Clase usada para versiones de donde MIN_SDK >= 11
//No usada en versiones donde MIN_SDK <11 (COmo la actual)
public class PreferenciasFragment extends PreferenceFragment {

     //private EditTextPreference max_scan;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
