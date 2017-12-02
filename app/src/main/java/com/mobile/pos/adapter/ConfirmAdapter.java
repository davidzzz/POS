package com.mobile.pos.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mobile.pos.R;
import com.mobile.pos.model.Order;
import com.mobile.pos.view.OnSwipeTouchListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ConfirmAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Order> list;

    public ConfirmAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.list = list;
    }

    public ArrayList<Order> getList() { return list; }

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
            convertView = inflater.inflate(R.layout.list_confirm, null);
        final Order o = list.get(position);
        DecimalFormat format = new DecimalFormat();
        TextView teks = (TextView) convertView.findViewById(R.id.nama);
        teks.setText(o.getNama());
        TextView teksHarga = (TextView) convertView.findViewById(R.id.harga);
        teksHarga.setText(format.format(o.getHarga()));
        TextView teksSubtotal = (TextView) convertView.findViewById(R.id.subtotal);
        teksSubtotal.setText(format.format(o.getSubtotal()));
        TextView teksQty = (TextView) convertView.findViewById(R.id.qty);
        teksQty.setText(String.valueOf(o.getQty()));
        TextView teksKeterangan = (TextView) convertView.findViewById(R.id.keterangan);
        if (o.getKeterangan() == null) {
            teksKeterangan.setVisibility(View.GONE);
        } else {
            teksKeterangan.setVisibility(View.VISIBLE);
            teksKeterangan.setText(o.getKeterangan());
        }

        return convertView;
    }
}
