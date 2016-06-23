package com.hintersteininger.ralf.openwheels;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by ralf on 31.05.16.
 */
public class Car implements Serializable {
    private Bitmap image;
    private String imageName;
    private String carName;
    private int acceleration;
    private int weight;
    private int max_speed;

    public Car(String carName, Bitmap image, String imageName, int acceleration, int weight, int max_speed) {
        this.carName = carName;
        this.image = image;
        this.imageName = imageName;
        this.acceleration = acceleration;
        this.weight = weight;
        this.max_speed = max_speed;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getMax_speed() {
        return max_speed;
    }

    public void setMax_speed(int max_speed) {
        this.max_speed = max_speed;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public byte[] getByteImage(int compressRatio)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, compressRatio, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    @Override
    public String toString() {
        return Arrays.toString(getByteImage(80))+","+carName+","+acceleration+","+weight+","+max_speed;
    }
}
