package com.hintersteininger.ralf.openwheels;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startGame (View v)
    {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    public void exitGame (View v)
    {
        System.exit(0);
    }
}
