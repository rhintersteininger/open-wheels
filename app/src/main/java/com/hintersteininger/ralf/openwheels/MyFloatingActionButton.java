package com.hintersteininger.ralf.openwheels;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.IOException;
import java.net.URI;

/**
 * Created by ralf on 13.06.16.
 */
public class MyFloatingActionButton extends FloatingActionButton {

    private String playerName;

    public MyFloatingActionButton(Context context, String iconName, String playerName) {
        super(context);
        this.playerName = playerName;
        try {

            this.setImageDrawable(Drawable.createFromStream(
                    getResources().getAssets().open("icons/"+iconName), iconName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setPadding(0,0,0,0);
        //this.setBackground(ContextCompat.getDrawable(getContext(), icon));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        this.startAnimation(animation);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
        this.startAnimation(animation);
    }

    public String getPlayerName() {
        return playerName;
    }
}
