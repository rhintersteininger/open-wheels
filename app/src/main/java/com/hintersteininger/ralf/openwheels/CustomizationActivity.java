package com.hintersteininger.ralf.openwheels;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ralf on 19.05.16.
 */
public class CustomizationActivity extends Activity {

    private final String LOG_TAG = this.getClass().getName();
    private Car[] allCars;

    private ImageSwitcher switcher;
    private ProgressBar accelerationBar;
    private ProgressBar speedBar;
    private ProgressBar weightBar;
    private TextView carNameView;

    private int currentCarNum = 0;

    private Animation slide_out_right;
    private Animation slide_out_left;
    private Animation slide_in_right;
    private Animation slide_in_left;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.customize_activity);
        switcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        accelerationBar = (ProgressBar) findViewById(R.id.progressBar_customize_acceleration);
        speedBar = (ProgressBar) findViewById(R.id.progressBar_customize_speed);
        weightBar = (ProgressBar) findViewById(R.id.progressBar_customize_weight);
        carNameView = (TextView) findViewById(R.id.textViewCarName);

        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {

                ImageView myView = new ImageView(getApplicationContext());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                        (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                myView.setLayoutParams(params);
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                return myView;
            }
        });

        slide_in_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        slide_in_right = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slide_out_left = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);



        loadCars();
        updateData();

    }

    private void loadCars()
    {
        AssetManager manager = this.getResources().getAssets();
        try {
            String[] cars = manager.list("cars");
            for (String s : cars)
            {
                if (s.endsWith(".json"))
                {
                    InputStream inputStream = manager.open("cars/"+s);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String all = "";
                    String line = "";
                    while ((line = reader.readLine())!= null)
                    {
                        all+=line;
                    }
                    try {
                        JSONObject carObject = new JSONObject(all);

                        JSONArray carList = carObject.getJSONArray("list");
                        allCars = new Car[carList.length()];
                        for (int i = 0; i < allCars.length; i++)
                        {
                            allCars[i] = makeCar(carList.getJSONObject(i));
                        }
                    }
                    catch (JSONException e)
                    {
                        Log.w(LOG_TAG, "JSON exception: Can not load cars");
                        Toast.makeText(this, "Error: can not load cars", Toast.LENGTH_LONG);
                        e.printStackTrace();
                    }


                }
            }
        }
        catch (IOException e)
        {
            Log.w(this.getClass().getName(), "no cars found");
        }
    }

    public Car makeCar (JSONObject object)
    {
        try
        {
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



            return new Car(carName, bitmap,imagePath, acceleration, weight, speed);

        }
        catch (JSONException e)
        {
            Log.w(LOG_TAG, "JSON exception: Can not load car "+object.toString());
            e.printStackTrace();
        }
        catch (IOException ex)
        {
            Log.w(LOG_TAG, "IO exception: Can not load car "+object.toString());
            ex.printStackTrace();
        }


        return null;
    }

    public void clickNext(View v)
    {
        currentCarNum++;
        switcher.setInAnimation(slide_in_right);
        switcher.setOutAnimation(slide_out_left);
        updateData();
    }

    public void clickPrevious(View v)
    {
        currentCarNum--;
        switcher.setInAnimation(slide_in_left);
        switcher.setOutAnimation(slide_out_right);
        updateData();
    }

    public void updateData()
    {
        int i = currentCarNum % allCars.length;

        if (i < 0)
        {
            i += allCars.length;
        }

        if (allCars != null && allCars.length > 0)
        {
            Car c = allCars[i];
            Drawable drawable = new BitmapDrawable(getResources(), c.getImage());
            switcher.setImageDrawable(drawable);
            accelerationBar.setProgress(c.getAcceleration());
            speedBar.setProgress(c.getMax_speed());
            weightBar.setProgress(c.getWeight());
            carNameView.setText(c.getCarName());
        }
        else
        {
            switcher.setImageResource(R.drawable.no_image_new);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Car c = allCars[currentCarNum % allCars.length];

        intent.putExtra("carName", c.getCarName());
        intent.putExtra("accel", c.getAcceleration());
        intent.putExtra("weight", c.getWeight());
        intent.putExtra("speed", c.getMax_speed());
        intent.putExtra("image", c.getImageName());

        System.out.println((allCars[currentCarNum % allCars.length]).toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
