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
import android.widget.Toast;

import com.mobile.pos.OrderActivity;
import com.mobile.pos.R;
import com.mobile.pos.model.Order;
import com.mobile.pos.view.OnSwipeTouchListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Order> list;
    private Button button;

    public OrderAdapter(Context context, ArrayList<Order> list, Button button) {
        this.context = context;
        this.list = list;
        this.button = button;
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
            convertView = inflater.inflate(R.layout.list_order, null);
        final Order o = list.get(position);
        DecimalFormat format = new DecimalFormat();
        TextView teks = (TextView) convertView.findViewById(R.id.nama);
        teks.setText(o.getNama());
        TextView teksHarga = (TextView) convertView.findViewById(R.id.harga);
        teksHarga.setText(format.format(o.getHarga()));
        TextView teksQty = (TextView) convertView.findViewById(R.id.qty);
        teksQty.setText(String.valueOf(o.getQty()));
        TextView teksKeterangan = (TextView) convertView.findViewById(R.id.keterangan);
        teksKeterangan.setText(o.getKeterangan());
        final Button delete = (Button) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog alert = new SweetAlertDialog(context)
                        .setTitleText("HAPUS PESANAN")
                        .setContentText("Apakah pesanan ini akan dihapus?")
                        .setCancelText("TIDAK")
                        .setConfirmText("YA")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                delete.setVisibility(View.GONE);
                                list.remove(position);
                                notifyDataSetChanged();
                                if (list.size() == 0) {
                                    button.setEnabled(false);
                                }
                                sDialog.cancel();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        });
                alert.setCancelable(false);
                alert.show();
            }
        });

        return convertView;
    }
}
