package com.hintersteininger.ralf.openwheels;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by ralf on 19.05.16.
 */
public class LobbyActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.lobby_activity);
    }

    public void openCustomization (View v)
    {
        Intent intent = new Intent(this, CustomizationActivity.class);
        startActivity(intent);
    }

    public void openPreferences (View v)
    {
        Intent intent = new Intent(this, Preferences.class);
        startActivity(intent);
    }
}
