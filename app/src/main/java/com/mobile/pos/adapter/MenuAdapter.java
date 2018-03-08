package com.mobile.pos.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.pos.Constant;
import com.mobile.pos.MejaActivity;
import com.mobile.pos.R;
import com.mobile.pos.model.Menu;
import com.mobile.pos.model.Order;
import com.mobile.pos.sql.Query;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Menu> list;
    private String date;
    private OrderAdapter orderAdapter;
    private Query query;
    private Button button;

    public MenuAdapter(Context context, ArrayList<Menu> list, String date, OrderAdapter orderAdapter, Query query, Button button) {
        this.context = context;
        this.list = list;
        this.date = date;
        this.orderAdapter = orderAdapter;
        this.query = query;
        this.button = button;
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
            convertView = inflater.inflate(R.layout.list_menu, null);
        final Menu m = list.get(position);
        DecimalFormat format = new DecimalFormat();
        TextView teks = (TextView) convertView.findViewById(R.id.nama);
        teks.setText(m.getNama());
        TextView teksHarga = (TextView) convertView.findViewById(R.id.harga);
        teksHarga.setText(format.format(m.getHarga()));
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        /*String path = "http://" + Constant.ip + "/FBClub/Help/Pictures/" + m.getKode() + ".jpg";
        try {
            InputStream is = (InputStream) new URL(path).getContent();
            Glide.with(context)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
        } catch (Exception e) {
            path = "http://" + Constant.ip + "/FBClub/Help/Pictures/" + m.getKode() + ".bmp";
            try {
                InputStream is = (InputStream) new URL(path).getContent();
                Glide.with(context)
                        .load(path)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
            } catch (Exception ex) {
            }
        }*/
        String user = "";
        String pass = "";

        String url = "smb://" + Constant.ip + "/FBClub/Help/Pictures/" + m.getKode() + ".jpg";
        try{
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, pass);
            SmbFile sfile = new SmbFile(url, auth);
            Glide.with(context)
                    .load("http://" + Constant.ip + sfile.getURL().getFile())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
        }catch(Exception e){
            url = "smb://" + Constant.ip + "/FBClub/Help/Pictures/" + m.getKode() + ".bmp";
            try {
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, pass);
                SmbFile sfile = new SmbFile(url, auth);
                Glide.with(context)
                        .load("http://" + Constant.ip + sfile.getURL().getFile())
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
            } catch (Exception ex) {
            }
        }
        final TextView teksQty = (TextView) convertView.findViewById(R.id.qty);
        teksQty.setText("1");
        ImageView btn = (ImageView) convertView.findViewById(R.id.btn);
        final Button order = (Button) convertView.findViewById(R.id.order);
        final Button min = (Button) convertView.findViewById(R.id.min);
        final Button pos = (Button) convertView.findViewById(R.id.pos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setTitle("KETERANGAN");
                dialog.setContentView(R.layout.keterangan);
                final EditText teksKeterangan = (EditText) dialog.findViewById(R.id.keterangan);
                Button ok = (Button) dialog.findViewById(R.id.ok);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                teksKeterangan.setText(m.getKeterangan());
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String keterangan = teksKeterangan.getText().toString();
                        m.setKeterangan(keterangan);
                        dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                ResultSet rs = query.cekStok(date, m.getKode());
                int foodSales = 0;
                int foodQty = 0;
                boolean cek = true;
                if (rs != null) {
                    try {
                        foodSales = rs.getInt("Food_Sales");
                        foodQty = rs.getInt("Food_Qty");
                    } catch (Exception e) {
                    }
                    if (foodSales >= foodQty) {
                        SweetAlertDialog alert = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("PESAN KESALAHAN")
                                .setContentText("SOLD OUT");
                        alert.setCancelable(false);
                        alert.show();
                        cek = false;
                    } else {
                        if (Integer.parseInt(teksQty.getText().toString()) > foodQty - foodSales) {
                            SweetAlertDialog alert = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("PESAN KESALAHAN")
                                    .setContentText("SISA YANG BISA DIORDER " + (foodQty - foodSales));
                            alert.setCancelable(false);
                            alert.show();
                            cek = false;
                        }
                    }
                }
                if (cek) {
                    Order o = new Order();
                    o.setKode(m.getKode());
                    o.setNama(m.getNama());
                    o.setKeterangan(m.getKeterangan());
                    o.setHarga(m.getHarga());
                    o.setUom(m.getUom());
                    o.setWh(m.getWh());
                    o.setPrintCode(m.getPrintCode());
                    o.setQty(Integer.parseInt(teksQty.getText().toString()));
                    orderAdapter.getList().add(o);
                    orderAdapter.notifyDataSetChanged();
                    teksQty.setText("1");
                    m.setKeterangan("");
                    notifyDataSetChanged();
                    button.setEnabled(true);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        order.setBackgroundResource(R.drawable.button_order);
                    }
                }, 300);
            }
        });
        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                min.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                int qty = Integer.parseInt(teksQty.getText().toString());
                if (qty > 1) {
                    qty--;
                    teksQty.setText(String.valueOf(qty));
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        min.setBackgroundResource(R.drawable.button_min);
                    }
                }, 300);
            }
        });
        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                int qty = Integer.parseInt(teksQty.getText().toString());
                qty++;
                teksQty.setText(String.valueOf(qty));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pos.setBackgroundResource(R.drawable.button_plus);
                    }
                }, 300);
            }
        });

        return convertView;
    }
}
