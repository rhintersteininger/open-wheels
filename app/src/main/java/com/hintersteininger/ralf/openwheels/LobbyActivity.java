package com.hintersteininger.ralf.openwheels;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayInputStream;

/**
 * Created by ralf on 19.05.16.
 */
public class LobbyActivity extends Activity{

    private final int REQUEST_SCAN = 1;
    private final int REQUEST_CUSTOMIZE = 2;

    private Car currentCar;

    private ImageView currentCarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.lobby_activity);
        currentCarImage = (ImageView) findViewById(R.id.imageView);
    }

    public void openCustomization (View v)
    {
        Intent intent = new Intent(this, CustomizationActivity.class);
        startActivityForResult(intent, REQUEST_CUSTOMIZE);
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
            if (result != null) {
                if (result.getContents() == null) {
                    //Canceld scan
                } else {
                    //Scanned
                    Log.d("LobbyActivity", result.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }


        if (requestCode == REQUEST_CUSTOMIZE)
        {
            System.out.println("Hallo");
                String carName = data.getStringExtra("carName");
                int acc = data.getIntExtra("accel", 0);
                int weight = data.getIntExtra("weight", 0);
                int speed = data.getIntExtra("speed", 0);
                byte[] bytes = data.getByteArrayExtra("image");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                currentCarImage.setImageDrawable(drawable);
                currentCar = new Car(carName, bitmap, acc, weight, speed);
        }

    }
}
