package com.hintersteininger.ralf.openwheels;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by ralf on 19.05.16.
 */
public class LobbyActivity extends Activity{

    private final int REQUEST_SCAN = 1;
    private final int REQUEST_CUSTOMIZE = 2;

    private Car currentCar = null;

    private ImageView currentCarImage;
    private LinearLayout lobbyLayout;

    private Button findGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.lobby_activity);

        currentCarImage = (ImageView) findViewById(R.id.imageView);
        findGame = (Button) findViewById(R.id.button_lobby_join);
        lobbyLayout = (LinearLayout) findViewById(R.id.layoutLobby);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        int car = sp.getInt("currentCar", 0);
        try {
            InputStream is = getResources().getAssets().open("cars/carList.json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String all = "";
            String line = "";
            while ((line = reader.readLine())!= null)
            {
                all+=line;
            }

            JSONArray array = new JSONObject(all).getJSONArray("list");
            JSONObject object = array.getJSONObject(car);

            String carName = object.getString("CarName");
            String imagePath = object.getString("Image");
            int acceleration = object.getInt("Acceleration");
            int weight = object.getInt("Weight");
            int speed = object.getInt("Speed");

            Bitmap bitmap = null;

            if (imagePath.endsWith(".png"))
            {
                bitmap = new BitmapDrawable(getResources(), getResources().getAssets().open("cars/"+imagePath)).getBitmap();
            }
            else
            {
                bitmap = new BitmapDrawable(getResources(), getResources().getAssets().open("cars/"+imagePath+".png")).getBitmap();
            }
            currentCar = new Car(carName, bitmap,imagePath, acceleration, weight, speed);

            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            currentCarImage.setImageDrawable(drawable);


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateButtons();
    }

    private void updateButtons()
    {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        String username = sp.getString("username", "");

        if (Connection.connectionSocket != null)
        {
            findGame.setText("EXIT GAME");
            findGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (Connection.connectionSocket != null) {
                            Connection.connectionSocket.close();
                            Connection.connectionSocket = null;
                        }
                        findGame.setText("Join Game");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LobbyActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (currentCar == null || username.equals(""))
        {
            findGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentCar == null)
                    {
                        Toast.makeText(LobbyActivity.this, "Pick a car first!", Toast.LENGTH_LONG).show();
                    }
                    else if (sp.getString("username", "").equals(""))
                    {
                        Toast.makeText(LobbyActivity.this, "Choose a username first!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else
        {
            findGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinGame();
                }
            });
        }

        //Log.d("asjfljasdflkahglkja", new String(currentCar.getByteImage(100), StandardCharsets.UTF_8));
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

    public void joinGame ()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    String[] ipConf;
    String[] fullData;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    //Canceld scan
                } else {
                    //Scanned
                    Log.d("LobbyActivity", result.getContents());

                    ipConf = result.getContents().split(":");

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Connection.connectionSocket = new Socket(ipConf[0], Integer.valueOf(ipConf[1]));
                                BufferedReader reader = new BufferedReader(new InputStreamReader(Connection.connectionSocket.getInputStream()));
                                String line = "";

                                while ((line = reader.readLine()) != null)
                                {
                                    fullData = line.split(":");

                                    switch (fullData[0])
                                    {
                                        case "startGame":
                                            Intent intent = new Intent(LobbyActivity.this, Controller.class);
                                            intent.putExtra("tickrate", fullData[1]);
                                            intent.putExtra("maxSpeed", currentCar.getMax_speed());
                                            intent.putExtra("accel", currentCar.getAcceleration());
                                            intent.putExtra("imageName", currentCar.getImageName());
                                            intent.putExtra("weight", currentCar.getWeight());
                                            startActivity(intent);
                                            break;
                                        case "playerJoin":
                                            playerJoin(fullData[1], fullData[2]);
                                            break;
                                        case "players":

                                            break;
                                        case "playerLeft":

                                            break;
                                        case "serverClosed":
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    lobbyLayout.removeAllViews();
                                                }
                                            });

                                            break;
                                    }
                                }
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });

                    t.start();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }


        if (requestCode == REQUEST_CUSTOMIZE)
        {
                String carName = data.getStringExtra("carName");
                int acc = data.getIntExtra("accel", 0);
                int weight = data.getIntExtra("weight", 0);
                int speed = data.getIntExtra("speed", 0);
                String imagePath = data.getStringExtra("image");
            try {
                Bitmap bitmap = new BitmapDrawable(getResources(), getResources().getAssets().open("cars/" + imagePath)).getBitmap();
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                currentCarImage.setImageDrawable(drawable);
                currentCar = new Car(carName, bitmap,imagePath, acc, weight, speed);
                findGame.setEnabled(true);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }

    }

    private void playerJoin(final String name, final String iconName)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyFloatingActionButton button = new MyFloatingActionButton(LobbyActivity.this, iconName, name);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(10,5,10,5);

                button.setLayoutParams(params);
                lobbyLayout.addView(button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyFloatingActionButton myButton = (MyFloatingActionButton) v;
                        Toast.makeText(LobbyActivity.this, myButton.getPlayerName(), Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(LobbyActivity.this,fullData[1]+" joined the Game", Toast.LENGTH_LONG).show();
            }
        });
    }
}
