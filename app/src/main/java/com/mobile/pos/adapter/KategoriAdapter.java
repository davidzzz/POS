package com.mobile.pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.pos.R;
import com.mobile.pos.model.Kategori;

import java.io.File;
import java.util.ArrayList;

public class KategoriAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Kategori> list;

    public KategoriAdapter(Context context, ArrayList<Kategori> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int location) { return list.get(location); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_kategori, null);
        Kategori k = list.get(position);
        TextView teks = (TextView) convertView.findViewById(R.id.kategori);
        teks.setText(k.getNama());
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        String path = "http://192.168.56.1/FBClub/Help/Pictures/" + k.getKode() + ".jpg";
        File file = new File(path);
        if (!file.exists()) {
            path = "http://192.168.56.1/FBClub/Help/Pictures/" + k.getKode() + ".bmp";
            file = new File(path);
        }
        if (file.exists()) {
            Glide.with(context)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
        }

        return convertView;
    }
}