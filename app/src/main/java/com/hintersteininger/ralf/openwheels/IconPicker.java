package com.hintersteininger.ralf.openwheels;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ralf on 15.06.16.
 */
public class IconPicker extends Activity {

    private GridView iconLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.icon_picker);
        iconLayout = (GridView) findViewById(R.id.imageLists);

        AssetManager manager = getResources().getAssets();
        ArrayList<String> imageNames = new ArrayList<>();

        try {
            String[] images = manager.list("icons");

            for (int i = 0; i<images.length; i++)
            {
                imageNames.add(images[i]);
            }

            ImageAdapter a = new ImageAdapter(this, imageNames);
            iconLayout.setAdapter(a);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onIconClicked(View v)
    {
        String name = v.getTag().toString();
        Intent intent = new Intent();
        intent.putExtra("iconName", name);
        IconPicker.this.setResult(RESULT_OK, intent);
        IconPicker.this.finish();
    }
}
