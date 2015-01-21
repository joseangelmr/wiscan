package com.android.wiscan;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

/**
 * Created by David on 21/01/2015.
 */
public class PreferenciasFragment extends PreferenceFragment {

     //private EditTextPreference max_scan;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
