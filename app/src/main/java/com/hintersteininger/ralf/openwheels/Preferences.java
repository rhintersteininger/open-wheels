package com.hintersteininger.ralf.openwheels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.net.URLConnection;

/**
 * Created by ralf on 19.05.16.
 */
public class Preferences extends PreferenceActivity {

    private final int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceFragment fragment = new MyPreferenceFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        getFragmentManager().executePendingTransactions();
        fragment.getPreferenceScreen().findPreference("a").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Preferences.this, IconPicker.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            }
        });

    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        public static Preference iconPick;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(R.xml.prefs);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                SharedPreferences sp = getSharedPreferences(this.getClass().toString(), Context.MODE_PRIVATE);
                sp.edit().putString("iconName", data.getStringExtra("iconName")).commit();

            }
        }
    }
}
