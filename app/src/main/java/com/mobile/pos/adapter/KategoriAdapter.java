package com.mobile.pos.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.pos.Constant;
import com.mobile.pos.R;
import com.mobile.pos.model.Kategori;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

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
        String user = "";
        String pass = "";
        String url = "smb://" + Constant.ip + "/FBClub/Help/Pictures/" + k.getKode() + ".jpg";
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, pass);
            SmbFile sfile = new SmbFile(url, auth);
            Glide.with(context)
                    .load("http://" + Constant.ip + sfile.getURL().getFile())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
            teks.setTextColor(context.getResources().getColor(android.R.color.white));
        } catch (Exception e) {
            url = "smb://" + Constant.ip + "/FBClub/Help/Pictures/" + k.getKode() + ".bmp";
            try {
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, pass);
                SmbFile sfile = new SmbFile(url, auth);
                Glide.with(context)
                        .load("http://" + Constant.ip + sfile.getURL().getFile())
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
                teks.setTextColor(context.getResources().getColor(android.R.color.white));
            } catch (Exception ex) {
                teks.setTextColor(context.getResources().getColor(android.R.color.black));
            }
        }
        return convertView;
    }
}
