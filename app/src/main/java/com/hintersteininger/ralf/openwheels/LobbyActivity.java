package com.hintersteininger.ralf.openwheels;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

    public void joinGame (View v)
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);

        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                //Canceld scan
            }
            else
            {
                //Scanned
                Log.d("LobbyActivity", result.getContents());
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
