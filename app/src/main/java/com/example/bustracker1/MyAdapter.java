package com.example.bustracker1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<BusModel> {

    private Context mcontext;
    private int mresource;

    public MyAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BusModel> objects) {
        super(context, resource, objects);
        mcontext=context;
        mresource=resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            String name=getItem(position).getName();
        LayoutInflater layoutInflater=LayoutInflater.from(mcontext);
        convertView=layoutInflater.inflate(mresource,parent,false);

        TextView txt=convertView.findViewById(R.id.bus_name);

        txt.setText(name);
        return convertView;
    }
}
