package com.hintersteininger.ralf.openwheels;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ralf on 29.10.2015.
 */
public class ImageAdapter extends BaseAdapter
{
    ArrayList<String> imageNames = null;
    LayoutInflater inflater;
    Context context;

    public ImageAdapter(Context context, ArrayList<String> list)
    {
        super();
        this.imageNames = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return imageNames.size();
    }

    @Override
    public Object getItem(int position)
    {
        return imageNames.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent)
    {
        View row = inflater.inflate(R.layout.imageview, parent, false);
        ImageView image = (ImageView) row.findViewById(R.id.imageView);
        try {
            image.setImageDrawable(Drawable.createFromStream(
                    context.getAssets().open("icons/" + imageNames.get(position)), imageNames.get(position)));
            image.setTag(imageNames.get(position));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return row;
    }
}


