package com.hintersteininger.ralf.openwheels;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.lang.Override;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by ralf on 09.06.16.
 */
public class Controller extends Activity {

    final static int ACCELERATION_NEUTRAL = 0;
    final static int ACCELERATION_POSITIVE = 1;
    final static int ACCELERATION_NEGATIVE = -1;

    final static int STEER_MULTIPLICATOR = 10;

    final float[] mValuesMagnet      = new float[3];
    final float[] mValuesAccel       = new float[3];
    final float[] mValuesOrientation = new float[3];
    final float[] mRotationMatrix    = new float[9];

    SensorManager sensorManager;
    SensorEventListener mEventListener;

    Button breakPedal;
    Button gasPedal;

    private int accelerationMode = ACCELERATION_NEUTRAL;
    private int specialButton = 0;

    private boolean breakPedalDown;
    private boolean gasPedalDown;

    private NetworkWriter writer;

    private int tickrate = 100;

    private int invertSteer = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getBoolean("invertAccelerometer", false))
        {
            setContentView(R.layout.controller_activity_inverted);
        }
        else
        {
            setContentView(R.layout.controller_activity);
        }

        if (sp.getBoolean("invertSteer", false))
        {
            invertSteer = -1;
        }


        final Intent intent = getIntent();
        tickrate = Integer.valueOf(intent.getStringExtra("tickrate"));


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writer = new NetworkWriter(Controller.this);
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Controller.this);

                    String a = "playerdata:"
                            + sp.getString("username", "Unknown Player "+Math.random()*1000+10) + ";"
                            + intent.getIntExtra("accel", 1) + ";"
                            + intent.getIntExtra("maxSpeed", 10) + ";"
                            + intent.getIntExtra("weight", 5) + ";"
                            + intent.getStringExtra("imageName");


                    writer.write(a);
                    writer.write(a);
                    writer.write(a);


                    while (true) {
                        writer.write("frameData:" + accelerationMode + ";" + getTilt() * STEER_MULTIPLICATOR * invertSteer + ";" + specialButton);
                        Thread.sleep(tickrate);
                    }

                }catch (InterruptedException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        t.start();

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        mEventListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        System.arraycopy(event.values, 0, mValuesAccel, 0, 3);
                        break;

                    case Sensor.TYPE_MAGNETIC_FIELD:
                        System.arraycopy(event.values, 0, mValuesMagnet, 0, 3);
                        break;
                }
            };
        };


        sensorManager.registerListener(mEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(mEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);

        breakPedal = (Button) findViewById(R.id.button_controller_break);
        gasPedal = (Button) findViewById(R.id.button_controller_gas);

        breakPedal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    breakPedalDown = true;
                    accelerationMode = ACCELERATION_NEGATIVE;
                    breakPedal.setBackground(ContextCompat.getDrawable(Controller.this, R.drawable.break_down));
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (gasPedalDown)
                    {
                        accelerationMode = ACCELERATION_POSITIVE;
                    }
                    else {
                        accelerationMode = ACCELERATION_NEUTRAL;
                    }
                    breakPedalDown = false;
                    breakPedal.setBackground(ContextCompat.getDrawable(Controller.this, R.drawable.break_up));
                }

                return true;
            }
        });

        gasPedal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    gasPedalDown = true;
                    accelerationMode = ACCELERATION_POSITIVE;
                    gasPedal.setBackground(ContextCompat.getDrawable(Controller.this, R.drawable.gas_down));
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (breakPedalDown)
                    {
                        accelerationMode = ACCELERATION_NEGATIVE;
                    }
                    else {
                        accelerationMode = ACCELERATION_NEUTRAL;
                    }
                    gasPedalDown = false;
                    gasPedal.setBackground(ContextCompat.getDrawable(Controller.this, R.drawable.gas_up));
                }

                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(mEventListener);
    }

    public float getTilt()
    {
        SensorManager.getRotationMatrix(mRotationMatrix, null, mValuesAccel, mValuesMagnet);
        SensorManager.getOrientation(mRotationMatrix, mValuesOrientation);

        return mValuesOrientation[1];
    }
}
