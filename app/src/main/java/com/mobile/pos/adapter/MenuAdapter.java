package com.mobile.pos.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.pos.OrderActivity;
import com.mobile.pos.R;
import com.mobile.pos.model.Menu;
import com.mobile.pos.model.Order;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Menu> list;

    public MenuAdapter(Context context, ArrayList<Menu> list) {
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
            convertView = inflater.inflate(R.layout.list_menu, null);
        final Menu m = list.get(position);
        DecimalFormat format = new DecimalFormat();
        TextView teks = (TextView) convertView.findViewById(R.id.nama);
        teks.setText(m.getNama());
        TextView teksHarga = (TextView) convertView.findViewById(R.id.harga);
        teksHarga.setText(format.format(m.getHarga()));
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        String path = "http://192.168.56.1/FBClub/Help/Pictures/" + m.getKode() + ".jpg";
        File file = new File(path);
        if (!file.exists()) {
            path = "http://192.168.56.1/FBClub/Help/Pictures/" + m.getKode() + ".bmp";
            file = new File(path);
        }
        if (file.exists()) {
            Glide.with(context)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
        }
        final TextView teksQty = (TextView) convertView.findViewById(R.id.qty);
        Button btn = (Button) convertView.findViewById(R.id.btn);
        Button order = (Button) convertView.findViewById(R.id.order);
        Button min = (Button) convertView.findViewById(R.id.min);
        Button pos = (Button) convertView.findViewById(R.id.pos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setTitle("KETERANGAN");
                dialog.setContentView(R.layout.keterangan);
                final EditText teksKeterangan = (EditText) dialog.findViewById(R.id.keterangan);
                Button ok = (Button) dialog.findViewById(R.id.ok);
                teksKeterangan.setText(m.getKeterangan());
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String keterangan = teksKeterangan.getText().toString();
                        m.setKeterangan(keterangan);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((OrderActivity)context).getOrder(m.getKode()) != null) {
                    Order o = ((OrderActivity)context).getOrder(m.getKode());
                    o.setKeterangan(m.getKeterangan());
                    o.setQty(Integer.parseInt(teksQty.getText().toString()));
                } else {
                    Order o = new Order();
                    o.setKode(m.getKode());
                    o.setNama(m.getNama());
                    o.setKeterangan(m.getKeterangan());
                    o.setHarga(m.getHarga());
                    o.setQty(Integer.parseInt(teksQty.getText().toString()));
                    ((OrderActivity) context).getListOrder().add(o);
                }
                ((OrderActivity)context).getOrderAdapter().notifyDataSetChanged();
            }
        });
        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(teksQty.getText().toString());
                if (qty > 0) {
                    qty--;
                    teksQty.setText(String.valueOf(qty));
                }
            }
        });
        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(teksQty.getText().toString());
                qty++;
                teksQty.setText(String.valueOf(qty));
            }
        });

        return convertView;
    }
}
